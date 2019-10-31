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

Each mapper will extend an interface that looks like this:
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
This is a collection of extensions for **Rx** that aid in invoking **database** queries (without continuously observing), and in making **network** calls. It also has an **object** that has a **boolean** variable **isTesting** which should be set to **true** in tests, and it has a method **getScheduler** which will return the appropriate scheduler depending on **isTesting**. The **object** looks like this:
```kotlin
object RxKotlinExtensions {
    var isTesting = false
    fun getScheduler(): Scheduler = if (isTesting) Schedulers.trampoline() else Schedulers.io()
}
```
As for the extensions themselves; let's go through them one by one. The first one executes a **Completable** which aids in simple **database** queries such as a **delete** if no value is needed back. The function **Completable.execute(...)** takes an optional **onSuccess()** and an optional **onFailure(throwable: Throwable)** which can be used to handle those two cases.

For **database** queries that return simple values such as an **insert** returning an **id** I have a **Single<T>** extension named **Single<T>.execute(...)**. It also takes an optional **onSuccess(value: T)** and an optional **onFailure(throwable: Throwable)**.

For **database** queries that can be continuously observed but we need to make a one-time call to them I have a **Flowable<T>** extenstion named **Flowable<T>.getValue(...)** that takes a required **onSuccess(value: T)** and an optional **onFailure(throwable: Throwable)**.
 
For **network** requests I have an **Observable<Response<T>>** extension named **Observable<Response<T>>.execute(...). It takes a required **onSuccess(value: Response<T>)**, an optional **onFailure(throwable: Throwable)**, and an optional **onComplete()**.

When we dive deeper in the layers we will see examples of those used which will make it clearer.

### Foundation and View

### Navigation
