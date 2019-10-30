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
    fun insert_twoErrorNetwork_shouldHaveTwoErrorNetworks() {
        // Insert 2 network errors
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()

        // Get all network errors
        val resultList = errorNetworkDatabaseDao.getAll().test()
        val insertedErrorNetworkList = resultList.values()[0]

        // Check if database has 2 network errors
        Assert.assertEquals(2, insertedErrorNetworkList.size)
    }

    @Test
    fun insert_oneErrorNetwork_shouldHaveSameErrorNetwork() {
        // Insert 1 network error
        val result = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        val id = result.values()[0]

        // Get inserted network error by ID
        val resultList = errorNetworkDatabaseDao.getById(id).test()
        val insertedErrorNetworkList = resultList.values()[0]
        val insertedErrorNetwork = insertedErrorNetworkList[0]

        // Check if it has the same values
        Assert.assertEquals(testErrorNetworkDatabaseEntity.type, insertedErrorNetwork.type)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.code, insertedErrorNetwork.code)
        Assert.assertEquals(testErrorNetworkDatabaseEntity.action, insertedErrorNetwork.action)
    }

    @Test
    fun insert_oneErrorNetworkThenUpdate_shouldHaveUpdatedErrorNetwork() {
        // Insert 1 network error
        val result = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        val id = result.values()[0]

        // Get inserted network error by ID
        var resultList = errorNetworkDatabaseDao.getById(id).test()
        var insertedErrorNetworkList = resultList.values()[0]
        var insertedErrorNetwork = insertedErrorNetworkList[0]

        // Check if it's the same code
        Assert.assertEquals(testErrorNetworkDatabaseEntity.code, insertedErrorNetwork.code)

        // Set ID to update the same error, and change code
        testErrorNetworkDatabaseEntity.id = id
        testErrorNetworkDatabaseEntity.code = 200

        // Update with new values
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()

        // Get updated network error by ID again
        resultList = errorNetworkDatabaseDao.getById(id).test()
        insertedErrorNetworkList = resultList.values()[0]
        insertedErrorNetwork = insertedErrorNetworkList[0]

        // Check it has the updated code
        Assert.assertEquals(testErrorNetworkDatabaseEntity.code, insertedErrorNetwork.code)
    }

    @Test
    fun delete_oneErrorNetwork_shouldHaveSizeMinusOne() {
        // Insert 2 network errors
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()

        // Get all network errors
        var resultList = errorNetworkDatabaseDao.getAll().test()
        var insertedErrorNetworkList = resultList.values()[0]

        // Check we have 2 network errors
        Assert.assertEquals(2, insertedErrorNetworkList.size)

        // Delete first network error
        errorNetworkDatabaseDao.delete(insertedErrorNetworkList[0]).test()

        // Get all network errors again
        resultList = errorNetworkDatabaseDao.getAll().test()
        insertedErrorNetworkList = resultList.values()[0]

        // Check we have 1 network error left
        Assert.assertEquals(1, insertedErrorNetworkList.size)
    }

    @Test
    fun delete_oneErrorNetwork_shouldNotHaveThatErrorNetwork() {
        // Insert 1 network error
        val result = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        val id = result.values()[0]

        // Get network error by ID
        var resultList = errorNetworkDatabaseDao.getById(id).test()
        var insertedErrorNetworkList = resultList.values()[0]

        // Check it exists
        Assert.assertEquals(1, insertedErrorNetworkList.size)

        // Delete network error by ID
        errorNetworkDatabaseDao.delete(id).test()

        // Get network error by ID again
        resultList = errorNetworkDatabaseDao.getById(id).test()
        insertedErrorNetworkList = resultList.values()[0]

        // Check it doesn't exist
        Assert.assertEquals(0, insertedErrorNetworkList.size)
    }

    @Test
    fun delete_oneErrorNetwork_shouldHaveOtherErrorNetworks() {
        // Insert first network error
        val result1 = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        val id1 = result1.values()[0]

        // Insert second network error
        val result2 = errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        val id2 = result2.values()[0]

        // Get all network errors
        var resultList = errorNetworkDatabaseDao.getAll().test()
        var insertedErrorNetworkList = resultList.values()[0]

        // Check we have 2 network errors
        Assert.assertEquals(2, insertedErrorNetworkList.size)

        // Delete first network error by ID
        errorNetworkDatabaseDao.delete(id1).test()

        // Get all network errors again
        resultList = errorNetworkDatabaseDao.getAll().test()
        insertedErrorNetworkList = resultList.values()[0]

        // Check 1 network error left
        Assert.assertEquals(1, insertedErrorNetworkList.size)

        // Check remaining network error is the second network error
        Assert.assertEquals(id2, insertedErrorNetworkList[0].id)
    }

    @Test
    fun deleteAll_shouldHaveNoErrorNetworks() {
        // Insert 3 network errors
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()
        errorNetworkDatabaseDao.insert(testErrorNetworkDatabaseEntity).test()

        // Get all network errors
        var resultList = errorNetworkDatabaseDao.getAll().test()
        var insertedErrorNetworkList = resultList.values()[0]

        // Check we have 3 network errors
        Assert.assertEquals(3, insertedErrorNetworkList.size)

        // Delete all network errors
        errorNetworkDatabaseDao.deleteAll().test()

        // Get all network errors again
        resultList = errorNetworkDatabaseDao.getAll().test()
        insertedErrorNetworkList = resultList.values()[0]

        // Check we have no network errors left
        Assert.assertEquals(0, insertedErrorNetworkList.size)
    }
}