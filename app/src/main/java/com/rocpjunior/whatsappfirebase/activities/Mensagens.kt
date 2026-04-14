package com.rocpjunior.whatsappfirebase.activities

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.rocpjunior.whatsappfirebase.adapters.MensagensAdapter
import com.rocpjunior.whatsappfirebase.databinding.ActivityMensagensBinding
import com.rocpjunior.whatsappfirebase.model.Conversa
import com.rocpjunior.whatsappfirebase.model.Mensagem
import com.rocpjunior.whatsappfirebase.model.Usuario
import com.rocpjunior.whatsappfirebase.utils.Constantes
import com.rocpjunior.whatsappfirebase.utils.exibirMensagem
import com.squareup.picasso.Picasso


class Mensagens : AppCompatActivity() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val binding by lazy {
        ActivityMensagensBinding.inflate(layoutInflater)
    }

    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var mensagensAdapter: MensagensAdapter
    private var dadosDestinatario: Usuario? = null
    private var dadosRemetente: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        dadosUsuarios()
        toolbar()
        click()
        recyclerView()
        listener()
    }

    private fun recyclerView() {
        with(binding){
            mensagensAdapter = MensagensAdapter()
            rvMensagens.adapter = mensagensAdapter
            rvMensagens.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun listener() {
        val idRemetente = firebaseAuth.currentUser?.uid
        val idDestinatario = dadosDestinatario?.id
        if(idRemetente != null && idDestinatario != null){

            listenerRegistration =  firestore
                .collection(Constantes.BD_MENSAGENS)
                .document(idRemetente)
                .collection(idDestinatario)
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, errado ->

                    if(errado != null ){
                        exibirMensagem("ERRO ao exibir mensagens!")
                    }
                    val listaMensagens = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->
                        val mensagem = documentSnapshot.toObject(Mensagem::class.java)
                        if(mensagem != null){
                            listaMensagens.add(mensagem)
                            Log.i("mensagens", mensagem.mensagem)
                        }
                    }
                    if(listaMensagens.isNotEmpty()){
                        mensagensAdapter.adicionarLista(listaMensagens)
                    }
                }
        }
    }

    private fun click() {
        binding.fabMensagem.setOnClickListener {
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }
    }

    private fun salvarMensagem(textoMensagem: String) {
        if( textoMensagem.isNotEmpty()){
            val idRemetente = firebaseAuth.currentUser?.uid
            val idDestinatario = dadosDestinatario?.id

            if(idRemetente != null && idDestinatario != null){
                val mensagem = Mensagem(
                    idRemetente, textoMensagem
                )
                salvarMensagemFirestore(idRemetente, idDestinatario, mensagem)
                val conversaRemetente = Conversa(
                    idRemetente, idDestinatario,
                    dadosDestinatario!!.foto, dadosDestinatario!!.nome,
                    textoMensagem)
                    salvarConversaFirestore(conversaRemetente)

                salvarMensagemFirestore(idDestinatario, idRemetente, mensagem)
                val conversaDestinatario = Conversa(
                    idDestinatario, idRemetente,
                    dadosRemetente!!.foto, dadosRemetente!!.nome,
                    textoMensagem)
                    salvarConversaFirestore(conversaDestinatario)

                binding.editMensagem.setText("")
            }
        }
    }

    private fun salvarConversaFirestore(conversa: Conversa) {
        firestore
            .collection("conversas")
            .document(conversa.idRemetente)
            .collection("ultimas_conversas")
            .document(conversa.idDestinatario)
            .set(conversa)
            .addOnFailureListener {
                exibirMensagem("Falha ao salvar conversa")
            }
    }

    private fun salvarMensagemFirestore(idRemetente: String, idDestinatario: String, mensagem: Mensagem) {

        firestore.collection(Constantes.BD_MENSAGENS)
            .document(idRemetente)
            .collection(idDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                exibirMensagem("Falha ao enviar mensagem!")
            }
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

        val idRemetente = firebaseAuth.currentUser?.uid
        if(idRemetente != null){
            firestore
                .collection("Usuarios")
                .document(idRemetente)
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if(usuario != null){
                        dadosRemetente = usuario
                    }
                }
        }

        val extras = intent.extras
        if( extras != null) {
            dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                extras.getParcelable("dados", Usuario::class.java)
            } else{
                extras.getParcelable("dados")
            }
        }
    }
}