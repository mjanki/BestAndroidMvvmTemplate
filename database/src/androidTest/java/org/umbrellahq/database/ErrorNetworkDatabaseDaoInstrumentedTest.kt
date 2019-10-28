package org.umbrellahq.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.*
import org.junit.runner.RunWith
import org.umbrellahq.database.daos.ErrorNetworkDatabaseDao
import org.umbrellahq.database.models.ErrorNetworkDatabaseEntity
import org.umbrellahq.util.enums.ErrorNetworkTypes
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ErrorNetworkDatabaseDaoInstrumentedTest {
    @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var errorNetworkDatabaseDao: ErrorNetworkDatabaseDao

    private lateinit var testErrorNetworkDatabaseEntity: ErrorNetworkDatabaseEntity

    @Before
    fun setup() {
        testErrorNetworkDatabaseEntity = ErrorNetworkDatabaseEntity(
                id = null,
                type = ErrorNetworkTypes.HTTP,
                code = 404,
                action = "Dummy Action"
        )
    }
    
    @Before
    fun setupDB() {
        // Create test database
        db = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                AppDatabase::class.java
        ).allowMainThreadQueries().build()
        
        errorNetworkDatabaseDao = db.errorNetworkDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testMultipleEntriesCount() {
        // Add 2 network errors
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()

        // Get all network errors
        val allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()

        // Check if database has 2 network errors
        Assert.assertEquals(2, allErrorNetwork.size)
    }

    @Test
    fun testSingleEntryValue() {
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()

        val allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(1, allErrorNetwork.size)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.type, allErrorNetwork[0].type)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.code, allErrorNetwork[0].code)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.action, allErrorNetwork[0].action)
    }

    @Test
    fun testSingleEntryUpdateValue() {
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()
        var allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(1, allErrorNetwork.size)

        val newCode = 200
        allErrorNetwork[0].code = newCode
        errorNetworkDatabaseDao.insert(allErrorNetwork[0]).blockingAwait()
        allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(1, allErrorNetwork.size)
        Assert.assertEquals(newCode, allErrorNetwork[0].code)
    }

    @Test
    fun testDeleteSingleEntry() {
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()
        
        var allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()
        Assert.assertEquals(2, allErrorNetwork.size)

        errorNetworkDatabaseDao.delete(allErrorNetwork[0]).blockingAwait()
        allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()
        Assert.assertEquals(1, allErrorNetwork.size)
    }

    @Test
    fun testDeleteAllEntries() {
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).blockingAwait()
        var allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(3, allErrorNetwork.size)

        errorNetworkDatabaseDao.deleteAll().blockingAwait()
        allErrorNetwork = errorNetworkDatabaseDao.getAll().blockingFirst()

        Assert.assertEquals(0, allErrorNetwork.size)
    }
}