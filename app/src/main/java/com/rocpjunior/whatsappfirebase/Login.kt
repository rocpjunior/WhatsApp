package com.rocpjunior.whatsappfirebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.rocpjunior.whatsappfirebase.databinding.ActivityLoginBinding
import com.rocpjunior.whatsappfirebase.utils.exibirMensagem

class Login : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var senha: String

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        click()
        //firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarLoginUsuario()
    }

    private fun verificarLoginUsuario() {
        val usuarioAtual = firebaseAuth.currentUser
        if(usuarioAtual != null) {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }

    fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if(email.isNotEmpty()){
            binding.textInputLoginEmail.error = null
            if(senha.isNotEmpty()) {
                binding.textInputLoginSenha.error = null
                return true
            } else{
                binding.textInputLoginSenha.error = "Preencha o campo da Senha"
                return false
            }
        } else{
            binding.textInputLoginEmail.error = "Preencha o campo do E-Mail"
            return false
        }
    }

    private fun click() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, Cadastro::class.java)
            )
        }
        binding.btnEntrar.setOnClickListener {
            if(validarCampos()) {
                entrarUsuario()
            }
        }
    }

    fun entrarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener {
            exibirMensagem("Login realizado com Sucesso =D")
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }.addOnFailureListener { erro ->
            try {
                throw erro
            } catch (erroEmailIncorreto: FirebaseAuthInvalidUserException){
                erroEmailIncorreto.printStackTrace()
                exibirMensagem("E-mail não cadastrado")
            } catch (erroDadosIncorretos: FirebaseAuthInvalidCredentialsException){
                erroDadosIncorretos.printStackTrace()
                exibirMensagem("E-mail ou senha estão incorretos")
            }
        }
    }
}