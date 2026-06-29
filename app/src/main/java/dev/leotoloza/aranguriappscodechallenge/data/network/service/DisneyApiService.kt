package dev.leotoloza.aranguriappscodechallenge.data.network.service

import dev.leotoloza.aranguriappscodechallenge.data.network.dto.CharacterResponseDto
import dev.leotoloza.aranguriappscodechallenge.data.network.dto.CharactersListResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface de Retrofit para interactuar con la API de Disney.
 */
interface DisneyApiService {

    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<CharactersListResponseDto>

    @GET("character")
    suspend fun filterCharacters(
        @Query("name") name: String? = null,
        @Query("films") films: String? = null,
        @Query("shortFilms") shortFilms: String? = null,
        @Query("tvShows") tvShows: String? = null,
        @Query("videoGames") videoGames: String? = null,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): Response<CharactersListResponseDto>

    @GET("character/{id}")
    suspend fun getCharacterById(
        @Path("id") id: Int
    ): Response<CharacterResponseDto>
}