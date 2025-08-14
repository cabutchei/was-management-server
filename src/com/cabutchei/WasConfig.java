package com.cabutchei;


import java.util.List;

import com.ibm.websphere.product.WASDirectory;
import com.ibm.websphere.product.WASProductInfo;
import com.ibm.wsspi.profile.WSProfile;
import com.ibm.wsspi.profile.WSProfileException;
import com.ibm.wsspi.profile.registry.Profile;

public class WasConfig {

    private String installDir;
    public WASDirectory wasDirectory;

    public WasConfig(String installDir) {
        this.installDir = installDir;
        this.wasDirectory = new WASDirectory(installDir);
        System.setProperty("WAS_HOME", installDir);
    } 

    public String getInstallDir() {
        return installDir;
    }

    public WASProductInfo getServerInfo() {
        return this.wasDirectory.getWASProductInfo(wasDirectory.ID_ND);

    }

    public String getProductId() {
        return wasDirectory.ID_ND;
    }

    public String getVersion() {
        return this.wasDirectory.getVersion(wasDirectory.ID_ND);
    }

    public String getName() {
        return this.wasDirectory.getName(wasDirectory.ID_ND);
    }

    public List<Profile> getProfiles() {
        try {
            return (List<Profile>) WSProfile.getProfileList();
        } catch (WSProfileException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getProfileNames() {
        return this.getProfiles().stream().map(Profile::getName).toList();
    }

}
