package com.kalixia.ha.model;

import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractAuditable implements Auditable {
    private final DateTime creationDate;
    private DateTime lastUpdateDate;

    protected AbstractAuditable() {
        this(new DateTime());
    }

    public AbstractAuditable(DateTime creationDate) {
        this(creationDate, creationDate);
    }

    public AbstractAuditable(DateTime creationDate, DateTime lastUpdateDate) {
        checkNotNull(creationDate, "The creation date can't be null");
        checkNotNull(lastUpdateDate, "The last update date can't be null");
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public DateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    @Override
    public Auditable setLastUpdateDate(DateTime modificationDate) {
        this.lastUpdateDate = modificationDate;
        return this;
    }
}
