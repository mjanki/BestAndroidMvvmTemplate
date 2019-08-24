package org.umbrellahq.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.*
import org.junit.runner.RunWith
import org.umbrellahq.database.dao.TaskDao
import org.umbrellahq.database.model.TaskDatabaseEntity
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class TaskDaoInstrumentedTest {
    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var taskDao: TaskDao
    private lateinit var db: AppDatabase

    private lateinit var testTaskDatabaseEntity: TaskDatabaseEntity

    @Before
    fun setupAndroidThreeTen() {
        AndroidThreeTen.init(ApplicationProvider.getApplicationContext())

        testTaskDatabaseEntity = TaskDatabaseEntity(
                null,
                "Ahmad"
        )
    }

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create test database
        db = Room.inMemoryDatabaseBuilder(
                context,
                AppDatabase::class.java
        ).allowMainThreadQueries().build()

        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testMultipleEntriesCount() {
        // Add 2 tasks
        taskDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDao.insert(testTaskDatabaseEntity).blockingAwait()

        // Get all tasks
        val allTasks = taskDao.getAll().blockingFirst()

        // Check if database has 2 tasks
        Assert.assertEquals(2, allTasks.size)
    }

    @Test
    fun testSingleEntryValue() {
        taskDao.insert(testTaskDatabaseEntity).blockingAwait()

        val allTasks = taskDao.getAll().blockingFirst()

        Assert.assertEquals(1, allTasks.size)
        Assert.assertEquals(testTaskDatabaseEntity.name, allTasks[0].name)
    }

    @Test
    fun testSingleEntryUpdateValue() {
        taskDao.insert(testTaskDatabaseEntity).blockingAwait()
        var allTasks = taskDao.getAll().blockingFirst()

        Assert.assertEquals(1, allTasks.size)

        val newName = "Eyad"
        allTasks[0].name = newName
        taskDao.insert(allTasks[0]).blockingAwait()
        allTasks = taskDao.getAll().blockingFirst()

        Assert.assertEquals(1, allTasks.size)
        Assert.assertEquals(newName, allTasks[0].name)
    }

    @Test
    fun testDeleteAllEntries() {
        taskDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDao.insert(testTaskDatabaseEntity).blockingAwait()
        taskDao.insert(testTaskDatabaseEntity).blockingAwait()
        var allTasks = taskDao.getAll().blockingFirst()

        Assert.assertEquals(3, allTasks.size)

        taskDao.deleteAll().blockingAwait()
        allTasks = taskDao.getAll().blockingFirst()

        Assert.assertEquals(0, allTasks.size)
    }
}