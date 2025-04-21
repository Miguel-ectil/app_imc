package com.example.imc

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
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
                val imc = weight / (height * height)
                val classification = getImcClassification(imc)

                textViewResult.text = "IMC = %.2f\n%s".format(imc, classification)

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
