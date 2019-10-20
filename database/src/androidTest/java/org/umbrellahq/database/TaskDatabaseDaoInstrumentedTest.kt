package org.umbrellahq.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.*
import org.junit.runner.RunWith
import org.umbrellahq.database.dao.TaskDatabaseDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import java.io.IOException

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

        testTaskDatabaseEntity = TaskDatabaseEntity(
                null,
                "Ahmad"
        )
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
    fun testMultipleEntriesCount() {
        // Add 2 tasks
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()

        // Get all tasks
        val allTasks = taskDatabaseDao.getAll().blockingFirst()

        // Check if database has 2 tasks
        Assert.assertEquals(2, allTasks.size)
    }

    @Test
    fun testSingleEntryValue() {
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()

        val allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(1, allTasks.size)
        Assert.assertEquals(testTaskDatabaseEntity.name, allTasks[0].name)
    }

    @Test
    fun testSingleEntryUpdateValue() {
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
    fun testDeleteAllEntries() {
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDatabaseDao.insert(testTaskDatabaseEntity).blockingAwait()
        var allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(3, allTasks.size)

        taskDatabaseDao.deleteAll().blockingAwait()
        allTasks = taskDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(0, allTasks.size)
    }
}