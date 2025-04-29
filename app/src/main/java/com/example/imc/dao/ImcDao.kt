package com.example.imc.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.imc.model.ImcEntity

@Dao
interface ImcDao {

    // Inserir um ou mais registros
    @Insert
    suspend fun inserir(imcEntities: List<ImcEntity>): List<Long>

    // Buscar todos os registros ordenados por ID
    @Query("SELECT * FROM tabela_imc ORDER BY uid DESC")
    suspend fun get(): List<ImcEntity>

    // Atualizar um registro específico
    @Query("""
        UPDATE tabela_imc 
        SET peso = :novoPeso, altura = :novaAltura 
        WHERE uid = :id
    """)
    suspend fun atualizar(
        id: Int,
        novoPeso: Float,
        novaAltura: Float
    )

    // Deletar um registro específico
    @Query("DELETE FROM tabela_imc WHERE uid = :id")
    suspend fun deletar(id: Int)
}

