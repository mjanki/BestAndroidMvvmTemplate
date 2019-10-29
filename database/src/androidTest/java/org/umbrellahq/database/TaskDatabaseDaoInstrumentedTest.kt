package org.umbrellahq.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
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
    fun insert_twoTasks_shouldHaveTwoTasks() {
        // Add 2 tasks
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()

        // Get all tasks
        val allTasks = taskDatabaseDao.getAll().blockingFirst()

        // Check if database has 2 tasks
        Assert.assertEquals(2, allTasks.size)
    }

    @Test
    fun insert_oneTask_shouldHaveSameTask() {
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()

        val allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(1, allTasks.size)
        Assert.assertEquals(testTaskDatabaseEntity.name, allTasks[0].name)
    }

    @Test
    fun insert_oneTaskThenUpdate_shouldHaveUpdatedTask() {
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        var allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(1, allTasks.size)

        val newName = "Eyad"
        allTasks[0].name = newName
        taskDatabaseDao.insert(allTasks[0]).blockingAwait()
        allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(1, allTasks.size)
        Assert.assertEquals(newName, allTasks[0].name)
    }

    @Test
    fun deleteAll_shouldHaveNoTasks() {
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        var allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(3, allTasks.size)

        taskDatabaseDao.deleteAll().blockingAwait()
        allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(0, allTasks.size)
    }

    @Test
    fun getWithUUID_shouldReturnTaskWithUUID() {
        val uuid1 = UUID.randomUUID().toString()
        val name1 = "Mamoon"

        val uuid2 = UUID.randomUUID().toString()
        val name2 = "Ayman"

        taskDatabaseDao.insert(getTaskDatabaseEntity(uuid = uuid1, name = name1)).blockingAwait()
        taskDatabaseDao.insert(getTaskDatabaseEntity(uuid = uuid2, name = name2)).blockingAwait()

        val tasksWithUUID1 = taskDatabaseDao.getWithUUID(uuid1).blockingFirst()
        Assert.assertEquals(1, tasksWithUUID1.size)
        Assert.assertEquals(name1, tasksWithUUID1[0].name)
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