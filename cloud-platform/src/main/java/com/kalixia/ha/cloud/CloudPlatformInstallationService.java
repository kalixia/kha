package com.kalixia.ha.cloud;

import com.kalixia.ha.api.InstallationService;
import com.kalixia.ha.model.User;

public class CloudPlatformInstallationService implements InstallationService {
    @Override
    public boolean isSetupDone() {
        return false;   // TODO
    }

    @Override
    public User installFor(User user) {
        return null;    // TODO
    }
}
