package org.umbrellahq.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.umbrellahq.database.daos.TaskDatabaseDao
import org.umbrellahq.database.models.TaskDatabaseEntity
import java.io.IOException
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class TaskDatabaseDaoInstrumentedTest {
    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var taskDatabaseDao: TaskDatabaseDao

    // Mock Entities
    private lateinit var testTaskDatabaseEntity: TaskDatabaseEntity

    @Before
    fun setup() {
        AndroidThreeTen.init(ApplicationProvider.getApplicationContext())

        testTaskDatabaseEntity = getTaskDatabaseEntity()
    }

    @Before
    fun setupDB() {
        // Create test database
        db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
        ).allowMainThreadQueries().build()

        taskDatabaseDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_twoTasks_shouldHaveTwoTasks() = runBlocking(Dispatchers.IO) {
        // Insert 2 tasks
        taskDatabaseDao.insert(testTaskDatabaseEntity)
        taskDatabaseDao.insert(testTaskDatabaseEntity)

        // Get all tasks
        val resultList = taskDatabaseDao.getAll().first()

        // Check we have 2 tasks
        Assert.assertEquals(2, resultList.size)
    }

    @Test
    fun insert_oneTask_shouldHaveSameTask() = runBlocking(Dispatchers.IO) {
        // Insert 1 task
        taskDatabaseDao.insert(testTaskDatabaseEntity)

        // Get all tasks
        val resultList = taskDatabaseDao.getAll().first()

        // Check we have 1 task
        Assert.assertEquals(1, resultList.size)

        // Check that task has the same name
        Assert.assertEquals(testTaskDatabaseEntity.name, resultList[0].name)
    }

    @Test
    fun insert_oneTaskThenUpdate_shouldHaveUpdatedTask() = runBlocking(Dispatchers.IO) {
        // Insert 1 task
        val id = taskDatabaseDao.insert(testTaskDatabaseEntity)

        // Get all tasks
        var resultList = taskDatabaseDao.getAll().first()

        // Check we have 1 task
        Assert.assertEquals(1, resultList.size)

        // Set ID to update the same task, and change name
        val newName = "Eyad"
        resultList[0].id = id
        resultList[0].name = newName

        // Update with new values
        taskDatabaseDao.insert(resultList[0])

        // Get all tasks again
        resultList = taskDatabaseDao.getAll().first()

        // Check we have 1 task
        Assert.assertEquals(1, resultList.size)

        // Check that task has new updated name
        Assert.assertEquals(newName, resultList[0].name)
    }

    @Test
    fun deleteAll_shouldHaveNoTasks() = runBlocking(Dispatchers.IO) {
        // Insert 3 tasks
        taskDatabaseDao.insert(testTaskDatabaseEntity)
        taskDatabaseDao.insert(testTaskDatabaseEntity)
        taskDatabaseDao.insert(testTaskDatabaseEntity)

        // Get all tasks
        var resultList = taskDatabaseDao.getAll().first()

        // Check we have 3 tasks
        Assert.assertEquals(3, resultList.size)

        // Delete all tasks
        taskDatabaseDao.deleteAll()

        // Get all tasks again
        resultList = taskDatabaseDao.getAll().first()

        // Check we have no tasks
        Assert.assertEquals(0, resultList.size)
    }

    @Test
    fun getWithUUID_shouldReturnTaskWithUUID() = runBlocking(Dispatchers.IO) {
        // Create 2 UUIDs and Names
        val uuid1 = UUID.randomUUID().toString()
        val name1 = "Mamoon"

        val uuid2 = UUID.randomUUID().toString()
        val name2 = "Ayman"

        // Insert 2 tasks with UUIDs and Names
        taskDatabaseDao.insert(getTaskDatabaseEntity(uuid = uuid1, name = name1))
        taskDatabaseDao.insert(getTaskDatabaseEntity(uuid = uuid2, name = name2))

        // Get tasks with first UUID
        val result = taskDatabaseDao.getByUUID(uuid1)

        // Check if it's not null
        Assert.assertNotNull(result)

        // Check that task has name for task with first UUID
        Assert.assertEquals(name1, result?.name)
    }

    // region Helper

    private fun getTaskDatabaseEntity(
            uuid: String = "UUID Test",
            name: String = "Ahmad"
    ): TaskDatabaseEntity = TaskDatabaseEntity(
            id = null,
            uuid = uuid,
            name = name
    )

    // endregion Helper
}