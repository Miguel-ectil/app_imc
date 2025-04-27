package com.example.imc

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imc.database.ImcDatabase
import com.example.imc.model.ImcEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Imc(
    val uid: Int,   // O campo uid precisa estar presente aqui
    val peso: String,
    val altura: String,
    val imc: String
)


class ImcListActivity : AppCompatActivity() {

    private lateinit var recyclerViewImcs: RecyclerView
    private lateinit var imcAdapter: ImcAdapter
    private val _listaImc = MutableLiveData<List<Imc>>() // LiveData para armazenar a lista de IMCs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imc_list)

        recyclerViewImcs = findViewById(R.id.recyclerViewImcs)
        recyclerViewImcs.layoutManager = LinearLayoutManager(this)

        _listaImc.observe(this, Observer { listaImc ->
            val listaMutable = listaImc.toMutableList()  // Converte a lista imutável para mutável
            imcAdapter = ImcAdapter(listaMutable) { imc ->
                deleteImc(imc) // Chama a função de exclusão
            }
            recyclerViewImcs.adapter = imcAdapter
            imcAdapter.notifyDataSetChanged()  // Atualiza o adapter
        })

        loadImcsFromDatabase()
    }

    private fun loadImcsFromDatabase() {
        val db = ImcDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imcEntityList = db.imcDao().get()

                val imcList = imcEntityList.map { entity ->
                    Imc(
                        uid = entity.uid,  // Garantir que o UID é mapeado corretamente
                        peso = "Peso: ${entity.peso}kg",
                        altura = "Altura: ${entity.altura}m",
                        imc = "IMC: ${entity.resultadoImc}"
                    )
                }

                withContext(Dispatchers.Main) {
                    _listaImc.value = imcList
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ImcListActivity, "Erro ao carregar os IMCs", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun deleteImc(imc: Imc) {
        Log.d("ImcListActivity", "Tentando deletar IMC: ${imc.peso}, ${imc.altura}, UID: ${imc.uid}")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = ImcDatabase.getDatabase(this@ImcListActivity)
                db.imcDao().deletar(imc.uid)  // Passando o UID corretamente

                // Recarregar a lista após a exclusão
                withContext(Dispatchers.Main) {
                    val updatedList = _listaImc.value?.toMutableList()?.apply {
                        remove(imc) // Remove o item da lista local
                    }
                    _listaImc.value = updatedList  // Atualiza o LiveData com a nova lista
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ImcListActivity, "Erro ao excluir IMC: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ImcListActivity", "Erro ao excluir IMC", e)
                }
            }
        }
    }
}
