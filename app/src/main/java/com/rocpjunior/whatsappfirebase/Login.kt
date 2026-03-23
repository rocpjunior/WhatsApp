package com.rocpjunior.whatsappfirebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.rocpjunior.whatsappfirebase.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        click()
    }

    private fun click() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, Cadastro::class.java)
            )
        }
    }
}