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

    // Task Network
    @Test
    fun testErrorNetworkRepoNetworkMapperDownstream() {
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
    fun testErrorNetworkRepoNetworkMapperUpstream() {
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

    // Task Database
    @Test
    fun testErrorNetworkRepoDatabaseMapperDownstream() {
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
    fun testErrorNetworkRepoDatabaseMapperUpstream() {
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