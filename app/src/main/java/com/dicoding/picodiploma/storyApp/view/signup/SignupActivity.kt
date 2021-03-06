package com.dicoding.picodiploma.storyApp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dicoding.picodiploma.storyApp.api.ApiConfig
import com.dicoding.picodiploma.storyApp.api.RegisterResponse
import com.dicoding.picodiploma.storyApp.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
        playAnimation()
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
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnSignUp.setOnClickListener {
            Log.d("signupSubmit", "setupAction: ")
            val name = binding.fillName.editText.toString()
            val email = binding.fillEmail.editText.toString()
            val password = binding.fillPassword.editText.toString()
            when {
                name.isEmpty() -> {
                    Log.d("signupSubmit", "name.isEmpty(): ")
                    binding.fillName.error = "Enter your email"
                }
                email.isEmpty() -> {
                    Log.d("signupSubmit", "email.isEmpty(): ")
                    binding.fillEmail.error = "Enter your email"
                }
                password.isEmpty() -> {
                    Log.d("signupSubmit", "password.isEmpty()")
                    binding.fillPassword.error = "Enter your password"
                }
                password.length < 6 -> {
                    Log.d("signupSubmit", "password.length")
                    binding.fillPassword.error = "Your password must be at least 6 characters"
                }
                else -> {
                    Toast.makeText(this, "waiting...", Toast.LENGTH_SHORT).show()
                    ApiConfig.instances.register(name, email, password).enqueue(object :
                        Callback<RegisterResponse> {
                        override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                            Log.d("Register", name)
                            if(response.isSuccessful) {
                                AlertDialog.Builder(this@SignupActivity).apply {
                                    setTitle("Yeah!")
                                    setMessage("Your account is ready")
                                    setPositiveButton("Next") { _, _ ->
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            } else {
                                Toast.makeText(this@SignupActivity,  response.errorBody()?.string(), Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Toast.makeText(this@SignupActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                    })
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.signupImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.signupText, View.ALPHA, 1f).setDuration(200)
        val nameTextView = ObjectAnimator.ofFloat(binding.name, View.ALPHA, 1f).setDuration(200)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.fillName, View.ALPHA, 1f).setDuration(200)
        val emailTextView = ObjectAnimator.ofFloat(binding.email, View.ALPHA, 1f).setDuration(200)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.fillEmail, View.ALPHA, 1f).setDuration(200)
        val passwordTextView = ObjectAnimator.ofFloat(binding.password, View.ALPHA, 1f).setDuration(200)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.fillPassword, View.ALPHA, 1f).setDuration(200)
        val signup = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(200)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 400
        }.start()
    }
}