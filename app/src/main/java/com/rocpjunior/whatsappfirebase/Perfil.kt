package com.rocpjunior.whatsappfirebase

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rocpjunior.whatsappfirebase.databinding.ActivityPerfilBinding
import com.rocpjunior.whatsappfirebase.utils.exibirMensagem

class Perfil : AppCompatActivity() {

    private var galeria = false
    private var camera = false

    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        if(uri != null) {
            binding.imgPerfil.setImageURI(uri)
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

    private fun eventosClick() {
        binding.fabGaleria.setOnClickListener {
            if(galeria) {
                gerenciadorGaleria.launch("image/*")
            } else{
                exibirMensagem("Você não concedeu permissão de acesso a Galeria")
                permissoes()
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