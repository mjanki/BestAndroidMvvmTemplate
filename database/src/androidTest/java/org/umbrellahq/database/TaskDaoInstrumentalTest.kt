package org.umbrellahq.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jakewharton.threetenabp.AndroidThreeTen
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
class TaskDaoInstrumentalTest {
    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var taskDao: TaskDao
    private lateinit var db: AppDatabase

    @Before
    fun setupAndroidThreeTen() {
        AndroidThreeTen.init(ApplicationProvider.getApplicationContext())
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
    fun writeTaskAndReadInList() {
        val taskDatabaseEntity = TaskDatabaseEntity(
                null,
                "Ahmad"
        )

        taskDao.insert(taskDatabaseEntity).blockingAwait()

        taskDao.getAll().test().assertValueCount(1)
    }
}