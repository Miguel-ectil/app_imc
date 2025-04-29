package com.example.imc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.imc.database.ImcDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent

class EditImcActivity : AppCompatActivity() {

    private lateinit var pesoEditText: EditText
    private lateinit var alturaEditText: EditText
    private lateinit var imcTextView: TextView
    private lateinit var saveButton: Button

    private var imcId: Int = -1  // Variável para armazenar o ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_imc)

        pesoEditText = findViewById(R.id.pesoEditText)
        alturaEditText = findViewById(R.id.alturaEditText)
        imcTextView = findViewById(R.id.imcTextView)
        saveButton = findViewById(R.id.saveButton)

        val peso = intent.getStringExtra("peso")
        val altura = intent.getStringExtra("altura")
        val imc = intent.getStringExtra("imc")
        imcId = intent.getIntExtra("uid", -1)  // Obtendo o ID do IMC

        peso?.let {
            pesoEditText.setText(it.replace("[^0-9.]".toRegex(), ""))  // Remove "kg" ou qualquer outro texto
        } ?: pesoEditText.setText("0.0")  // Se o valor for nulo, define um valor padrão

        altura?.let {
            alturaEditText.setText(it.replace("[^0-9.]".toRegex(), ""))  // Remove "m" ou qualquer outro texto
        } ?: alturaEditText.setText("0.0")  // Se o valor for nulo, define um valor padrão

        imcTextView.text = imc

        saveButton.setOnClickListener {
            val novoPeso = pesoEditText.text.toString().toFloatOrNull() ?: 0f  // Verifica se é um número válido
            val novaAltura = alturaEditText.text.toString().toFloatOrNull() ?: 0f  // Verifica se é um número válido

            if (novoPeso == 0f || novaAltura == 0f) {
                Toast.makeText(this, "Por favor, insira valores válidos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoImc = calculateImc(novoPeso, novaAltura)

            if (imcId != -1) {  // Verifica se o ID do IMC foi recuperado corretamente
                saveImc(imcId, novoPeso, novaAltura, novoImc)
            } else {
                Toast.makeText(this, "ID do IMC não encontrado!", Toast.LENGTH_SHORT).show()
            }

            // Chama a função de atualização da lista na ImcListActivity
            val intent = Intent(this, ImcListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun calculateImc(peso: Float, altura: Float): String {
        val imc = peso / (altura * altura)
        return "IMC: %.2f".format(imc)
    }

    // Função para salvar a atualização no banco de dados
    private fun saveImc(id: Int, peso: Float, altura: Float, imc: String) {
        val db = ImcDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.imcDao().atualizar(id, peso, altura)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditImcActivity, "IMC Atualizado!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditImcActivity, "Erro ao atualizar IMC.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
