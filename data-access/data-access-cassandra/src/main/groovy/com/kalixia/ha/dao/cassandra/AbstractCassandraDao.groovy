package com.kalixia.ha.dao.cassandra

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException
import com.netflix.astyanax.model.ColumnList

abstract class AbstractCassandraDao<T, K, V> {
    abstract protected T buildFromColumnList(K rowKey, ColumnList<V> result) throws ConnectionException
}
