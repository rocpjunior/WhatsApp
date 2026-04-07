package com.rocpjunior.whatsappfirebase.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.rocpjunior.whatsappfirebase.databinding.ActivityCadastroBinding
import com.rocpjunior.whatsappfirebase.utils.exibirMensagem
import com.rocpjunior.whatsappfirebase.model.Usuario

class Cadastro : AppCompatActivity() {

    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        toolbar()
        eventosClick()
    }

    private fun validarCampos(): Boolean {

        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

        if(nome.isNotEmpty()){
            binding.textInputNome.error = null
            if(email.isNotEmpty()){
                binding.textInputEmail.error = null
                if(senha.isNotEmpty()){
                    binding.textInputSenha.error = null
                    return true
                } else {
                    binding.textInputSenha.error = "Preencha o campo com uma Senha"
                    return false
                }
            } else {
                binding.textInputEmail.error = "Preencha o campo com o seu E-Mail"
                return false
            }
        } else {
            binding.textInputNome.error = "Preencha o campo com o seu Nome"
            return false
        }
    }

    private fun eventosClick() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()){
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(
            email, senha
        ).addOnCompleteListener { resultado ->
            if(resultado.isSuccessful){
                val idUsuario = resultado.result.user?.uid
                if(idUsuario != null) {
                    val usuario = Usuario(idUsuario, nome, email)
                    salvarUsuario(usuario)
                }
            }
        }.addOnFailureListener { erro ->
            try {
                throw erro
            }catch (erroSenhaFraca: FirebaseAuthWeakPasswordException) {
                erroSenhaFraca.printStackTrace()
                exibirMensagem("Senha fraca. Invente outra senha")
            } catch (erroUsuarioExistente: FirebaseAuthUserCollisionException) {
                erroUsuarioExistente.printStackTrace()
                exibirMensagem("Esse e-mail já pertence a outro usuário")
            } catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException) {
                erroCredenciaisInvalidas.printStackTrace()
                exibirMensagem("Email inválido, digite um outro e-mail")
            }
        }
    }

    private fun salvarUsuario(usuario: Usuario) {
        firestore
            .collection("Usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao fazer o Cadastro =D")
                startActivity(
                    Intent(applicationContext, MainActivity::class.java)
                )
            }.addOnFailureListener {
                exibirMensagem("Erro ao fazer o Cadastro =(")
            }
    }

    private fun toolbar() {
        val toolbar = binding.includeToolbar.materialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}