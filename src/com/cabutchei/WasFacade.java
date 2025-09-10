package com.cabutchei;



import javax.management.ObjectName;
import javax.management.NotificationListener;
import javax.management.InstanceNotFoundException;
import javax.management.NotificationFilter;
import javax.management.MalformedObjectNameException;


import java.io.File;

import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cabutchei.notification.Subscription;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.exception.ConnectorException;

import org.eclipse.emf.common.util.URI;


public class WasFacade implements AutoCloseable {

    private String cellName;
    private String nodeName;
    private String serverName;
    private String host;
    private int port;

    private volatile AdminClient adminClient;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean healthy = new AtomicBoolean(false);
    private final Object connectLock = new Object();

    private static final class Reg {
        final ObjectName mbean;
        final NotificationListener listener;
        final NotificationFilter filter;
        Reg(ObjectName mbean, NotificationListener listener, NotificationFilter filter) {
            this.mbean = mbean;
            this.filter = filter;
            this.listener = listener;
        }
    }

    private final Map<String, Reg> listenerRegistry = new ConcurrentHashMap<>();

    public WasFacade(String host, int port, String cellName, String nodeName, String serverName) {
        this.cellName = cellName;
        this.nodeName = nodeName;
        this.serverName = serverName;
        this.host = host;
        this.port = port;

    }

    public WasFacade(String host, String port, String cellName, String nodeName, String serverName) {
        this(host, Integer.parseInt(port), cellName, nodeName, serverName);

    }

    public void ensureConnected() {
        if (connected.get() && healthy.get() && this.adminClient != null) return;
        synchronized (connectLock) {
            if (connected.get() && healthy.get() && this.adminClient != null) return;

            try {
                this.adminClient = createAdminClient(2 * 60 * 1000, 500);
                this.connected.set(true);

                probe();
                this.healthy.set(true);

                reRegisterAll();

            } catch (Exception e) {
                System.out.println("something went wrong");
                e.printStackTrace();
            }
        }
    }

    public boolean isHealthy() {
        if (healthy.get() && connected.get() && this.adminClient != null) return true;

        try {
            ensureConnected();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
        
    }

    public Subscription addListener(ObjectName mbean, NotificationListener listener, NotificationFilter filter) throws InstanceNotFoundException {
        String id = UUID.randomUUID().toString();
        Reg reg = new Reg(mbean, listener, filter);
        this.listenerRegistry.put(id, reg);

        try {
            adminClient.addNotificationListener(mbean, listener, filter, null);
        } catch (Exception e) {
            listenerRegistry.remove(id);
            markUnhealthy();
            throw new RuntimeException("Failed to add JMX listener for " + mbean, e);
        }

        return () -> removeListenerById(id);
    } 

    public void addServerListener(NotificationListener listener, NotificationFilter filter) throws InstanceNotFoundException, ConnectorException {
        ObjectName serverMBean = this.adminClient.getServerMBean();
        addListener(serverMBean, listener, filter);
    }

    public void addServerlistener(NotificationListener listener) throws InstanceNotFoundException, ConnectorException {
        addServerListener(listener, null);
    }

    public void addAppManagementServerListener(NotificationListener listener, NotificationFilter filter) throws InstanceNotFoundException, ConnectorException, MalformedObjectNameException {
        Set<ObjectName> result  = (Set<ObjectName>) adminClient.queryNames(new ObjectName("WebSphere:*,j2eeType=J2EEApplication,name=" + "silce"), null);
        if (result.isEmpty()) {
            throw new InstanceNotFoundException("No AppManagement MBean found");
        }
        ObjectName appMBean = result.iterator().next();
        this.adminClient.addNotificationListener(appMBean, listener, filter, null);
    }

    public AdminClient createAdminClient(long timeoutMs, long retryIntervalMs) throws Exception {
        long startTime = System.currentTimeMillis();
        Properties connectProps = new Properties();
        connectProps.setProperty(AdminClient.CONNECTOR_TYPE,AdminClient.CONNECTOR_TYPE_SOAP);
        connectProps.setProperty(AdminClient.CONNECTOR_HOST, this.host);
        connectProps.setProperty(AdminClient.CONNECTOR_PORT, Integer.toString(port));
        while (true) {
            try {
                AdminClient adminClient = AdminClientFactory.createAdminClient(connectProps);
                if (adminClient == null) {
                    throw new ConnectorException("AdminClient creation returned null");
                }
                System.out.println("Successfully connected to the WebSphere server.");
                return adminClient; // Connection successful; exit loop.
            } catch (ConnectorException e) {
                if (System.currentTimeMillis() - startTime > timeoutMs) {
                    System.out.println("Error: Timed out attempting to create admin client");
                    throw new Exception("Timed out connecting to WebSphere server", e);
                }
                Thread.sleep(retryIntervalMs);
            }
        }
    }

    public void probe() {
        try {
            this.adminClient.queryNames(new ObjectName("WebSphere>*"), null);
        } catch (Exception e) {
            markUnhealthy();
            throw new RuntimeException("WAS JMX probe failed", e);
        }
    }

    private void reRegisterAll() {
        for (Map.Entry<String, Reg> entry : listenerRegistry.entrySet()) {
            Reg reg = entry.getValue();
            try {
                this.adminClient.addNotificationListener(reg.mbean, reg.listener, reg.filter, null);
            } catch (Exception e) {
                markUnhealthy();
            }
        }
    }

    private void removeListenerById(String id) {
        Reg reg = listenerRegistry.remove(id);
        if (reg == null) return;
        try {
            this.adminClient.removeNotificationListener(reg.mbean, reg.listener);
        } catch (Exception e) {
        }
    }

    public Boolean isConnected() {
        return this.connected.get();
    }

    public void markUnhealthy() {
        this.healthy.set(false);
    }

    public void close() {}

    public void connect(long timeoutMs, long retryIntervalMs) throws Exception {
        long startTime = System.currentTimeMillis();
        Properties connectProps = new Properties();
        connectProps.setProperty(AdminClient.CONNECTOR_TYPE, "SOAP");
        connectProps.setProperty(AdminClient.CONNECTOR_HOST, this.host);
        connectProps.setProperty(AdminClient.CONNECTOR_PORT, Integer.toString(port));
        while (true) {
            try {
                this.adminClient = AdminClientFactory.createAdminClient(connectProps);
                if (adminClient == null) {
                    throw new ConnectorException("AdminClient creation returned null");
                }
                System.out.println("Successfully connected to the WebSphere server.");
                break; // Connection successful; exit loop.
            } catch (ConnectorException e) {
                if (System.currentTimeMillis() - startTime > timeoutMs) {
                    System.out.println("Error: Timed out attempting to create admin client");
                    throw new Exception("Timed out connecting to WebSphere server", e);
                }
                System.out.println("Connection failed, retrying in " + retryIntervalMs + "ms...");
                Thread.sleep(retryIntervalMs);
            }
        }
    }


    public void subscribeToNotifications(NotificationListener listener) throws Exception {
        Map<String, String> queries = Map.of(
            "server", "WebSphere:cell=" + cellName + ",node=" + nodeName + ",process=" + serverName + ",type=Server,*",
            "application", "WebSphere:*,j2eeType=J2EEApplication,name=silce"
        );
        try {
            // Query for the server MBean
            String query = queries.get("server");
            Set<ObjectName> result = adminClient.queryNames(new ObjectName(query), null);
            if (result.isEmpty()) {
                System.out.println("No server MBeans found for query: " + query);
                return;
            }
            ObjectName serverObject = result.iterator().next();

            // // Query for the application MBean
            // query = queries.get("application");
            // result = adminClient.queryNames(new ObjectName(query), null);
            // if (result.isEmpty()) {
            //     System.out.println("No application MBeans found for query: " + query);
            //     return;
            // }
            // ObjectName appObject = result.iterator().next();

            adminClient.addNotificationListener(serverObject, listener, null, null);
            // adminClient.addNotificationListener(appObject, listener, null, null);
        } catch (MalformedObjectNameException e) {
            System.out.println("Malformed object name: " + e.getMessage());
        } catch (ConnectorException e) {
            System.out.println("Connector exception: " + e.getMessage());
        }
    }

    public Object invokeCommand(String mbeanQuery, String command, Object[] params, String[] signature) throws Exception {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to WAS server");
        }
        try {
            ObjectName objectName = new ObjectName(mbeanQuery);
            return adminClient.invoke(objectName, command, params, signature);
        } catch (MalformedObjectNameException e) {
            System.out.println("Malformed object name: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error invoking command: " + e.getMessage());
            throw e;
        }
    }

    public void startServer(String serverId) throws Exception {
        String mbeanQuery = "WebSphere:cell=" + cellName + ",node=" + nodeName + ",process=" + serverName + ",type=Server,*";
        Set<ObjectName> result = adminClient.queryNames(new ObjectName(mbeanQuery), null);
        if (result.isEmpty()) {
            System.out.println("No server MBeans found for query: " + mbeanQuery);
        }
        ObjectName serevrObject = result.iterator().next();
        this.adminClient.invoke(serevrObject, "start", null, null);
        // invokeCommand(mbeanQuery, "start", new Object[]{}, new String[]{});
    }

    public void stopServer(String serverId) throws Exception {
        ObjectName serverObject = this.adminClient.getServerMBean();
        this.adminClient.invoke(serverObject, "stop", null, null);
    }

    public Integer getPid(String serverName) throws Exception {
        ObjectName serverObject;
        try {
            serverObject = this.adminClient.getServerMBean();
            Object r = this.adminClient.invoke(serverObject, "getPid", null, null);
            int pid = Integer.valueOf((String) r);
            return pid;
        } catch (Exception e) {
            return null;
        }
    }

    public void installApplication(String appPath, String looseConfigPath) throws Exception {

        appPath = "C:\\Desenvolvimento\\workspaces\\was-experiments-app-srv03\\WebAppEAR";
        looseConfigPath = "C:/Desenvolviment/workspaces/was-experiments-appsrv03/.metadata/.plugins/com.ibm.etools.wrd.websphere.v85/looseconfigurations/WebSphereApplicationServertraditionalv8.5atlocalhost/WebAppEAR/looseconfig.xmi";
        File applicationPath = new File(appPath);
        File looseConfigFile = new File(looseConfigPath);
        //need to be a URI
        URI looseConfigUri = URI.createFileURI(looseConfigFile.getAbsolutePath());

        AppManagement appManagement = AppManagementProxy.getJMXProxyForClient(this.adminClient);
        Hashtable<String, Object> options = new Hashtable<>();
        // these two apparently prevent the ear sructure (and the ear file) fro being copied to the applications folder
        options.put(AppConstants.APPDEPL_ZERO_BINARY_COPY, Boolean.TRUE);
        options.put(AppConstants.APPDEPL_USE_BINARY_CONFIG, Boolean.TRUE);
        options.put(AppConstants.APPDEPL_DISTRIBUTE_APP, Boolean.FALSE);

        // don't know why or if I need this
        options.put("com.ibm.websphere.application.migration.disabled", "true");
        options.put(AppConstants.LOOSE_CONFIG_PROPERTY, looseConfigUri.toString());
        options.put(AppConstants.APPDEPL_RELOADENABLED, Boolean.TRUE);
        options.put(AppConstants.APPDEPL_INSTALL_DIR_FINAL, applicationPath.getAbsoluteFile());
        options.put(AppConstants.APPDEPL_INSTALL_DIR, applicationPath.getCanonicalPath());

        Hashtable<String, String> moduleToServerMapping = new Hashtable<>();

        // wildcard for all modules and target server
        moduleToServerMapping.put("*", " WebSphere:cell=" + cellName + ",node=" + nodeName + ",server=" + serverName);
        options.put(AppConstants.APPDEPL_MODULE_TO_SERVER, moduleToServerMapping);

        appManagement.installApplication(applicationPath.getAbsolutePath(), options, null);
    }

}