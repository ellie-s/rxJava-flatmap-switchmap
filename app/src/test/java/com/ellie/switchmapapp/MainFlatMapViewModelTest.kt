package com.ellie.switchmapapp

import android.graphics.Bitmap
import com.ellie.image.ImageRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit.SECONDS

class MainFlatMapViewModelTest {

    private val imageRepository = mock<ImageRepository>()
    private val flatMapViewModel = MainFlatMapViewModel(imageRepository)
    private val switchMapViewModel = MainSwitchMapViewModel(imageRepository)

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `SwitchMap should cancel previous request`() {
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
    }

    @Test
    fun `FlatMap should not cancel previous request`() {
        // given
        val scheduler = TestScheduler()
        whenever(imageRepository.image(FIRST_ID))
                .thenReturn(Single.just(FIRST_BITMAP).delay(5, SECONDS, scheduler))
        whenever(imageRepository.image(SECOND_ID))
                .thenReturn(Single.just(SECOND_BITMAP).delay(2, SECONDS, scheduler))

        // when
        val observer = flatMapViewModel.images().test()
        flatMapViewModel.onButtonClick(FIRST_ID)
        flatMapViewModel.onButtonClick(SECOND_ID)
        scheduler.advanceTimeBy(10, SECONDS)

        // then
        observer.assertValueCount(2)
                .assertValueAt(0, SECOND_BITMAP)
                .assertValueAt(1, FIRST_BITMAP)
    }

    companion object {
        const val FIRST_ID = "first"
        const val SECOND_ID = "second"
        val FIRST_BITMAP = mock<Bitmap>()
        val SECOND_BITMAP = mock<Bitmap>()
    }
}
