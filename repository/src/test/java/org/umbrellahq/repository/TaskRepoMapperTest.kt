package org.umbrellahq.repository

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.OffsetDateTime
import org.umbrellahq.database.models.TaskDatabaseEntity
import org.umbrellahq.network.models.TaskNetworkEntity
import org.umbrellahq.repository.mappers.TaskRepoDatabaseMapper
import org.umbrellahq.repository.mappers.TaskRepoNetworkMapper
import org.umbrellahq.repository.models.TaskRepoEntity

class TaskRepoMapperTest {

    // TaskRepoNetworkMapper Tests
    @Test
    fun taskRepoNetworkMapperDownstream_shouldMapCorrectly() {
        val mapper = TaskRepoNetworkMapper()
        val mappedValue = mapper.downstream(
                TaskRepoEntity(
                        id = 51L,
                        uuid = "UUID Test",
                        name = "MOCK ENTITY",
                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                        status = 1
                )
        )

        Assert.assertEquals("UUID Test", mappedValue.uuid)
        Assert.assertEquals("2007-12-23T10:15:30+01:00", mappedValue.date)
    }

    @Test
    fun taskRepoNetworkMapperUpstream_shouldMapCorrectly() {
        val mapper = TaskRepoNetworkMapper()
        val mappedValue = mapper.upstream(
                TaskNetworkEntity(
                        uuid = "UUID Test",
                        name = "MOCK ENTITY",
                        date = "2007-12-23T10:15:30+01:00",
                        status = 1
                )
        )

        Assert.assertEquals(null, mappedValue.id)
        Assert.assertEquals("UUID Test", mappedValue.uuid)
        Assert.assertEquals(OffsetDateTime.parse("2007-12-23T10:15:30+01:00"), mappedValue.date)
    }

    // TaskRepoDatabaseMapper Tests
    @Test
    fun taskRepoDatabaseMapperDownstream_shouldMapCorrectly() {
        val mapper = TaskRepoDatabaseMapper()
        val mappedValue = mapper.downstream(
                TaskRepoEntity(
                        id = 51L,
                        uuid = "UUID Test",
                        name = "MOCK ENTITY",
                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                        status = 1
                )
        )

        Assert.assertEquals(51L, mappedValue.id)
        Assert.assertEquals(OffsetDateTime.parse("2007-12-23T10:15:30+01:00"), mappedValue.date)
    }

    @Test
    fun taskRepoDatabaseMapperUpstream_shouldMapCorrectly() {
        val mapper = TaskRepoDatabaseMapper()
        val mappedValue = mapper.upstream(
                TaskDatabaseEntity(
                        id = 51L,
                        uuid = "UUID Test",
                        name = "MOCK ENTITY",
                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                        status = 1
                )
        )

        Assert.assertEquals(51L, mappedValue.id)
        Assert.assertEquals(OffsetDateTime.parse("2007-12-23T10:15:30+01:00"), mappedValue.date)
    }
}