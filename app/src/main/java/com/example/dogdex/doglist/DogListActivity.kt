package com.example.dogdex.doglist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogdex.allDogs
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.databinding.ActivityDogListBinding

class DogListActivity : AppCompatActivity() {

    private val dogListViewModel: DogListViewModel by viewModels()
    private val adapter = DogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recycler = binding.dogRecycler
        recycler.layoutManager = LinearLayoutManager(this)

        recycler.adapter = adapter

        dogListViewModel.dogList.observe(this) { dogList ->
            adapter.submitList(dogList)
        }

        dogListViewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseState.Loading -> binding.progress.visibility = View.VISIBLE
                is ApiResponseState.Success -> binding.progress.visibility = View.GONE
                is ApiResponseState.Error -> {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(this,status.messageId,Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}