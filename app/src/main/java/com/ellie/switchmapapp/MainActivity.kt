package com.ellie.switchmapapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ellie.image.ImageRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**
     * Fetching apple image will take 3 seconds, however, banana, orange will be fetched instantly.
     *
     * [MainFlatMapViewModel] will not cancel previous request. Clicking apple, banana, orange order
     * will result in apple image eventually.
     *
     * If you replace [MainFlatMapViewModel] with [MainSwitchMapViewModel], then it will cancel
     * previous request.Clicking apple, banana, orange order will result in orange image.
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = MainFlatMapViewModel(ImageRepository(this))
        appleButton.setOnClickListener { viewModel.onButtonClick("apple") }
        bananaButton.setOnClickListener { viewModel.onButtonClick("banana") }
        orangeButton.setOnClickListener { viewModel.onButtonClick("orange") }

        viewModel.images()
                .subscribe { imageView.setImageBitmap(it) }
    }
}
