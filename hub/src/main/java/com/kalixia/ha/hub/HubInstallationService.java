package com.kalixia.ha.hub;

import com.kalixia.ha.api.InstallationService;
import com.kalixia.ha.api.UsersService;
import com.kalixia.ha.model.User;
import com.kalixia.ha.model.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class HubInstallationService implements InstallationService {
    private final UsersService usersService;
    private static final Logger LOGGER = LoggerFactory.getLogger(HubInstallationService.class);

    public HubInstallationService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public boolean isSetupDone() {
        return usersService.getUsersCount() > 0;
    }

    @Override
    public User installFor(User user) {
        checkNotNull(user, "User can't be null");
        if (isSetupDone())
            throw new IllegalStateException("Installation has already been done");
        // create admin user
        user.addRole(Role.ADMINISTRATOR);
        User created = usersService.createUser(user);
        LOGGER.info("Created user {}", created);
        return created;
    }
}
