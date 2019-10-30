package org.umbrellahq.repository

import org.junit.Assert
import org.junit.Test
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity
import org.umbrellahq.network.models.ErrorNetworkEntity
import org.umbrellahq.repository.mappers.ErrorNetworkRepoDatabaseMapper
import org.umbrellahq.repository.mappers.ErrorNetworkRepoNetworkMapper
import org.umbrellahq.repository.models.ErrorNetworkRepoEntity
import org.umbrellahq.util.enums.ErrorNetworkTypes

class ErrorNetworkRepoMapperTest {
    // Mock Entities
    private var testErrorNetworkRepoEntity = ErrorNetworkRepoEntity(
            id = 51L,
            type = ErrorNetworkTypes.HTTP,
            action = "TESTING"
    )

    private var testErrorNetworkEntity = ErrorNetworkEntity(
            type = ErrorNetworkTypes.HTTP,
            action = "TESTING"
    )

    private var testErrorNetworkDatabaseEntity = ErrorNetworkDatabaseEntity(
            id = 51L,
            type = ErrorNetworkTypes.HTTP,
            action = "TESTING"
    )

    // ErrorNetworkRepoNetworkMapper Tests
    @Test
    fun errorNetworkRepoNetworkMapperDownstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoNetworkMapper()
        val mappedValue = mapper.downstream(testErrorNetworkRepoEntity)

        Assert.assertEquals(testErrorNetworkRepoEntity.type, mappedValue.type)
        Assert.assertEquals(testErrorNetworkRepoEntity.action, mappedValue.action)
    }

    @Test
    fun errorNetworkRepoNetworkMapperUpstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoNetworkMapper()
        val mappedValue = mapper.upstream(testErrorNetworkEntity)

        Assert.assertEquals(testErrorNetworkEntity.type, mappedValue.type)
        Assert.assertEquals(testErrorNetworkEntity.action, mappedValue.action)
    }

    // ErrorNetworkRepoDatabaseMapper Tests
    @Test
    fun errorNetworkRepoDatabaseMapperDownstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoDatabaseMapper()
        val mappedValue = mapper.downstream(testErrorNetworkRepoEntity)

        Assert.assertEquals(testErrorNetworkRepoEntity.id, mappedValue.id)
        Assert.assertEquals(testErrorNetworkRepoEntity.type, mappedValue.type)
        Assert.assertEquals(testErrorNetworkRepoEntity.action, mappedValue.action)
    }

    @Test
    fun errorNetworkRepoDatabaseMapperUpstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoDatabaseMapper()
        val mappedValue = mapper.upstream(testErrorNetworkDatabaseEntity)

        Assert.assertEquals(testErrorNetworkDatabaseEntity.type, mappedValue.type)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.action, mappedValue.action)
    }
}