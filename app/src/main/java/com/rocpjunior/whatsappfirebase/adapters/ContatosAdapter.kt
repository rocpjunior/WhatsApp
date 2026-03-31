package com.rocpjunior.whatsappfirebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rocpjunior.whatsappfirebase.R
import com.rocpjunior.whatsappfirebase.databinding.ItemContatosBinding
import com.rocpjunior.whatsappfirebase.model.Usuario
import com.squareup.picasso.Picasso

class ContatosAdapter: RecyclerView.Adapter<ContatosAdapter.ContatosViewHolder>() {

    private var listaContatos = emptyList<Usuario>()
    fun adicionarLista(lista: List<Usuario>){
        listaContatos = lista
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ) : RecyclerView.ViewHolder(binding.root){

        fun bind(usuario: Usuario){
            binding.textContatoNome.text = usuario.nome
            if (usuario.foto.isNotEmpty()) {
                Picasso.get()
                    .load(usuario.foto)
                    .placeholder(R.drawable.perfil)
                    //.error(R.drawable.perfil)
                    .into(binding.imgContatoFoto)
            } else {
                binding.imgContatoFoto.setImageResource(R.drawable.perfil)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {
        val inflador = LayoutInflater.from(parent.context)
        val itemView = ItemContatosBinding.inflate(
            inflador, parent, false
        )
        return ContatosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val usuario = listaContatos[position]
        holder.bind(usuario)
    }

    override fun getItemCount(): Int {
        return listaContatos.size
    }
}