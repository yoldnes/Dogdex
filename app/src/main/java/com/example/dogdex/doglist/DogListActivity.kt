package com.example.dogdex.doglist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import coil.annotation.ExperimentalCoilApi
import com.example.dogdex.DogDetail.DogDetailActivity
import com.example.dogdex.DogDetail.DogDetailActivity.Companion.DOG_KEY
import com.example.dogdex.DogDetail.DogDetailComposeActivity
import com.example.dogdex.api.ApiResponseState
import com.example.dogdex.databinding.ActivityDogListBinding

private const val GRID_SPAN_COUNT = 3

@ExperimentalCoilApi
class DogListActivity : AppCompatActivity() {

    private val dogListViewModel: DogListViewModel by viewModels()
    private val adapter = DogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recycler = binding.dogRecycler
        recycler.layoutManager = GridLayoutManager(this, GRID_SPAN_COUNT)

        recycler.adapter = adapter

        dogListViewModel.dogList.observe(this) { dogList ->
            adapter.submitList(dogList)
        }

        adapter.setOnClickListener {
            val intent = Intent(this, DogDetailComposeActivity::class.java)
            intent.putExtra(DOG_KEY, it)
            startActivity(intent)
        }

        dogListViewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseState.Loading -> binding.progress.visibility = View.VISIBLE
                is ApiResponseState.Success -> binding.progress.visibility = View.GONE
                is ApiResponseState.Error -> {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(this, status.messageId, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}