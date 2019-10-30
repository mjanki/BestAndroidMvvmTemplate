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

    // ErrorNetworkRepoNetworkMapper Tests
    @Test
    fun errorNetworkRepoNetworkMapperDownstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoNetworkMapper()
        val mappedValue = mapper.downstream(
                ErrorNetworkRepoEntity(
                        id = 51L,
                        type = ErrorNetworkTypes.HTTP,
                        action = "TESTING"
                )
        )

        Assert.assertEquals(ErrorNetworkTypes.HTTP, mappedValue.type)
        Assert.assertEquals("TESTING", mappedValue.action)
    }

    @Test
    fun errorNetworkRepoNetworkMapperUpstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoNetworkMapper()
        val mappedValue = mapper.upstream(
                ErrorNetworkEntity(
                        type = ErrorNetworkTypes.HTTP,
                        action = "TESTING"
                )
        )

        Assert.assertEquals(ErrorNetworkTypes.HTTP, mappedValue.type)
        Assert.assertEquals("TESTING", mappedValue.action)
    }

    // ErrorNetworkRepoDatabaseMapper Tests
    @Test
    fun errorNetworkRepoDatabaseMapperDownstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoDatabaseMapper()
        val mappedValue = mapper.downstream(
                ErrorNetworkRepoEntity(
                        id = 51L,
                        type = ErrorNetworkTypes.HTTP,
                        action = "TESTING"
                )
        )

        Assert.assertEquals(51L, mappedValue.id)
        Assert.assertEquals(ErrorNetworkTypes.HTTP, mappedValue.type)
        Assert.assertEquals("TESTING", mappedValue.action)
    }

    @Test
    fun errorNetworkRepoDatabaseMapperUpstream_shouldMapCorrectly() {
        val mapper = ErrorNetworkRepoDatabaseMapper()
        val mappedValue = mapper.upstream(
                ErrorNetworkDatabaseEntity(
                        id = 51L,
                        type = ErrorNetworkTypes.HTTP,
                        action = "TESTING"
                )
        )

        Assert.assertEquals(ErrorNetworkTypes.HTTP, mappedValue.type)
        Assert.assertEquals("TESTING", mappedValue.action)
    }
}