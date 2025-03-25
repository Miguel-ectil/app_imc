package com.example.imc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

                // Exibir o resultado com 2 casas decimais
                textViewResult.text = "Resultado: IMC = %.2f".format(imc)
            } else {
                // Caso o usuário insira valores inválidos
                textViewResult.text = "Por favor, insira valores válidos para \n peso e altura!"
            }
        }
    }
}
