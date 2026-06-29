package dev.leotoloza.aranguriappscodechallenge.data.repository

import dev.leotoloza.aranguriappscodechallenge.data.network.service.DisneyApiService
import dev.leotoloza.aranguriappscodechallenge.data.network.mapper.toDomain
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterFilter
import dev.leotoloza.aranguriappscodechallenge.domain.model.PaginatedResult
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: DisneyApiService
) : CharacterRepository {

    override suspend fun getCharacters(page: Int): Result<PaginatedResult<Character>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCharacters(page = page)
            if (response.isSuccessful) {
                val body = response.body()
                val characters = body?.data?.map { it.toDomain() } ?: emptyList()
                val hasNextPage = body?.info?.nextPage != null
                Result.success(PaginatedResult(items = characters, hasNextPage = hasNextPage))
            } else {
                Result.failure(Exception("Error al obtener personajes: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun filterCharacters(filter: CharacterFilter, page: Int): Result<List<Character>> = withContext(Dispatchers.IO) {
        try {
            val response = when (filter) {
                is CharacterFilter.ByName -> apiService.filterCharacters(name = filter.value, page = page)
                is CharacterFilter.ByFilm -> apiService.filterCharacters(films = filter.value, page = page)
                is CharacterFilter.ByShortFilm -> apiService.filterCharacters(shortFilms = filter.value, page = page)
                is CharacterFilter.ByTvShow -> apiService.filterCharacters(tvShows = filter.value, page = page)
                is CharacterFilter.ByVideoGame -> apiService.filterCharacters(videoGames = filter.value, page = page)
            }
            
            if (response.isSuccessful) {
                val body = response.body()
                val characters = body?.data?.map { it.toDomain() } ?: emptyList()
                Result.success(characters)
            } else {
                Result.failure(Exception("Error al filtrar personajes: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCharacterById(id: Int): Result<Character> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCharacterById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data.toDomain())
                } else {
                    Result.failure(Exception("Personaje no encontrado"))
                }
            } else {
                Result.failure(Exception("Error al obtener detalle: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
