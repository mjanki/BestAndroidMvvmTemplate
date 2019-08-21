package org.umbrellahq.viewmodel.interfaces

interface ViewModelMapperInterface<T, V> {
    fun downstream(currentLayerEntity: T): V
    fun upstream(nextLayerEntity: V): T
}