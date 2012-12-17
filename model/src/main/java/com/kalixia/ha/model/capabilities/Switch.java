package com.kalixia.ha.model.capabilities;

import com.kalixia.ha.model.Capability;

/**
 * A switch can have two states: {@link Status#ON} or {@link Status#OFF}.
 */
public interface Switch extends Capability {
    void on();
    void off();
    Status getStatus();

    public enum Status {ON, OFF}
}
