package com.example.imc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    val uid: Int,
    val peso: String,
    val altura: String,
    val imc: String
)

class ImcListActivity : AppCompatActivity() {

    private lateinit var recyclerViewImcs: RecyclerView
    private lateinit var imcAdapter: ImcAdapter
    private val _listaImc = MutableLiveData<List<Imc>>() // LiveData da lista de IMCs

    // Cria o launcher para receber o resultado da edição
    private val editImcLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Sempre recarrega a lista após a tela de edição voltar
        loadImcsFromDatabase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imc_list)

        recyclerViewImcs = findViewById(R.id.recyclerViewImcs)
        recyclerViewImcs.layoutManager = LinearLayoutManager(this)

        _listaImc.observe(this, Observer { listaImc ->
            val listaMutable = listaImc.toMutableList()
            imcAdapter = ImcAdapter(listaMutable,
                onDeleteClicked = { imc ->
                    deleteImc(imc)
                },
                onEditClicked = { imc ->
                    editImc(imc)
                }
            )
            recyclerViewImcs.adapter = imcAdapter
            imcAdapter.notifyDataSetChanged()
        })

        loadImcsFromDatabase()
    }

    private fun editImc(imc: Imc) {
        val intent = Intent(this, EditImcActivity::class.java)
        intent.putExtra("uid", imc.uid)
        intent.putExtra("peso", imc.peso)
        intent.putExtra("altura", imc.altura)
        intent.putExtra("imc", imc.imc)
        editImcLauncher.launch(intent) // <-- Usa o launcher para esperar o resultado
    }

    private fun loadImcsFromDatabase() {
        val db = ImcDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imcEntityList = db.imcDao().get()

                val imcList = imcEntityList.map { entity ->
                    Imc(
                        uid = entity.uid,
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
                db.imcDao().deletar(imc.uid)

                withContext(Dispatchers.Main) {
                    val updatedList = _listaImc.value?.toMutableList()?.apply {
                        remove(imc)
                    }
                    _listaImc.value = updatedList
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
