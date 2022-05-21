package com.dicoding.picodiploma.storyApp.view.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.storyApp.model.UserPreference
import com.dicoding.picodiploma.storyApp.view.ViewModelFactory
import com.dicoding.picodiploma.storyApp.view.detail.DetailActivity
import com.dicoding.picodiploma.storyApp.view.login.LoginViewModel
import com.dicoding.picodiploma.storyApp.view.story.AdapterStory
import com.dicoding.picodiploma.storyApp.view.story.AddStoryActivity
import com.dicoding.picodiploma.storyApp.view.story.ListStory
import com.dicoding.picodiploma.storyApp.view.story.LoadingStateAdapter
import com.dicoding.picodiploma.storyApp.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var storyAdapter: AdapterStory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        storyAdapter = AdapterStory()
        storyAdapter.setOnItemClickCallback(object: AdapterStory.OnItemClickCallback{
            override fun onItemClick(result: ListStory) {
                Intent(this@MainActivity, DetailActivity::class.java).apply {
                    this.putExtra(DetailActivity.EXTRA_NAME, result.name)
                    this.putExtra(DetailActivity.EXTRA_DESCRIPTION, result.description)
                    this.putExtra(DetailActivity.EXTRA_PHOTO, result.photoUrl)
                    startActivity(this)
                }
            }
        })

        binding.addPhoto.setOnClickListener { addStory() }

        setupView()
        setupViewModel()
        setupListStories()
        showLoading(true)
    }

    override fun onResume() {
        super.onResume()
        getStoryFromApi()
    }

    private fun addStory() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    private fun getStoryFromApi() {
        showLoading(false)
        loginViewModel.getToken().observe(this) { token ->
            mainViewModel.stories("Bearer $token").observe(this) {
                storyAdapter.submitData(lifecycle, it)
                showLoading(false)
                Log.d(TAG, "getStoryFromApi: $it")
            }
        }
    }

    private fun setupListStories() {
        binding.apply {
            rvListStories.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStories.setHasFixedSize(true)
            rvListStories.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[LoginViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                loginViewModel.setLoginData("", "", false)
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(state: Boolean){
        if (state){
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }

}