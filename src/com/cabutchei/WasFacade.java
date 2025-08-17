package com.cabutchei;



import javax.management.ObjectName;
import javax.management.NotificationListener;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.ws.webservices.xml.waswebservices.server;

import javax.management.MalformedObjectNameException;

public class WasFacade {
    private AdminClient adminClient;
    private String cellName;
    private String nodeName;
    private String serverName;
    private String host;
    private int port;
    private Boolean connected = false;

    public WasFacade(String host, String port, String cellName, String nodeName, String serverName) throws Exception {
        this.cellName = cellName;
        this.nodeName = nodeName;
        this.serverName = serverName;

    }

    public WasFacade(String host, int port, String cellName, String nodeName, String serverName) throws Exception {
        this.cellName = cellName;
        this.nodeName = nodeName;
        this.serverName = serverName;
        this.host = host;
        this.port = port;
    }
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

    public Boolean isConnected() {
        return this.connected;
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

}