package com.rocpjunior.whatsappfirebase.activities

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rocpjunior.whatsappfirebase.databinding.ActivityPerfilBinding
import com.rocpjunior.whatsappfirebase.utils.exibirMensagem
import com.squareup.picasso.Picasso

class Perfil : AppCompatActivity() {

    private var galeria = false
    private var camera = false

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        if(uri != null) {
            binding.imgPerfil.setImageURI(uri)
            uploadImagemFirebase(uri)
        } else{
            exibirMensagem("Nenhuma imagem selecionada")
        }
    }

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        toolbar()
        permissoes()
        eventosClick()
    }

    override fun onStart() {
        super.onStart()
        dadosIniciaisPerfil()
    }

    private fun dadosIniciaisPerfil() {
        val idUsuario = firebaseAuth.currentUser?.uid
        if(idUsuario != null) {
            firestore
                .collection("Usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dadosUsuarios = documentSnapshot.data
                    if (dadosUsuarios != null){

                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String

                        binding.editNomePerfil.setText(nome)
                        if (foto.isNotEmpty()){
                            Picasso.get()
                                .load(foto)
                                .into(binding.imgPerfil)
                        }
                    }
                }
        }
    }

    fun uploadImagemFirebase(uri: Uri) {
        val idUsuario = firebaseAuth.currentUser?.uid
        if(idUsuario != null){
            storage
                .getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener { task ->
                    exibirMensagem("Sucesso ao fazer upload da imagem")
                    task.metadata
                        ?.reference
                        ?.downloadUrl
                        ?.addOnSuccessListener { url ->
                            val dados = mapOf("foto" to url.toString())
                            atualizarDadosPerfil(idUsuario, dados)
                        }
                        ?.addOnFailureListener {  }
                }.addOnFailureListener {
                    exibirMensagem("Ocorreu um erro ao fazer upload da imagem")
                }
        }
    }

    fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {
        firestore
            .collection("Usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener { exibirMensagem("Sucesso ao atualizar dados do perfil") }
            .addOnFailureListener { exibirMensagem("Falha ao atualizar dados do perfil") }
    }

    private fun eventosClick() {
        binding.fabGaleria.setOnClickListener {
            if(galeria) {
                gerenciadorGaleria.launch("image/*")
            } else{
                exibirMensagem("Você não concedeu permissão de acesso a Galeria")
                permissoes()
            }
        }
        binding.btnSalvar.setOnClickListener {
            val nomeUsuario = binding.editNomePerfil.text.toString()
            if(nomeUsuario.isNotEmpty()) {
                val idUsuario = firebaseAuth.currentUser?.uid
                if(idUsuario != null) {
                    val dados = mapOf("nome" to nomeUsuario)
                    atualizarDadosPerfil(idUsuario, dados)
                }
            } else{
                exibirMensagem("Preencha um nome para atualizar o nome")
            }
        }
    }

    private fun toolbar() {
        val toolbar = binding.includePerfilToolbar.materialToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun permissoes() {
        camera = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        galeria = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        val permissoesNegadas = mutableListOf<String>()
        if(!camera)
            permissoesNegadas.add(Manifest.permission.CAMERA)
        if(!galeria)
            permissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)

        if(permissoesNegadas.isNotEmpty()) {
            val gerenciadorPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){permissoes ->
                camera = permissoes[Manifest.permission.CAMERA]
                    ?: camera
                galeria = permissoes[Manifest.permission.READ_MEDIA_IMAGES]
                    ?: galeria
            }
            gerenciadorPermissoes.launch(permissoesNegadas.toTypedArray())
        }
    }
}