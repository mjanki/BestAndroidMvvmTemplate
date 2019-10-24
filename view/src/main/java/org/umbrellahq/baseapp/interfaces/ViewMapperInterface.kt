package org.umbrellahq.baseapp.interfaces

interface ViewMapperInterface<T, V> {
    fun downstream(currentLayerEntity: T): V
    fun upstream(nextLayerEntity: V): T
}