# RxJava: FlatMap vs SwitchMap
---

This repository demonstrates difference between FlatMap and SwitchMap operation.

**FlatMap** is popular transformation operation widely used by developers using Rx.
Put it simply, it will merge multiple observables into one that emits items in no particular order.
For some use cases, it is perfectly fine to use this transformation.
However, there are situations where you want more efficient flow and preserve the order of emitted items.

**SwitchMap** is another Rx transformation operation that is different to FlatMap in that it will unsubscribe from previous observable after emitting new one ensuring that only items from most recently emitting observable are emitted.

Look at [this marble diagram](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/switchMapSingle.o.png) for better understanding.

## Example

MainActivity has an `ImageView` to display selected fruit image and three buttons.
Clicking any of the buttons will fetch an image with corresponding fruit and update the `ImageView`.
Fetching an apple image will take 3 seconds and others will be fetched instantly.


### FlatMap

```Kotlin
fun images(): Observable<Bitmap> =
           clickEvents
                   .flatMapSingle { imageRepository.image(it) }
                   .observeOn(AndroidSchedulers.mainThread())
```

![Using FlatMap](https://github.com/ellie-s/rxJava-flatmap-switchmap/blob/master/demo/flatmap.gif)

Notice that buttons were clicked in the following order: `Apple`, `Banana`, `Orange`
Even though `Orange` was the last clicked item, ImageView eventually contain an image of apple.

That is because `FlatMap` does not emit items in any particular order.

### SwitchMap

```Kotlin
fun images(): Observable<Bitmap> =
           clickEvents
                   .switchMapSingle { imageRepository.image(it) }
                   .observeOn(AndroidSchedulers.mainThread())
```

![Using SwitchMap](https://github.com/ellie-s/rxJava-flatmap-switchmap/blob/master/demo/switchmap.gif)

Now, `ImageView` displays correct image. It shows last clicked image.
SwitchMap canceled previous long running apple image request so it didn't mess with the final state of `ImageView`.

## Play by yourself

Run the app, play with it and have fun.
If you just run the app, it will use `FlatMap`. If you want to use `SwitchMap`,
replace viewModel with [MainSwitchMapViewModel](https://github.com/ellie-s/rxJava-flatmap-switchmap/blob/master/app/src/main/java/com/ellie/switchmapapp/MainSwitchMapViewModel.kt) in [MainActivity](https://github.com/ellie-s/rxJava-flatmap-switchmap/blob/master/app/src/main/java/com/ellie/switchmapapp/MainActivity.kt)

From
```Kotlin
 val viewModel = MainFlatMapViewModel(ImageRepository(this))
```
To
```Kotlin
 val viewModel = MainSwitchMapViewModel(ImageRepository(this))
```

## Unit Testing
How do we test the view model to guarantee the most recent item emitted?
Check this code: [FlatMapSwitchMapTest.kt](https://github.com/ellie-s/rxJava-flatmap-switchmap/blob/master/app/src/test/java/com/ellie/switchmapapp/FlatMapSwitchMapTest.kt)

```Kotlin
// given
val scheduler = TestScheduler()
whenever(imageRepository.image(FIRST_ID))
      .thenReturn(Single.just(FIRST_BITMAP).delay(5, SECONDS, scheduler))
whenever(imageRepository.image(SECOND_ID))
      .thenReturn(Single.just(SECOND_BITMAP).delay(2, SECONDS, scheduler))

// when
val observer = switchMapViewModel.images().test()
switchMapViewModel.onButtonClick(FIRST_ID)
switchMapViewModel.onButtonClick(SECOND_ID)
scheduler.advanceTimeBy(10, SECONDS)

// then
observer.assertValueCount(1)
        .assertValue(SECOND_BITMAP)
```
