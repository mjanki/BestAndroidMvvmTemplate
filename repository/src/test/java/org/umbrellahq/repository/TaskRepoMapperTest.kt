package org.umbrellahq.repository

import org.junit.Assert
import org.junit.Test
import org.threeten.bp.OffsetDateTime
import org.umbrellahq.database.model.TaskDatabaseEntity
import org.umbrellahq.network.model.TaskNetworkEntity
import org.umbrellahq.repository.mappers.TaskRepoDatabaseMapper
import org.umbrellahq.repository.mappers.TaskRepoNetworkMapper
import org.umbrellahq.repository.model.TaskRepoEntity

class TaskRepoMapperTest {

    // Task Network
    @Test
    fun testTaskRepoNetworkMapperDownstream() {
        val mapper = TaskRepoNetworkMapper()
        val mappedValue = mapper.downstream(
                TaskRepoEntity(
                        id = 51L,
                        name = "MOCK ENTITY",
                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                        status = 1
                )
        )

        Assert.assertEquals("51", mappedValue.uuid)
        Assert.assertEquals("2007-12-23T10:15:30+01:00", mappedValue.date)
    }

    @Test
    fun testTaskRepoNetworkMapperUpstream() {
        val mapper = TaskRepoNetworkMapper()
        val mappedValue = mapper.upstream(
                TaskNetworkEntity(
                        uuid = "51",
                        name = "MOCK ENTITY",
                        date = "2007-12-23T10:15:30+01:00",
                        status = 1
                )
        )

        Assert.assertEquals(51L, mappedValue.id)
        Assert.assertEquals(OffsetDateTime.parse("2007-12-23T10:15:30+01:00"), mappedValue.date)
    }

    // Task Database
    @Test
    fun testTaskRepoDatabaseMapperDownstream() {
        val mapper = TaskRepoDatabaseMapper()
        val mappedValue = mapper.downstream(
                TaskRepoEntity(
                        id = 51L,
                        name = "MOCK ENTITY",
                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                        status = 1
                )
        )

        Assert.assertEquals(51L, mappedValue.id)
        Assert.assertEquals(OffsetDateTime.parse("2007-12-23T10:15:30+01:00"), mappedValue.date)
    }

    @Test
    fun testTaskRepoDatabaseMapperUpstream() {
        val mapper = TaskRepoDatabaseMapper()
        val mappedValue = mapper.upstream(
                TaskDatabaseEntity(
                        id = 51L,
                        name = "MOCK ENTITY",
                        date = OffsetDateTime.parse("2007-12-23T10:15:30+01:00"),
                        status = 1
                )
        )

        Assert.assertEquals(51L, mappedValue.id)
        Assert.assertEquals(OffsetDateTime.parse("2007-12-23T10:15:30+01:00"), mappedValue.date)
    }
}