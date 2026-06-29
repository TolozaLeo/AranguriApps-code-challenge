package dev.leotoloza.aranguriappscodechallenge.data.local.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

/**
 * Convertidor de tipos para Room que permite persistir colecciones de cadenas en formato JSON.
 *
 * Utiliza una instancia provista de [Moshi] para garantizar consistencia con los adaptadores JSON globales.
 *
 * @property moshi Instancia de [Moshi] inyectada mediante Hilt.
 */
@ProvidedTypeConverter
class CharacterTypeConverters @Inject constructor(
    private val moshi: Moshi
) {
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter: JsonAdapter<List<String>> = moshi.adapter(listType)

    /**
     * Decodifica una cadena JSON para reconstruir la lista de cadenas.
     *
     * @param value Cadena en formato JSON.
     * @return Lista de cadenas resultante, o una lista vacía en caso de error.
     */
    @TypeConverter
    fun fromString(value: String): List<String> {
        return try {
            adapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Serializa una lista de cadenas a formato JSON para su almacenamiento.
     *
     * @param list Lista de cadenas a codificar.
     * @return Cadena serializada en formato JSON.
     */
    @TypeConverter
    fun fromList(list: List<String>): String {
        return try {
            adapter.toJson(list)
        } catch (e: Exception) {
            "[]"
        }
    }
}
