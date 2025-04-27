package com.example.imc

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.imc.database.ImcDatabase
import com.example.imc.model.ImcEntity
import kotlinx.coroutines.launch
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private lateinit var database: ImcDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instanciando o banco
        database = ImcDatabase.getDatabase(this)

        val editTextWeight = findViewById<EditText>(R.id.editTextWeight)
        val editTextHeight = findViewById<EditText>(R.id.editTextHeight)
        val buttonCalculate = findViewById<Button>(R.id.buttonCalculate)
        val textViewResult = findViewById<TextView>(R.id.textViewResult)

        buttonCalculate.setOnClickListener {
            val weight = editTextWeight.text.toString().toFloatOrNull()
            val height = editTextHeight.text.toString().toFloatOrNull()

            if (weight != null && height != null && weight > 0 && height > 0) {
                // Calcular o IMC
                val imc = weight / (height * height)
                val classification = getImcClassification(imc)

                textViewResult.text = "IMC = %.2f\n%s".format(imc, classification)

                val imcEntity = ImcEntity(peso = weight, altura = height, resultadoImc = imc)

                // Inserir no banco de dados com retorno do ID
                lifecycleScope.launch {
                    val idList = database.imcDao().inserir(listOf(imcEntity))
                    val insertedId = idList.firstOrNull() // Pegando o primeiro ID inserido

                    if (insertedId != null) {
                        // Mostrar a mensagem de sucesso com o ID inserido
                        Toast.makeText(this@MainActivity, "IMC salvo com sucesso! ID: $insertedId", Toast.LENGTH_LONG).show()

                        // Redirecionar para a ImcListActivity
                        val intent = Intent(this@MainActivity, ImcListActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Se não houver ID (caso algo tenha dado errado)
                        Toast.makeText(this@MainActivity, "Erro ao salvar IMC", Toast.LENGTH_SHORT).show()
                    }
                }

                hideKeyboard()
            } else {
                textViewResult.text = "Por favor, insira valores válidos para peso e altura!"
            }
        }

    }

    private fun getImcClassification(imc: Float): String {
        return when {
            imc < 18.5 -> "Abaixo do peso"
            imc in 18.5..24.9 -> "Você está no Peso ideal"
            imc in 25.0..29.9 -> "Você está Sobrepeso"
            imc in 30.0..34.9 -> "Você está com Obesidade Grau I"
            imc in 35.0..39.9 -> "Você está com Obesidade Grau II"
            else -> "Você está com Obesidade Grau III (Mórbida)"
        }
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
