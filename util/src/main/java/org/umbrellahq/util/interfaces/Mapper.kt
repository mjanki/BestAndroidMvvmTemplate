package org.umbrellahq.util.interfaces

interface Mapper<T, V> {
    // Map from current layer entity to specific layer below entity
    fun downstream(currentLayerEntity: T): V

    // Map from specific layer below entity to current layer entity
    fun upstream(nextLayerEntity: V): T
}