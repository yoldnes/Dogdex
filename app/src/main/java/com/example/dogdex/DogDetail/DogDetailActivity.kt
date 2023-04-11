package com.example.dogdex.DogDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.load
import com.example.dogdex.Dog
import com.example.dogdex.R
import com.example.dogdex.databinding.ActivityDogDetailBinding


class DogDetailActivity : AppCompatActivity() {

    companion object {
        val DOG_KEY = "dog"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)

        if (dog == null) {
            Toast.makeText(this, R.string.error_show_not_found, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        with(binding){
            this.dog = dog
            maleHeight.text = dog.heightMale.toString()
            maleWeight.text = dog.heightFemale.toString()
            dogIndex.text = getString(R.string.dog_index_format,dog.index)
            lifeExpectancy.text = getString(R.string.dog_expectancy_format,dog.lifeExpectancy)
            dogImage.load(dog.imageUrl)
            closeButton.setOnClickListener {
                finish()
            }
        }
    }
}