package com.rocpjunior.whatsappfirebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rocpjunior.whatsappfirebase.R
import com.rocpjunior.whatsappfirebase.databinding.ItemConversasBinding
import com.rocpjunior.whatsappfirebase.model.Conversa
import com.squareup.picasso.Picasso

class ConversasAdapter(
    private val onClick: (Conversa) -> Unit
): RecyclerView.Adapter<ConversasAdapter.ConversasViewHolder>() {

    private var listaConversas = emptyList<Conversa>()
    fun adicionarLista( lista: List<Conversa>){
        listaConversas = lista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {
        val inflador = LayoutInflater.from(parent.context)
        val itemView = ItemConversasBinding.inflate(
            inflador, parent, false
        )
        return ConversasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listaConversas[position]
        holder.bind(conversa)
    }

    override fun getItemCount(): Int {
        return listaConversas.size
    }

    inner class ConversasViewHolder(
        private val binding: ItemConversasBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(conversa: Conversa){
            binding.txtNomeConversa.text = conversa.nome
            binding.txtMensagemConversa.text = conversa.ultimaMensagem
            if (conversa.foto.isNotEmpty()) {
                Picasso.get()
                    .load(conversa.foto)
                    .placeholder(R.drawable.perfil)
                    //.error(R.drawable.perfil)
                    .into(binding.imgConversa)

                binding.clConversa.setOnClickListener {
                    onClick(conversa)
                }
            } else {
                binding.imgConversa.setImageResource(R.drawable.perfil)
            }
        }
    }
}