# Current Version
1.0.0 Alpha 3

# Overview
This is a base app, a template, a starting point for any android project. This template uses [**MVVM Clean architecture**](https://www.toptal.com/android/android-apps-mvvm-with-clean-architecture) to the best of my understanding; it uses **reactive patterns** using [**RxKotlin**](https://github.com/ReactiveX/RxKotlin), [**Room**](https://developer.android.com/topic/libraries/architecture/room) for persistence, and [**Retrofit2**](https://square.github.io/retrofit/) for networking. I will start by describing and explaining everything layer by layer, starting from the lowest layers all the way until the **View** layer.

<img align="center" alt="Architecture Overview" src="/README_FILES/architecture_full.png" />

## First Use
Here are the things you **have** to do when you first start a project with this template:

Either use the `start.py` script (Python3):
* In the command line go to the project.
* Type the command `python start.py`, or `python3 start.py` depending on which python version is your default.
* Enter the package name when prompted.
* Enter the database name when prompted.

Or do it manually:
* Change the package name of each module.
* Change the package name in each module's `AndroidManifest.xml`.
* Change the `applicationId` in the **View's** gradle file.
* Change all imports in your files such as `org.umbrellahq.baseapp.R` to fit your new package names.

Then:
* Delete all **Task** related classes / files, or keep for reference on how to use.
* Empty the `MainFragment`.
* Modify the `MainActivity` to your liking, or keep as is.
* Clean out the resources that you don't want, and replace / modify the `xml` layouts.

## Quick Points
Here are a few things to keep in mind before you read further:
* This project is written completely in **Kotlin**.
* This project is **modular**; each layer lives inside its own module and has its own dependencies.
* Each layer sees only the layers directly below it:
  * The **View** layer only sees the **ViewModel** layer.
  * The **ViewModel** layer only sees the **Repository** layer.
  * The **Repository** layer only sees the **Database** and the **Network** layers.
  * **EXCEPTION**: all layers see a **util** layer that has helper methods and extensions.
* Each layer has its own **model** and maps between them using custom mappers.
* This project uses `RxKotlin` & `RxAndroid` in the **Database**, **Network**, and **Repository** layers, and `LiveData` in the **View** layer, and maps from `Rx` to `LiveData` in the **ViewModel** layer.
  * The reason behind this is that `LiveData` is made to work with lifecycles, and the lower layers really don't know much about the lifecycles on the app.
* The **Database** is the single source of truth.
* This template uses a single activity, multiple fragments architecture; this is what **Google** recommends (not the best source I agree but what can I say?) It also uses the new **Navigation** components; I haven't used `SafeArgs` yet but they should be easily added.
* This project comes with a running example that has **Unit Tests** and **Instrumented Tests**.

## Model Custom Mappers
Each layer will have its own **models**, along with a **mapper** per model per direct layer below it. So a `TaskViewModelEntity` will have a mapper that maps to and from a `TaskRepoEntity`. A `TaskRepoEntity` will have two mappers in the **Repository** layer that will map it to and from both `TaskNetworkEntity` and `TaskDatabaseEntity`.

Each mapper will implement an interface that looks like this:
```kotlin
interface Mapper<T, V> {
    // Map from current layer entity to specific layer below entity
    fun downstream(currentLayerEntity: T): V

    // Map from specific layer below entity to current layer entity
    fun upstream(nextLayerEntity: V): T
}
```
An example would be `TaskViewViewModelMapper` in the **View** layer which would look like this:
```kotlin
class TaskViewViewModelMapper : Mapper<TaskViewEntity, TaskViewModelEntity> {
    override fun downstream(currentLayerEntity: TaskViewEntity) = TaskViewModelEntity(
            id = currentLayerEntity.id,
            name = currentLayerEntity.name,
            date = currentLayerEntity.date,
            status = currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskViewModelEntity) = TaskViewEntity(
            id = nextLayerEntity.id,
            name = nextLayerEntity.name,
            date = nextLayerEntity.date,
            status = nextLayerEntity.status
    )
}
```
For more information on how to use mappers please check the example code I have in this project.

### Important Note:
One might argue that we can share a **model** module between all layers and that would make our lives easier. However, the benefits of this more separate approach can be seen in this template's example code. `TaskNetworkEntity` has a `uuid` and no `id`, `TaskDatabaseEntity` has both `id` and `uuid` for syncing purposes, `TaskRepoEntity` has both `id` and `uuid` to coordinate between the database and the network, and `TaskViewModelEntity` only has `id` because in a **single source of truth** approach where the **database** is the single source of truth the **ViewModel** layer doesn't care about the `uuid`.

In summary, different layers care about different properties and that's why I decided to go with the **model per layer** approach.

## Util Module
This module's purpose is to provide **helper methods**, **extensions**, and **super classes** that should be useful in the other layers. This module was created in the earliest stages of development and needs refinement, but as of now here's a quick rundown of what it has.

### ErrorNetworkTypes:
This is an `enum` for **network error types** which is used when recording network errors in the **database**, and it looks like this:
```kotlin
enum class ErrorNetworkTypes {
    HTTP,
    TIMEOUT,
    IO,
    OTHER
}
```

### RxKotlinExtensions:
This is a collection of extensions for `Rx` that aid in invoking **database** queries (without continuously observing), and in making **network** calls. It also has an `object` that has a `Boolean` variable named `isTesting` which should be set to `true` in tests, and it has a method named `getScheduler()` that will return the appropriate scheduler depending on `isTesting`. The `object` looks like this:
```kotlin
object RxKotlinExtensions {
    var isTesting = false
    fun getScheduler(): Scheduler = if (isTesting) Schedulers.trampoline() else Schedulers.io()
}
```
As for the extensions themselves; let's go through them one by one:

`Completable.execute(...)`: used to invoke **database** queries that return no values such as `deleteAll`. It takes in two parameters:
* `onSuccess()`: **optional** success handler.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.

`Single<T>.execute(...)`: used to invoke **database** queries that return a value such as an `id` after `insert`. It takes in two parameters:
* `onSuccess(value: T)`: **optional** success handler with value.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.

`Flowable<T>.getValue(...)`: used to invoke **database** queries that return a `Flowable` that you can continuously observe but you want to get the value **once**. It takes in two parameters:
* `onSuccess(value: T)`: **required** success handler with value.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.

`Observable<Response<T>>.execute(...)`: used to invoke a **network** call. It takes in three parameters:
* `onSuccess(value: Response<T>)`: **required** success handler with value.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.
* `onComplete()`: **optional** completion handler.

This last extension for the **network** calls is particularly interesting because it will invoke the `onFailure(throwable: Throwable)` method on both network errors **and** on an `HttpException`.

Usage examples will be shown as we dive deeper into each layer. You can also look at this template's example code.

### NavigationHelper:
This has two methods to add and remove a blocking overlay programmatically on top of the **Activity**; I added this to fix the problem of double clicking on a button that navigates to a different **Activity**/**Fragment**. The methods `addOverlay(activity: FragmentActivity)` and `removeOverlay(activity: FragmentActivity)` both take in a `FragmentActivity` as a parameter. The idea is to add the overlay on click, and remove the overlay on `onResume(...)`. This will work as long as:
* **Activity** uses a `ConstraintLayout`; preferably a full screen one.
* The `id` of the `ConstraintLayout` is passed on `onCreate` of the **Activity** (using `NavigationUtil.setup(R.id.[id])`).

### NavigationUtil:
This has two things as of this moment:
* `constraintLayoutResId: Int` to store the **Activity**'s `ConstraintLayout`'s `id` to be used in adding and removing the overlay.
* `AppCompatActivity.setupToolbar(...)` extension to shorten setting up a `Toolbar`:
```kotlin
fun AppCompatActivity.setupToolbar(toolbar: Toolbar, showUp: Boolean = true, title: String = "") {
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(showUp)
    this.title = title
}
```
* **NOT IMPORTANT**: this had many more extensions to help with navigation in general; but that was before **Google** introduced the new **Navigation Components** and **Jetpack** so I removed all of it.

### ViewUtil:
This only has a `ViewGroup.inflate(...)` helper method that shortens inflating views in **Fragments**:
```kotlin
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}
```

# Deeper Look
Now we shall move into the actual layers / modules of the template. Each layer is a module in our case so I'll be using the words *layer* and *module* interchangeably.

## Database Module
The **database** module takes care of everything related to the **database**. It uses `Room` with `RxKotlin`. Here's a detailed rundown of what this layer includes at the moment:

### Models:
This has all the models (entities) for the database. For example, `TaskDatabaseEntity` under the sub-package `models` looks like this:
```kotlin
@Entity(tableName = "tasks")
data class TaskDatabaseEntity(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "uuid") var uuid: String,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "date") var date: OffsetDateTime = OffsetDateTime.now(),
        @ColumnInfo(name = "status") var status: Int = 0
)
```

### Type Converters:
The database doesn't know how to convert complicated objects into primitive and storable data types. That's why we need **Type Converters**. For example, `TaskDatabaseEntity` has a column of type `OffsetDateTime`; so we have to write a type converter that we'll use in the `AppDatabase.kt` file later (explained in the **AppDatabase.kt** section below). The type converter for `OffsetDateTime` under the sub-package `type_converters` looks like this:
```kotlin
object DateTypeConverter {
    private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String?): OffsetDateTime? = value?.let {
        return formatter.parse(it, OffsetDateTime::from)
    }

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(date: OffsetDateTime?) = date?.format(formatter)
}
```

### Data Access Objects (DAOs):
This is where we define **query methods** to access the database. This is pretty straightforward; if you don't know how `Room` and `RxKotlin` can work together look it up. Here are a few examples of what this template uses:

* `getByUUID(uuid: String)` will retrieve tasks by `uuid` and will return a `Flowable` so that you can continuously observe the changes on those tasks:
```kotlin
@Query("SELECT * from tasks where uuid = :uuid")
fun getByUUID(uuid: String): Flowable<List<TaskDatabaseEntity>>
```

* `insert(taskDatabaseEntity: TaskDatabaseEntity)` will insert a task into the database and will return a `Single<Long>` with the inserted task's `id`:
```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
fun insert(taskDatabaseEntity: TaskDatabaseEntity): Single<Long>
```

* `deleteAll()` will delete all tasks in the database and will just return a `Completable`:
```kotlin
@Query("DELETE from tasks")
fun deleteAll(): Completable
```

Those queries can be used either by manually subscribing and doing the ***Rx*** thing; or you can just rely on the `RxKotlinExtensions` that I explained earlier which will do the same thing for you. Also, feel free to write other extensions if you want to expand what's already there.

### AppDatabase.kt:
Here all you need to do is define *4 things*:
* **Entities** & **Database Version**:
```kotlin
@Database(entities = [
    TaskDatabaseEntity::class,
    ErrorNetworkDatabaseEntity::class
], version = 1)
```
* **Type Converters**:
```kotlin
@TypeConverters(DateTypeConverter::class, ErrorNetworkTypeConverter::class)
```
* **Data Access Objects (DAOs)**:
```kotlin
abstract fun taskDao(): TaskDatabaseDao
abstract fun errorNetworkDao(): ErrorNetworkDatabaseDao
```
* **Database Name**:
```kotlin
private fun buildDatabase(context: Context) = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "simplyToDo.db"
).build()
```

### Tests:
Database tests are under the `androidTest` sub-package; the reason why they're not under the `test` sub-package is because we need the `ApplicationContext` to test the database. Please refer to this template's example code; it's fairly well documented with comments.

## Network Module
The **network** module takes care of everything related to the **network**. It uses `Retrofit2` with `RxKotlin`. Here's a detailed rundown of what this layer includes at the moment:

### AndroidManifest.xml:
This declares that the app needs the **Internet** permission:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

### Models:
This has all the models (entities) for the network. For example, `TaskNetworkEntity` under the sub-package `models` looks like this:
```kotlin
data class TaskNetworkEntity(
        var uuid: String,
        var name: String,
        var date: String,
        var status: Int
)
```

Here we also have an `ErrorNetworkEntity` which can be modified to your liking; this is what we'll emit to the `Repository` layer when **Network Errors** occur. You'll see how that's used in the **Repository Module** section, but for now this is how `ErrorNetworkEntity` looks like:
```kotlin
data class ErrorNetworkEntity (
        var type: ErrorNetworkTypes = ErrorNetworkTypes.OTHER,
        var shouldPersist: Boolean = false,
        var code: Int = 0,
        var message: String = "",
        var action: String = ""
)
```

### Clients
This is where you define all your endpoints (**NOT** the base URL), and the verb for each endpoint; I have one simple example of a `GET` request that has the end point `/tasks` (which will be appended to the base URL in the **DAO**) and it looks like this:
```kotlin
@GET("tasks")
fun getTasks(): Observable<Response<List<TaskNetworkEntity>>>
```

The API will return a `List<TaskNetworkEntity>`; we wrap it in a `Response` so that we can handle `HttpException`s and to handle different `Response` codes (200, 201, etc...) if we wish to do so.

### Data Access Objects (DAOs):
This is where we execute the **network** calls. In each **DAO** we'll have a few `PublishSubject`s that we can observe in the **Repository** layer. Before I go into an example let me explain the idea behind the `BaseNetworkDao` class that all other **DAOs** should extend.

#### BaseNetworkDao:
The `BaseNetworkDao` is an open class that has two main things:
* A `PublishSubject` named `errorNetwork` that emits `ErrorNetworkEntity` when network errors occur.
* A helper method `fun <T> executeNetworkCall(...)` that will figure out all the information in the error if an error occurs and emits that information into `errorNetwork` while allowing you to define an `onFailure(throwable: Throwable)` that will be called after that logic is done and the error is emitted. It takes advantage of the earlier described `RxKotlinExtensions` and is not meant to be removed, only modified; of course you can remove it if you want to anyway. Here's how it looks like:

```kotlin
open class BaseNetworkDao {

    val errorNetwork = PublishSubject.create<ErrorNetworkEntity>()
    protected fun <T> executeNetworkCall(
            observable: Observable<Response<T>>,
            shouldPersist: Boolean = false,
            action: String = "",
            onSuccess: ((value: Response<T>) -> Unit),
            onFailure: ((throwable: Throwable) -> Unit)? = null,
            onComplete: (() -> Unit)? = null): Disposable {

        return observable.execute(
                onSuccess = { response ->
                    onSuccess(response)
                },
                onFailure = { throwable ->
                    val errorNetworkEntity = ErrorNetworkEntity()

                    throwable.message?.let {
                        errorNetworkEntity.message = it
                    }

                    when (throwable) {
                        is SocketTimeoutException -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.TIMEOUT
                        }

                        is IOException -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.IO
                        }

                        is HttpException -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.HTTP
                            errorNetworkEntity.code = throwable.code()
                        }

                        else -> {
                            errorNetworkEntity.type = ErrorNetworkTypes.OTHER
                        }
                    }

                    errorNetworkEntity.shouldPersist = shouldPersist
                    errorNetworkEntity.action = action

                    errorNetwork.onNext(errorNetworkEntity)

                    onFailure?.let { onFailure ->
                        onFailure(throwable)
                    }
                },
                onComplete = {
                    onComplete?.let { onComplete ->
                        onComplete()
                    }
                }
        )
    }
}
```

#### TaskNetworkDao (EXAMPLE):
This class extends `BaseNetworkDao` and will have a few things:
* `requestInterface` will be a `var` that will define things like the **base URL**, automatic converters (e.g. JSON to Model), `Rx` support, and such. It looks like this:
```kotlin
private var requestInterface: TaskClient = Retrofit.Builder()
        .baseUrl("https://demo2500655.mockable.io")
        .addConverterFactory(MoshiConverterFactory.create().asLenient())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(TaskClient::class.java)
```
* `setRequestInterface(...)` that we'll use to set **mock** interfaces for testing. Remember, tests in the **network** layer shouldn't test the endpoints themselves; that's the job of the **API** itself not the app. It's a simple setter:
```kotlin
fun setRequestInterface(taskClient: TaskClient) {
    requestInterface = taskClient
}
```
* A `PublishSubject` for `retrievedTasks` that will emit the result of the **network** call. It looks like this:
```kotlin
val retrievedTasks = PublishSubject.create<Response<List<TaskNetworkEntity>>>()
```
* A `PublishSubject` for `isRetrievingTasks` that will emit when a **network** call is made and when it's done (used for **UI** visual indication of *loading*). It looks like this:
```kotlin
val isRetrievingTasks = PublishSubject.create<Boolean>()
```
* `retrieveTasks()` that looks like this:
```kotlin
fun retrieveTasks() {
    isRetrievingTasks.onNext(true)
    executeNetworkCall(
            observable = requestInterface.getTasks(),
            action = "Fetching tasks from the cloud",
            onSuccess = {
                retrievedTasks.onNext(it)
            },
            onComplete = {
                isRetrievingTasks.onNext(false)
            }
    )
}
```

### Tests:
Network tests are under the `test` sub-package. Tests in the **network** layer mock all endpoints because testing those is the responsibility of the **API** itself. Please refer to this template's example code; it's fairly well documented with comments.

## Repository Module
The **Repository** module takes care of coordinating between the two data sources we have (network and database). You can implement any logic you want here, but ideally all data sources should dump results in the database and the app should only observe the database. Here's a detailed rundown of what this layer includes at the moment:

### Models:
This has all the models (entities) for the repository. For example, `TaskRepoEntity` under the sub-package `models` looks like this:
```kotlin
data class TaskRepoEntity(
        var id: Long? = null,
        var uuid: String = UUID.randomUUID().toString(),
        var name: String,
        var date: OffsetDateTime = OffsetDateTime.now(),
        var status: Int = 0
)
```

### Mappers:
An example of a `Mapper` is the `TaskRepoNetworkMapper` under the `mappers` sub-package and it looks like this:
```kotlin
class TaskRepoNetworkMapper : Mapper<TaskRepoEntity, TaskNetworkEntity> {
    override fun downstream(currentLayerEntity: TaskRepoEntity) = TaskNetworkEntity(
            uuid = currentLayerEntity.uuid,
            name = currentLayerEntity.name,
            date = "${currentLayerEntity.date}",
            status = currentLayerEntity.status
    )

    override fun upstream(nextLayerEntity: TaskNetworkEntity) = TaskRepoEntity(
            uuid = nextLayerEntity.uuid,
            name = nextLayerEntity.name,
            date = OffsetDateTime.parse(nextLayerEntity.date),
            status = nextLayerEntity.status
    )
}
```

### Repositories:
Here we'll have all the repositories that are used in the **ViewModel** layer. They handle communicating with the data sources and coordinating between them so that the **ViewModel** layer doesn't need to care about how the data is retrieved or saved. First let me explain the `Repository.kt` superclass that should be extended by all repositories.

#### Repository.kt:
This is an open class that should be extended by all other repository classes. A few points to keep in mind:
* It will take a `Context?` as a parameter so that it can instantiate an `AppDatabase`, and it's an optional `Context?` because we'll pass a mock when we test.
* It has an `open fun init()` that will be used to initialize it with real data. Again, this is for testing purposes.
* It has a `CompositeDisposable` and a `clearDisposables()` method that will be used to add disposables and clear them when appropriate.
* `getDisposablesSize()` and `getDisposableIsDisposed()` are intended for testing.

It looks like this:
```kotlin
open class Repository(ctx: Context?) {
    protected lateinit var appDatabase: AppDatabase
    init {
        ctx?.let {
            appDatabase = AppDatabase(it)
        }
    }

    // To override
    open fun init() { /* Implement in subclasses */ }

    // DAOs
    protected var disposables = CompositeDisposable()

    fun clearDisposables() {
        disposables.clear()
    }

    fun getDisposablesSize() = disposables.size()
    fun getDisposableIsDisposed() = disposables.isDisposed
}
```

#### TaskRepository (EXAMPLE):
Each repository should be initialized first, and then should call `init(...)` before use. In production code we don't need to pass anything, but for the tests we need to pass mock data sources. The reason why we have two methods, an empty `init()` and another `init(testTaskNetworkDao? = null, testTaskDatabaseDao: TaskDatabaseDao? = null)` instead of just the latter is because we can't just use the latter in the **ViewModel** even if we just pass `null` because the **ViewModel** layer doesn't see the **network**'s and **database**'s models. Here are a few pointers on how to use this:

* Each repository will have a reference to each data source's **DAO**:
```kotlin
private lateinit var taskNetworkDao: TaskNetworkDao
private lateinit var taskDatabaseDao: TaskDatabaseDao
```
* It will also have a reference to all needed **Mappers**:
```kotlin
private var taskRepoDatabaseMapper = TaskRepoDatabaseMapper()
private var taskRepoNetworkMapper = TaskRepoNetworkMapper()
private var errorNetworkRepoNetworkMapper = ErrorNetworkRepoNetworkMapper()
```
* If you want to observe or get something from your data sources you can have a `private` property that will hold the retrieved values then you can have a `public` method that will map that property to the **Repository**'s models then emit to the **ViewModel**. `allTasks` is a good example, here we have a `private` property:
```kotlin
private lateinit var allTasks: Flowable<List<TaskDatabaseEntity>>
```
Then in the `init` method we set it to retrieve from the database:
```kotlin
allTasks = taskDatabaseDao.getAll()
```
Then in a public method we map it to a `Flowable` that holds a list of `TaskRepoEntity` instead of `TaskDatabaseEntity`:
```kotlin
fun getTasks(): Flowable<List<TaskRepoEntity>> =
        allTasks.flatMap { taskDatabaseEntityList ->
            Flowable.fromArray(
                    taskDatabaseEntityList.map { taskDatabaseEntity ->
                        taskRepoDatabaseMapper.upstream(
                                taskDatabaseEntity
                        )
                    }
            )
        }
```
* `isRetrievingTasks` is just retrieved from the **network** layer and emitted directly to the **ViewModel** (which will probably just emit it to the **View** layer to handle **UI** changes for *loading* states.
* The repository will handle syncing without letting the upper layers know. For example, when we retrieve the tasks from the **API** we sync it with the database (which is already observed in the **ViewModel** layer). This piece of code retrieves the tasks, then checks if we already have them; the ones we don't have already are inserted in the database, and the ones we already have are updated in the database:
```kotlin
taskNetworkDao.retrievedTasks.subscribe {
    it.body()?.let { taskNetworkEntities ->
        for (taskNetworkEntity in taskNetworkEntities) {
            val taskRepoEntity = taskRepoNetworkMapper.upstream(taskNetworkEntity)

            taskDatabaseDao.getByUUID(taskNetworkEntity.uuid).getValue(
                    onSuccess = { taskDatabaseEntities ->
                        if (taskDatabaseEntities.isNotEmpty()) {
                            taskRepoEntity.id = taskDatabaseEntities[0].id
                        }

                        insertTask(taskRepoEntity)
                    }
            )


        }
    }
}.addTo(disposables)
```
* Of course in such examples were we continuously observe using `Rx` we don't want to forget to `addTo(disposables)` so that we can clear them in the `onCleared()` method in the **ViewModel**.
* When an error occurs it's inserted in the database directly and it will be observed in the `BaseActivity` as we'll see in the **View Module**; this way we can handle all network errors in one place. You can easily modify that to emit the error to upper layers instead if you'd like.

* Here we map the `ErrorNetworkEntity` to an `ErrorRepoEntity`:
```kotlin
taskNetworkDao.errorNetwork.subscribe { errorNetworkEntity ->
    insertErrorNetwork(
            errorNetworkRepoNetworkMapper.upstream(
                    errorNetworkEntity
            )
    )
}.addTo(disposables)
```
* In the `insertNetworkError(...)` method we map the `ErrorRepoEntity` to an `ErrorDatabaseEntity` and then `execute()`:
```kotlin
fun insertErrorNetwork(errorNetworkRepoEntity: ErrorNetworkRepoEntity) {
    errorNetworkDatabaseDao.insert(
            errorNetworkRepoDatabaseMapper.downstream(errorNetworkRepoEntity)
    ).execute()
}
```

### Tests:
Repository tests are under the `test` sub-package. Tests in the **Repository** layer mock all data sources because we want to test it as an independent module. Please refer to this template's example code; it's fairly well documented with comments.

## ViewModel Module
The ViewModel module takes care of our business logic and emitting view-specific information so that the **View** can know what to do. As usual, we have `models` and `mappers` sub-packages that have more of the same of what we already explained. As for the `viewmodels` sub-package:
* It has a `BaseViewModel` that for now will only hold a `CompositeDisposable` and clears it on `onCleared()`. This `BaseViewModel` should be extended by all other ViewModels.
* Each **ViewModel** will have a reference to all the repositories it needs:
```kotlin
private lateinit var taskRepository: TaskRepository
```
* Each **ViewModel** will also have two `init` methods just like in the **Repository** layer for testing purposes.
* Each **ViewModel** is responsible to convert `Rx` to `LiveData`; so in the case of `allTasks` we'll have:
  * A private `allTasks` property:
  ```kotlin
  private var allTasks = MutableLiveData<List<TaskViewModelEntity>>()
  ```
  * A public getter to conceal the `Mutable` part of `allTasks` (so that the **UI** can't modify `allTasks` directly):
  ```kotlin
  fun getAllTasks(): LiveData<List<TaskViewModelEntity>> = allTasks
  ```
  * Then we subscribe to the repository layer's `Rx` tasks, we map the list of `TaskRepoEntity` to a list of `TaskViewModelEntity`, and then we post them to `allTasks`:
  ```kotlin
  taskRepository.getTasks().subscribe { taskRepoEntityList ->
      allTasks.postValue(
              taskRepoEntityList.map { taskRepoEntity ->
                  taskViewModelRepoMapper.upstream(taskRepoEntity)
              }.reversed()
      )
  }.addTo(disposables)
  ```
  * We do the same with `isRetrievingTasks`:
  ```kotlin
  taskRepository.isRetrievingTasks.subscribe {
      isRetrievingTasks.postValue(it)
  }.addTo(disposables)
  ```
* `onCleared()` will clear both ViewModel's and Repository's `CompositeDisposable`:
```kotlin
override fun onCleared() {
    super.onCleared()
    taskRepository.clearDisposables()
}
```

### ErrorNetworkViewModel:
This ViewModel will reference `ErrorRepository` and will be used by the `BaseActivity` to handle all errors uniformly.

### Tests:
ViewModel tests are under the `test` sub-package. Tests in the **ViewModel** layer mock all repositories because we want to test it as an independent module. Please refer to the example code that I have, it's fairly well documented with comments.

## View Module
The View module takes care of our **UI** only; it doesn't know or care about the business logic, and only knows and cares about displaying UI elements, moving UI elements around, navigation, and things like that. If you spot business logic in your **View** layer then you're probably doing something wrong. As usual, we have `models` and `mappers` sub-packages that have more of the same of what we already explained. As for the `activities` and `fragments` sub-packages:

### BaseActivity:
* `BaseActivity` extends `AppCompatActivity`.
* It initializes `AndroidThreeTen` so that we can use some of the methods in the `OffsetDateTime` class such as `now()`:
```kotlin
AndroidThreeTen.init(application)
```
* It sets up the `NavigationUtil` so that we can use `addOverlay()` and `removeOverlay()`:
```kotlin
NavigationUtil.setup(R.id.clContainer)
```
* It has an `ErrorNetworkViewModel` to handle all errors uniformly. It looks at the top error then calls `handleErrorNetwork(errorNetworkViewEntity: ErrorNetworkViewEntity)` which should be overridden in your `MainActivity` or whatever you want to call it.
* `BaseActivity` also has a `resolveErrorNetwork()`; this will delete the network error from the database which will consequently emit the new error list without the one we just resolved (because we are observing the network errors in the database).
* It invokes `removeOverlay(...)` on `onResume()` in case you decided to use `addOverlay()` on navigation.
* It should be extended by your activity.
* It looks like this:
```kotlin
open class BaseActivity : AppCompatActivity() {
    private lateinit var errorNetworkVM: ErrorNetworkViewModel
    private var currentErrorNetwork: ErrorNetworkViewModelEntity? = null
    private val errorNetworkViewViewModelMapper = ErrorNetworkViewViewModelMapper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize AndroidThreeTen
        AndroidThreeTen.init(application)

        // Setup main fragment id and main constraint layout id in NavigationUtil
        NavigationUtil.setup(R.id.clContainer)

        // Initialize ErrorsNetwork observable
        errorNetworkVM = ViewModelProviders.of(this).get(ErrorNetworkViewModel::class.java)
        errorNetworkVM.init()

        errorNetworkVM.getErrorsNetwork().observe(
                this,
                Observer { errorNetworkVMEntityList ->

                    // If all errors were handled
                    if (errorNetworkVMEntityList.isEmpty()) {
                        currentErrorNetwork = null
                        return@Observer
                    }

                    val firstID = errorNetworkVMEntityList.first().id
                    val currentID = currentErrorNetwork?.id

                    // We haven't handled current error
                    if (firstID == currentID) {
                        return@Observer
                    }

                    currentErrorNetwork = errorNetworkVMEntityList.first()
                    currentErrorNetwork?.let { currentError ->
                        handleErrorNetwork(errorNetworkViewViewModelMapper.upstream(currentError))
                    }
                }
        )
    }

    // To be handled in subclasses
    open fun handleErrorNetwork(errorNetworkViewEntity: ErrorNetworkViewEntity) { }

    protected fun resolveErrorNetwork() {
        currentErrorNetwork?.let {
            errorNetworkVM.deleteErrorNetwork(it)
        }
    }

    override fun onResume() {
        super.onResume()

        removeOverlay(this)
    }
}
```

### MainActivity:
```kotlin
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar(toolbar, showUp = false, title = getString(R.string.app_name))
    }

    override fun handleErrorNetwork(errorNetworkViewEntity: ErrorNetworkViewEntity) {
        Snackbar.make(
                clContainer,
                "${errorNetworkViewEntity.action} failed with ${errorNetworkViewEntity.code}",
                Snackbar.LENGTH_LONG
        ).addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                resolveErrorNetwork()
            }
        }).show()
    }
}
```

### BaseFragment:
This should be extended by all of your other fragments; as of now it just invokes `removeOverlay()` on `onResume()` and it looks like this:
```kotlin
open class BaseFragment : Fragment() {
    override fun onResume() {
        super.onResume()

        if (activity != null) { removeOverlay(activity as FragmentActivity) }
    }
}
```

### General Notes:
* We should declare the **ViewModel** as a `private lateinit var':
```kotlin
private lateinit var taskVM: TaskViewModel
```
* In `onCreate` we should initialize it and we don't forget to call `init()`:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    taskVM = ViewModelProviders.of(this).get(TaskViewModel::class.java)
    taskVM.init()
}
```
* We should just inflate the layout in `onCreateView`:
```kotlin
override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return container?.inflate(R.layout.fragment_main)
}
```
* We should manipulate the view after (and only after) `onViewCreated`; this is because before that `Kotlin Extensions` can't access the views.
* Check the template's example code to see what you can do.

### Tests:
View tests are under the `androidTest` sub-package; they are **Espresso** tests. Please refer to this template's example code; it's fairly well documented with comments.

# Final Thoughts
This is still in **Alpha 2** stage. Feedback is much appreciated at this stage; if  you think I'm doing something wrong, or if you have a suggestion please let me know. Thanks!
