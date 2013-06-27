package com.kalixia.ha.dao.cassandra

import com.netflix.astyanax.annotations.Component

class UserProperty {
    @Component(ordinal = 0)
    private String type

    @Component(ordinal = 1)
    private String property

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        UserProperty that = (UserProperty) o

        if (property != that.property) return false
        if (type != that.type) return false

        return true
    }

    int hashCode() {
        int result
        result = (type != null ? type.hashCode() : 0)
        result = 31 * result + (property != null ? property.hashCode() : 0)
        return result
    }
}
