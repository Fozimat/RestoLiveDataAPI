package com.fozimat.restolivedataapi

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fozimat.restolivedataapi.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        mainViewModel.restaurant.observe(this, { restaurant ->
            binding.tvTitle.text = restaurant.name
            binding.tvDescription.text = restaurant.description
            Glide.with(this)
                .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
                .into(binding.ivPicture)
        })

        mainViewModel.listReview.observe(this, { consumerReviews ->
            val listReview = consumerReviews.map {
                "${it.review}\n - ${it.name}"
            }

            binding.lvReview.adapter =
                ArrayAdapter(this, R.layout.item_review, listReview)
            binding.edReview.setText("")
        })

        mainViewModel.isLoading.observe(this, {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        mainViewModel.snackbarText.observe(this, {
            it.getContentIfNotHandled()?.let {
                Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
            }
        })

        binding.btnSend.setOnClickListener { view ->
            mainViewModel.postReview(binding.edReview.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}