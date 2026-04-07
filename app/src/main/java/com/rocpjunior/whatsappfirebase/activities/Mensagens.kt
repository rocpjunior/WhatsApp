package com.rocpjunior.whatsappfirebase.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.rocpjunior.whatsappfirebase.R
import com.rocpjunior.whatsappfirebase.databinding.ActivityMensagensBinding
import com.rocpjunior.whatsappfirebase.model.Usuario
import com.rocpjunior.whatsappfirebase.utils.Constantes
import com.squareup.picasso.Picasso


class Mensagens : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }

    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        dadosUsuarios()
        toolbar()
    }

    private fun toolbar() {
        val toolbar = binding.toolbarMensagens
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if(dadosDestinatario != null) {
                binding.textNomePerfil.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imgFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun dadosUsuarios() {
        val extras = intent.extras
        if( extras != null) {
            val origem = extras.getString("origem")
            if(origem == Constantes.ORIGEM_CONTATO){
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dados", Usuario::class.java)
                } else{
                    extras.getParcelable("dados")
                }

            } else if (origem == Constantes.ORIGEM_CONVERSA){

            }
        }
    }
}