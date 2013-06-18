package com.kalixia.ha.model;

import org.joda.time.DateTime;

public interface Auditable {
    DateTime getCreationDate();
    DateTime getLastUpdateDate();
    Auditable setLastUpdateDate(DateTime modificationDate);
}
