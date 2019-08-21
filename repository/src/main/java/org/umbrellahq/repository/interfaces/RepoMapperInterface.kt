package org.umbrellahq.repository.interfaces

interface RepoMapperInterface<T, V> {
    fun downstream(currentLayerEntity: T): V
    fun upstream(nextLayerEntity: V): T
}