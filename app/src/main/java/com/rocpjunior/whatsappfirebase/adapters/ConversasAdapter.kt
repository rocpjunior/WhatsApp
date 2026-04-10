package com.rocpjunior.whatsappfirebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.rocpjunior.whatsappfirebase.databinding.ItemMensagemDestinatarioBinding
import com.rocpjunior.whatsappfirebase.databinding.ItemMensagemRemetenteBinding
import com.rocpjunior.whatsappfirebase.model.Mensagem
import com.rocpjunior.whatsappfirebase.utils.Constantes

class ConversasAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listaMensagens = emptyList<Mensagem>()
    fun adicionarLista (lista: List<Mensagem>){
        listaMensagens = lista
        notifyDataSetChanged()
    }

    class MensagensRemetenteViewHolder(
        private val binding: ItemMensagemRemetenteBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(mensagem: Mensagem){
            binding.textRemetente.text = mensagem.mensagem
        }

        companion object{
            fun inflarLayout(parent: ViewGroup): MensagensRemetenteViewHolder {
                val inflador = LayoutInflater.from(parent.context)
                val itemView = ItemMensagemRemetenteBinding.inflate(
                    inflador,
                    parent,
                    false
                )
                return MensagensRemetenteViewHolder(itemView)
            }
        }
    }

    class MensagensDestinatarioViewHolder(
        private val binding: ItemMensagemDestinatarioBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(mensagem: Mensagem){
            binding.textDestinatario.text = mensagem.mensagem
        }

        companion object{
            fun inflarLayout(parent: ViewGroup): MensagensDestinatarioViewHolder {
                val inflador = LayoutInflater.from(parent.context)
                val itemView = ItemMensagemDestinatarioBinding.inflate(
                    inflador,
                    parent,
                    false
                )
                return MensagensDestinatarioViewHolder(itemView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val mensagem =  listaMensagens[position]
        val idUsuarioLoado = FirebaseAuth.getInstance().currentUser?.uid.toString()

        return if( idUsuarioLoado == mensagem.idUsuario){
            Constantes.BINARIO_REMETENTE
        }else{
            Constantes.BINARIO_DESTINATARIO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == Constantes.BINARIO_REMETENTE)
            return MensagensRemetenteViewHolder.inflarLayout(parent)
            return MensagensDestinatarioViewHolder.inflarLayout(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        when(holder){
            is MensagensRemetenteViewHolder -> holder.bind(mensagem)
            is MensagensDestinatarioViewHolder -> holder.bind((mensagem))
        }
    }

    override fun getItemCount(): Int {
        return listaMensagens.size
    }
}