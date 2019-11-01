# Current Version
1.0.0 Alpha 1

# Overview
This is a base app, a template, a starting point for any android project. This template uses **MVVM Clean architecture** to the best of my understanding; it uses **reactive patterns**, **Room** for persistence, and **Retrofit2** for networking. I will start by describing and explaining everything layer by layer, starting from the lowest layers all the way until the **View** layer.

<img align="center" alt="Architecture Overview" src="/README_FILES/architecture_full.png" />

## Quick Points
Here are a few things to keep in mind before you read further:
* This project is written completely in **Kotlin**.
* This project is **modular**; each layer lives inside its own module and has its own dependencies.
* Each layer sees only the layers directly below it:
  * The **View** layer only sees the **ViewModel** layer.
  * The **ViewModel** layer only sees the **Repository** layer.
  * The **Repository** layer only sees the **Database** and the **Network** layers.
  * **EXCEPTION**: all layers see a **util** layer that has helper methods and extensions.
* Each layer has its own **model** and **maps** between them using custom mappers.
* This project uses **RxKotlin** & **RxAndroid** in the **Database**, **Network**, and **Repository** layers, **LiveData** in the **View** layer, and maps from **Rx** to **LiveData** in the **ViewModel** layer.
* The **Database** is the single source of truth.
* This project comes with a running example that has **Unit Tests** and **Instrumented Tests**.

## Model Custom Mappers
Each layer will have its own **models**, along with a **mapper** per model per direct layer below it. So a **TaskViewModelEntity** will have a mapper that maps to and from a **TaskRepoEntity**. A **TaskRepoEntity** will have two mappers in the **Repository** layer that will map it to and from both **TaskNetworkEntity** and **TaskDatabaseEntity**.

Each mapper will implement an interface that looks like this:
```kotlin
interface MapperInterface<T, V> {
    // Map from current layer entity to specific layer below entity
    fun downstream(currentLayerEntity: T): V

    // Map from specific layer below entity to current layer entity
    fun upstream(nextLayerEntity: V): T
}
```
An example would be the **TaskViewEntity** mapper in the **View** layer which would look like this:
```kotlin
class TaskViewViewModelMapper : MapperInterface<TaskViewEntity, TaskViewModelEntity> {
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
For more info on how to use please check the example code I have in this project. It's **worth mentioning** here that **MapperInterface** doesn't literally exist in this example project, instead I have an **interface per layer**; I can see this changing some time in the future and maybe I can move the interface to the **util** layer.

### Important Note:
One might argue that we can share a **model** module between all layers and that would make our lives easier, especially with the **Reactive** nature of the app; in fact this is how I started this project out. However, the benefits of this more separate approach can be seen in the example starting app itself. As you can see, **TaskNetworkEntity** has a `uuid` and no `id`, **TaskDatabaseEntity** has both `id` and `uuid` for syncing purposes, **TaskRepoEntity** has both to coordinate between those two, and **TaskViewModelEntity** only has `id` because in a **single source of truth** approach where the **database** is the single source of truth the **ViewModel** layer has no business knowing about the `uuid`.

In summary, different layers care about different properties and that's why I decided to go with the **model per layer** approach.

## Util Module
This module's purpose is to provide helper methods, extensions, super classes that should be useful in all other layers. This module was created in the earliest stages of development and needs refinement, but as of now here's a quick rundown of what it has.

### ErrorNetworkTypes:
This is an **enum** for **network error types** which I use when catching network errors and recording them in the **database** (which again is the **single source of truth**), and it looks like this:
```kotlin
enum class ErrorNetworkTypes {
    HTTP,
    TIMEOUT,
    IO,
    OTHER
}
```

### RxKotlinExtensions:
This is a collection of extensions for **Rx** that aid in invoking **database** queries (without continuously observing), and in making **network** calls. It also has an `object` that has a **boolean** variable `isTesting` which should be set to `true` in tests, and it has a method `getScheduler` which will return the appropriate scheduler depending on `isTesting`. The `object` looks like this:
```kotlin
object RxKotlinExtensions {
    var isTesting = false
    fun getScheduler(): Scheduler = if (isTesting) Schedulers.trampoline() else Schedulers.io()
}
```
As for the extensions themselves; let's go through them one by one:

`Completable.execute(...)`: used in simple **database** queries that return no values such as **delete**. It takes in two parameters:
* `onSuccess()`: **optional** success handler.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.

`Single<T>.execute(...)`: used in simple **database** queries that return simple values such as an `id` after `insert`. It takes in two parameters:
* `onSuccess(value: T)`: **optional** success handler with value.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.

`Flowable<T>.getValue(...)`: used to get a value **one time** from an otherwise continuously observed **database** query. It takes in two parameters:
* `onSuccess(value: T)`: **required** success handler with value.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.

`Observable<Response<T>>.execute(...)`: used to invoke a **network** call. It takes in three parameters:
* `onSuccess(value: Response<T>)`: **required** success handler with value.
* `onFailure(throwable: Throwable)`: **optional** failure handler with `Throwable`.
* `onComplete()`: **optional** completion handler.

Usage examples will be shown as we dive deeper into each layer. You can also look at the example code I have in this project.

### NavigationHelper:
This has two methods to add and remove a blocking overlay programmatically on top of the **Activity**; I added this to fix the problem of double clicking on a button that navigates to a different **Activity**/**Fragment**. The methods `addOverlay(activity: FragmentActivity)` and `removeOverlay(activity: FragmentActivity)` both take in a `FragmentActivity` as a parameter. The idea is to add the overlay on click, and remove overlay on `onResume`. This will work as long as:
* **Activity** uses a full screen `ConstraintLayout`.
* The `ConstraintLayout`'s `id` is passed on `onCreate` of the **Activity** using `NavigationUtil.setup(R.id.[id])`.

### NavigationUtil:
This has two things as of this moment:
* `constraintLayoutResId: Int` to store the **Activity**'s `ConstraintLayout`'s `id` to be used in adding and removing the overlay.
* `AppCompatActivity.setupToolbar(...)` extension to shorten setting up a `Toolbar`.
* **NOT IMPORTANT**: this had many more extensions to help with the navigation in general; but that was before **Google** introduced the new **Navigation Components** and **Jetpack** so I removed all of it.

### ViewUtil:
This only has a `ViewGroup.inflate` helper method that shortens inflating views in **Fragments**.

### FoundationActivity & FoundationFragment:
Both basically only remove the overlay if it exists on `onResume`; might be moved to `BaseActivity` and `BaseFragment` later unless I find better uses for them. The idea is to always extend those two (in this case here we have a `BaseActivity` and a `BaseFragment` that extend those and get extended from everywhere else).

# Deeper Look
Now we shall move into the actual layers / modules of the base app. Each layer is a module in our case so I'll be using the words *layer* and *module* interchangeably.

## Database Module
The **database** module takes care of everything **database** related, and no other layer has to know about the **database** dependencies or how the **database** layer interacts with the database. It uses **Room** with **RxKotlin**. Here's a detailed rundown of what this includes at the moment:

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
The database doesn't know how to convert complicated objects into primitives, storable data types. That's why we need **Type Converters**. For example, in the previously shown `TaskDatabaseEntity` we have a column of type `OffsetDateTime`; and we'd write a type converter that we'll declare in the `AppDatabase.kt` file later (described in the **AppDatabase.kt** section below). The type converter for `OffsetDateTime` under the sub-package `type_converters` looks like this:
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
This is where we define **query methods** to access the database. This is pretty straight forward; if you don't know how **Room** and **RxKotlin** can work together do your research. Here are a few examples of what I'm using:

This `getByUUID(uuid: String)` method will retrieve tasks by `uuid` and will return a `Flowable` so that you can observe the changes on those tasks:
```kotlin
@Query("SELECT * from tasks where uuid = :uuid")
fun getByUUID(uuid: String): Flowable<List<TaskDatabaseEntity>>
```

This `insert(taskDatabaseEntity: TaskDatabaseEntity)` method will insert a task into the database and will return a `Single<Long>` for the inserted task's `id`:
```kotlin
@Insert(onConflict = OnConflictStrategy.REPLACE)
fun insert(taskDatabaseEntity: TaskDatabaseEntity): Single<Long>
```

This `deleteAll()` method will delete all tasks in the database and will just return a `Completable`:
```kotlin
@Query("DELETE from tasks")
fun deleteAll(): Completable
```

Those queries can be used either by manually subscribing and doing the ***Rx*** thing; or you can just rely on the `RxKotlinExtensions` that I described earlier which will do the same thing for you. Also, feel free to write other extensions if you want to expand what's already there.

### AppDatabase.kt:
Here all you need to do is define *4 things*:
* **Entities**:
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
Database tests are under the `androidTest` sub-package; the reason why they're not under the `test` sub-package is because we need the `ApplicationContext` to test the database. Please refer to the example code that I have, it's fairly well documented with comments.

## Network Module

## Repository Module

## ViewModel Module

## View Module

# First Use

# Final Thoughts
