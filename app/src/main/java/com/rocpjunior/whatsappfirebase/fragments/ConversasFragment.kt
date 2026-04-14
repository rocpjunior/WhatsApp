package com.rocpjunior.whatsappfirebase.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.rocpjunior.whatsappfirebase.R
import com.rocpjunior.whatsappfirebase.activities.Mensagens
import com.rocpjunior.whatsappfirebase.adapters.ConversasAdapter
import com.rocpjunior.whatsappfirebase.databinding.FragmentConversasBinding
import com.rocpjunior.whatsappfirebase.model.Conversa
import com.rocpjunior.whatsappfirebase.model.Usuario
import com.rocpjunior.whatsappfirebase.utils.exibirMensagem


class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventSnapshot: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentConversasBinding.inflate(
           inflater, container, false
       )

        conversasAdapter = ConversasAdapter{ conversa ->
            val intent = Intent(context, Mensagens::class.java)

            val usuario = Usuario(
                id = conversa.idDestinatario,
                nome = conversa.nome,
                foto = conversa.foto
            )
            intent.putExtra("dados", usuario)
            //intent.putExtra("origem", Constantes.ORIGEM_CONVERSA )
            startActivity(intent)
        }
        binding.rvConversas.adapter = conversasAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        listenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventSnapshot.remove()
    }

    private fun listenerConversas() {
        val idRemetente = firebaseAuth.currentUser?.uid
        if(idRemetente != null){
            eventSnapshot = firestore
                .collection("conversas")
                .document(idRemetente)
                .collection("ultimas_conversas")
                .orderBy("data", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, error ->
                    if(error != null){
                        activity?.exibirMensagem("Tem alguma coisa de errado com as conversas ;-;")
                    }

                    val listaConversas = mutableListOf<Conversa>()
                    val documentos = querySnapshot?.documents

                    documentos?.forEach { documentSnapshot ->
                        val conversa = documentSnapshot.toObject(Conversa::class.java)
                        if (conversa != null){
                            listaConversas.add(conversa)
                        }
                    }
                    if(listaConversas.isNotEmpty()){
                        conversasAdapter.adicionarLista(listaConversas)
                    }
                }
        }
    }
}