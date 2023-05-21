package com.example.dogdex.DogDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import coil.load
import com.example.dogdex.Main.MainViewModel
import com.example.dogdex.model.Dog
import com.example.dogdex.R
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.databinding.ActivityDogDetailBinding


class DogDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDogDetailBinding
    private val viewModel: DogDetailViewModel by viewModels()

    companion object {
        val DOG_KEY = "dog"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)
        val recognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY, false) ?: false

        if (dog == null) {
            Toast.makeText(this, R.string.error_show_not_found, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        with(binding) {
            this.dog = dog
            maleHeight.text = dog.heightMale.toString()
            maleWeight.text = dog.heightFemale.toString()
            dogIndex.text = getString(R.string.dog_index_format, dog.index)
            lifeExpectancy.text = getString(R.string.dog_expectancy_format, dog.lifeExpectancy)
            dogImage.load(dog.imageUrl)
            closeButton.setOnClickListener {
                if (recognition) {
                    viewModel.addDogToUser(dog.id)
                } else {
                    finish()
                }
            }
        }

        onObserver()
    }

    private fun onObserver() {
        viewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseState.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseState.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    finish()
                }
                is ApiResponseState.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(this, status.messageId, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}