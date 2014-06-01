package com.kalixia.ha.api;

import com.kalixia.ha.model.User;

public interface InstallationService {

    /**
     * Check if this is the time to setup the Hub or if it has already been done.
     * Right now the setup is done when there is at least one user.
     * @return true if the setup has already been done
     */
    boolean isSetupDone();

    /**
     * Proceed to the installation for the specified <tt>user</tt>.
     * @param user to user to whom the install belongs to
     */
    User installFor(User user);
}
