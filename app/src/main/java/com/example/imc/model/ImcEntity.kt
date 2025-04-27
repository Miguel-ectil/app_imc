package com.example.imc.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_imc")
data class ImcEntity(

    @ColumnInfo(name = "peso") val peso: Float,
    @ColumnInfo(name = "altura") val altura: Float,
    @ColumnInfo(name = "resultadoImc") val resultadoImc: Float,

    @ColumnInfo(name = "data_hora") val dataHora: Long = System.currentTimeMillis()
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}
