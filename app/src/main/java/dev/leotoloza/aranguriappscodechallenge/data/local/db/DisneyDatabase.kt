package dev.leotoloza.aranguriappscodechallenge.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.leotoloza.aranguriappscodechallenge.data.local.converter.CharacterTypeConverters
import dev.leotoloza.aranguriappscodechallenge.data.local.dao.CharacterDao
import dev.leotoloza.aranguriappscodechallenge.data.local.entity.CharacterEntity

/**
 * Base de datos local principal de la aplicación.
 *
 * Registra y gestiona las entidades persistidas localmente y expone los DAOs correspondientes.
 */
@Database(
    entities = [CharacterEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(CharacterTypeConverters::class)
abstract class DisneyDatabase : RoomDatabase() {

    /**
     * Proporciona acceso a las consultas de personajes favoritos definidos en el DAO.
     *
     * @return La interfaz del DAO [CharacterDao].
     */
    abstract fun characterDao(): CharacterDao
}
