package com.example.todofriends.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todofriends.databinding.ActivityHomeBinding
class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNav()
    }

    private fun setupBottomNav() {
        binding.bottomNavigationView.setOnItemSelectedListener { item -> when (item.itemId)
        }
    }
}