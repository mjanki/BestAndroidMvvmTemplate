package org.umbrellahq.repository.interfaces

interface MapperInterface<T, V> {
    fun downstream(currentLayerEntity: T): V
    fun upstream(nextLayerEntity: V): T
}