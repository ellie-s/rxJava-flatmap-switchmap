package com.ellie.switchmapapp

import android.graphics.Bitmap
import com.ellie.image.ImageRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject


class MainFlatMapViewModel(private val imageRepository: ImageRepository) {

    private val clickEvents = BehaviorSubject.create<String>()

    fun images(): Observable<Bitmap> =
            clickEvents
                    .flatMapSingle { imageRepository.image(it) }
                    .observeOn(AndroidSchedulers.mainThread())


    fun onButtonClick(text: String) {
        clickEvents.onNext(text)
    }
}
