package com.example.imc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.imc.R
import android.util.Log


class ImcAdapter(
    private val listaImc: MutableList<Imc>,
    private val onDeleteClicked: (Imc) -> Unit,
    private val onEditClicked: (Imc) -> Unit
) : RecyclerView.Adapter<ImcAdapter.ImcViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImcViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_imc, parent, false)
        return ImcViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImcViewHolder, position: Int) {
        val imc = listaImc[position]
        holder.bind(imc)
    }

    override fun getItemCount(): Int {
        return listaImc.size
    }

    inner class ImcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val pesoTextView: TextView = itemView.findViewById(R.id.peso)
        private val alturaTextView: TextView = itemView.findViewById(R.id.altura)
        private val imcTextView: TextView = itemView.findViewById(R.id.imc)
        private val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        private val editButton: ImageView = itemView.findViewById(R.id.edit_button)

        fun bind(imc: Imc) {
            pesoTextView.text = imc.peso.toString()
            alturaTextView.text = imc.altura.toString()
            imcTextView.text = imc.imc

            // Configurar o clique no botão de deletar
            deleteButton.setOnClickListener {
                onDeleteClicked(imc)
            }

            // Configurar o clique no botão de editar
            editButton.setOnClickListener {
                onEditClicked(imc)
            }
        }
    }
}
