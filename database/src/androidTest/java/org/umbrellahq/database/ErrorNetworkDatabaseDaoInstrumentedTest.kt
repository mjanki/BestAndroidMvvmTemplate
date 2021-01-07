package org.umbrellahq.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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

    // Mock Entities
    private var testErrorNetworkDatabaseEntity = ErrorNetworkDatabaseEntity(
            id = null,
            type = ErrorNetworkTypes.HTTP,
            code = 404,
            action = "Dummy Action"
    )
    
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
    fun insert_twoErrorNetwork_shouldHaveTwoErrorNetworks() = runBlocking(Dispatchers.IO) {
        // Insert 2 network errors
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get all network errors
        val resultList = errorNetworkDatabaseDao.getAll().first()

        // Check if database has 2 network errors
        Assert.assertEquals(2, resultList.size)
    }

    @Test
    fun insert_oneErrorNetwork_shouldHaveSameErrorNetwork() = runBlocking(Dispatchers.IO) {
        // Insert 1 network error
        val id = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get inserted network error by ID
        val result = errorNetworkDatabaseDao.getById(id)

        // Check if it's not null
        Assert.assertNotNull(result)

        // Check if it has the same values
        Assert.assertEquals(testErrorNetworkDatabaseEntity.type, result?.type)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.code, result?.code)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.action, result?.action)
    }

    @Test
    fun insert_oneErrorNetworkThenUpdate_shouldHaveUpdatedErrorNetwork() = runBlocking(Dispatchers.IO) {
        // Insert 1 network error
        val id = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get inserted network error by ID
        var result = errorNetworkDatabaseDao.getById(id)

        // Check if it's not null
        Assert.assertNotNull(result)

        // Check if it's the same code
        Assert.assertEquals(testErrorNetworkDatabaseEntity.code, result?.code)

        // Set ID to update the same error, and change code
        testErrorNetworkDatabaseEntity.id = id
        testErrorNetworkDatabaseEntity.code = 200

        // Update with new values
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get updated network error by ID again
        result = errorNetworkDatabaseDao.getById(id)

        // Check it has the updated code
        Assert.assertEquals(testErrorNetworkDatabaseEntity.code, result?.code)
    }

    @Test
    fun delete_oneErrorNetwork_shouldHaveSizeMinusOne() = runBlocking(Dispatchers.IO) {
        // Insert 2 network errors
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get all network errors
        var resultList = errorNetworkDatabaseDao.getAll().first()

        // Check we have 2 network errors
        Assert.assertEquals(2, resultList.size)

        // Delete first network error
        errorNetworkDatabaseDao.delete(resultList[0])

        // Get all network errors again
        resultList = errorNetworkDatabaseDao.getAll().first()

        // Check we have 1 network error left
        Assert.assertEquals(1, resultList.size)
    }

    @Test
    fun delete_oneErrorNetwork_shouldNotHaveThatErrorNetwork() = runBlocking(Dispatchers.IO) {
        // Insert 1 network error
        val id = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get network error by ID
        var result = errorNetworkDatabaseDao.getById(id)

        // Check if it's not null
        Assert.assertNotNull(result)

        // Delete network error by ID
        errorNetworkDatabaseDao.delete(id)

        // Get network error by ID again
        result = errorNetworkDatabaseDao.getById(id)

        // Check if it's null
        Assert.assertNull(result)
    }

    @Test
    fun delete_oneErrorNetwork_shouldHaveOtherErrorNetworks() = runBlocking(Dispatchers.IO) {
        // Insert first network error
        val id1 = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Insert second network error
        val id2 = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get all network errors
        var resultList = errorNetworkDatabaseDao.getAll().first()

        // Check we have 2 network errors
        Assert.assertEquals(2, resultList.size)

        // Delete first network error by ID
        errorNetworkDatabaseDao.delete(id1)

        // Get all network errors again
        resultList = errorNetworkDatabaseDao.getAll().first()

        // Check 1 network error left
        Assert.assertEquals(1, resultList.size)

        // Check remaining network error is the second network error
        Assert.assertEquals(id2, resultList[0].id)
    }

    @Test
    fun deleteAll_shouldHaveNoErrorNetworks() = runBlocking(Dispatchers.IO) {
        // Insert 3 network errors
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity)

        // Get all network errors
        var resultList = errorNetworkDatabaseDao.getAll().first()

        // Check we have 3 network errors
        Assert.assertEquals(3, resultList.size)

        // Delete all network errors
        errorNetworkDatabaseDao.deleteAll()

        // Get all network errors again
        resultList = errorNetworkDatabaseDao.getAll().first()

        // Check we have no network errors left
        Assert.assertEquals(0, resultList.size)
    }
}