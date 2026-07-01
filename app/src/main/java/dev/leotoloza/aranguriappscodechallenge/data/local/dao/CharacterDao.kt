package dev.leotoloza.aranguriappscodechallenge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.leotoloza.aranguriappscodechallenge.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz DAO (Data Access Object) de Room para gestionar los personajes favoritos.
 *
 * Provee métodos reactivos y asíncronos para interactuar con la tabla `favorite_characters`.
 */
@Dao
interface CharacterDao {

    /**
     * Obtiene una lista observable de todos los personajes favoritos, ordenados alfabéticamente por su nombre.
     *
     * @return Un [Flow] que emite colecciones actualizadas de [CharacterEntity].
     */
    @Query("SELECT * FROM favorite_characters ORDER BY name ASC")
    fun observeFavoriteCharacters(): Flow<List<CharacterEntity>>

    /**
     * Obtiene una lista observable de los IDs únicos de todos los personajes favoritos.
     *
     * @return Un [Flow] que emite listas con los IDs numéricos de favoritos.
     */
    @Query("SELECT id FROM favorite_characters")
    fun observeFavoriteIds(): Flow<List<Int>>

    /**
     * Consulta un personaje favorito específico utilizando su ID único.
     *
     * @param id Identificador único del personaje.
     * @return La instancia de [CharacterEntity] si está en favoritos, o null si no se encuentra.
     */
    @Query("SELECT * FROM favorite_characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    /**
     * Inserta un nuevo personaje en los registros locales de favoritos.
     * En caso de conflicto de claves (mismo ID), reemplaza los datos preexistentes.
     *
     * @param character Entidad del personaje [CharacterEntity] a persistir.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(character: CharacterEntity)

    /**
     * Elimina un personaje de los registros locales de favoritos usando su identificador único.
     *
     * @param id Identificador único del personaje a eliminar.
     */
    @Query("DELETE FROM favorite_characters WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)
}
