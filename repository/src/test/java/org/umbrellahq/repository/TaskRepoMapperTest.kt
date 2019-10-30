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
    // Mock Entities
    private var testTaskRepoEntity = TaskRepoEntity(
            id = 51L,
            uuid = "UUID Test",
            name = "MOCK ENTITY",
            date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
            status = 1
    )

    private var testTaskNetworkEntity = TaskNetworkEntity(
            uuid = "UUID Test",
            name = "MOCK ENTITY",
            date = "2007-12-23T10:15:30+01:00",
            status = 1
    )

    private var testTaskDatabaseEntity = TaskDatabaseEntity(
            id = 51L,
            uuid = "UUID Test",
            name = "MOCK ENTITY",
            date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
            status = 1
    )

    // TaskRepoNetworkMapper Tests
    @Test
    fun taskRepoNetworkMapperDownstream_shouldMapCorrectly() {
        val mapper = TaskRepoNetworkMapper()
        val mappedValue = mapper.downstream(testTaskRepoEntity)

        Assert.assertEquals(testTaskRepoEntity.uuid, mappedValue.uuid)
        Assert.assertEquals("${testTaskRepoEntity.date}", mappedValue.date)
    }

    @Test
    fun taskRepoNetworkMapperUpstream_shouldMapCorrectly() {
        val mapper = TaskRepoNetworkMapper()
        val mappedValue = mapper.upstream(testTaskNetworkEntity)

        Assert.assertEquals(null, mappedValue.id)
        Assert.assertEquals(testTaskNetworkEntity.uuid, mappedValue.uuid)
        Assert.assertEquals(OffsetDateTime.parse(testTaskNetworkEntity.date), mappedValue.date)
    }

    // TaskRepoDatabaseMapper Tests
    @Test
    fun taskRepoDatabaseMapperDownstream_shouldMapCorrectly() {
        val mapper = TaskRepoDatabaseMapper()
        val mappedValue = mapper.downstream(testTaskRepoEntity)

        Assert.assertEquals(testTaskRepoEntity.id, mappedValue.id)
        Assert.assertEquals(testTaskRepoEntity.date, mappedValue.date)
    }

    @Test
    fun taskRepoDatabaseMapperUpstream_shouldMapCorrectly() {
        val mapper = TaskRepoDatabaseMapper()
        val mappedValue = mapper.upstream(testTaskDatabaseEntity)

        Assert.assertEquals(testTaskDatabaseEntity.id, mappedValue.id)
        Assert.assertEquals(testTaskDatabaseEntity.date, mappedValue.date)
    }
}