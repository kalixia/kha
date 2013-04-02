package com.kalixia.ha.model.capabilities;

/**
 * A switch can have two states: {@link Status#ON} or {@link Status#OFF}.
 */
public interface Switch extends Capability {
    void on();
    void off();
    Switch.Status getStatus();

    public enum Status {ON, OFF}
}
