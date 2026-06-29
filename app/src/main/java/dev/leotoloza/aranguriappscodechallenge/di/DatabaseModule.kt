package dev.leotoloza.aranguriappscodechallenge.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.leotoloza.aranguriappscodechallenge.data.local.converter.CharacterTypeConverters
import dev.leotoloza.aranguriappscodechallenge.data.local.dao.CharacterDao
import dev.leotoloza.aranguriappscodechallenge.data.local.db.DisneyDatabase
import javax.inject.Singleton

/**
 * Módulo de inyección de dependencias con Hilt para la base de datos local Room.
 *
 * Configura e inyecta la base de datos, convertidores y accesos DAO correspondientes.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Proporciona los convertidores de tipo personalizados necesarios para persistir listas de strings.
     *
     * @param moshi Instancia de [Moshi] inyectada globalmente.
     * @return Instancia única de [CharacterTypeConverters].
     */
    @Provides
    @Singleton
    fun provideCharacterTypeConverters(moshi: Moshi): CharacterTypeConverters {
        return CharacterTypeConverters(moshi)
    }

    /**
     * Proporciona la instancia única de la base de datos local [DisneyDatabase].
     * Registra los convertidores provistos externamente en la construcción.
     *
     * @param context Contexto global de la aplicación.
     * @param typeConverters Convertidor personalizado para listas.
     * @return Instancia única de [DisneyDatabase].
     */
    @Provides
    @Singleton
    fun provideDisneyDatabase(
        @ApplicationContext context: Context,
        typeConverters: CharacterTypeConverters
    ): DisneyDatabase {
        return Room.databaseBuilder(
            context,
            DisneyDatabase::class.java,
            "disney_characters.db"
        )
        .addTypeConverter(typeConverters)
        .build()
    }

    /**
     * Proporciona el DAO de personajes favoritos para interactuar con la base de datos.
     *
     * @param database Instancia única de [DisneyDatabase].
     * @return Instancia única de [CharacterDao].
     */
    @Provides
    @Singleton
    fun provideCharacterDao(database: DisneyDatabase): CharacterDao {
        return database.characterDao()
    }
}
