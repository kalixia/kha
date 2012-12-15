package com.kalixia.ha.model.capabilities;

import com.kalixia.ha.model.Capability;

/**
 * A switch can have two states: {@link Status#OPENED} or {@link Status#CLOSED}.
 */
public interface Switch extends Capability {
    void open();
    void close();
    Status getStatus();

    public enum Status { OPENED, CLOSED }
}
