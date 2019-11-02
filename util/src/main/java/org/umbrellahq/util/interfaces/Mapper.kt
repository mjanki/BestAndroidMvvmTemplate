package org.umbrellahq.util.interfaces

interface Mapper<T, V> {
    fun downstream(currentLayerEntity: T): V
    fun upstream(nextLayerEntity: V): T
}