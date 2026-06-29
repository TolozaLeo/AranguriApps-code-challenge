package dev.leotoloza.aranguriappscodechallenge.data.repository

import dev.leotoloza.aranguriappscodechallenge.data.network.service.DisneyApiService
import dev.leotoloza.aranguriappscodechallenge.data.network.mapper.toDomain
import dev.leotoloza.aranguriappscodechallenge.domain.model.Character
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterFilter
import dev.leotoloza.aranguriappscodechallenge.domain.model.PaginatedResult
import dev.leotoloza.aranguriappscodechallenge.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val apiService: DisneyApiService
) : CharacterRepository {

    private val _favoriteCharacters = MutableStateFlow<Map<Int, Character>>(
        mapOf(
            4703 to Character(
                id = 4703,
                name = "Mickey Mouse",
                imageUrl = "https://picsum.photos/seed/mickey/800/400",
                url = "https://api.disneyapi.dev/characters/4703",
                films = listOf("Fantasia (1940)", "Mickey, Donald, Goofy: The Three Musketeers", "Saving Mr. Banks"),
                shortFilms = listOf("Steamboat Willie (1928)", "The Band Concert"),
                tvShows = listOf("Mickey Mouse Clubhouse", "House of Mouse", "The Wonderful World of Mickey Mouse"),
                videoGames = listOf("Kingdom Hearts Series", "Epic Mickey")
            ),
            1947 to Character(
                id = 1947,
                name = "Donald Duck",
                imageUrl = "https://picsum.photos/seed/donald/800/400",
                url = "https://api.disneyapi.dev/characters/1947",
                films = listOf("The Three Caballeros", "Fantasia 2000"),
                shortFilms = listOf("Don Donald", "Der Fuehrer's Face"),
                tvShows = listOf("DuckTales", "Quack Pack", "Mickey Mouse Works"),
                videoGames = listOf("Kingdom Hearts Series", "Donald Duck: Goin' Quackers")
            ),
            2743 to Character(
                id = 2743,
                name = "Goofy",
                imageUrl = "https://picsum.photos/seed/goofy/800/400",
                url = "https://api.disneyapi.dev/characters/2743",
                films = listOf("A Goofy Movie", "An Extremely Goofy Movie", "Who Framed Roger Rabbit"),
                shortFilms = listOf("How to Play Football", "Goofy and Wilbur"),
                tvShows = listOf("Goof Troop", "House of Mouse", "Mickey Mouse Clubhouse"),
                videoGames = listOf("Kingdom Hearts Series", "Goofy's Hysterical History Tour")
            )
        )
    )

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

    override fun getFavoriteCharacters(): Flow<List<Character>> {
        return _favoriteCharacters.map { it.values.toList() }
    }

    override fun getFavoriteIds(): Flow<Set<Int>> {
        return _favoriteCharacters.map { it.keys }
    }

    override suspend fun toggleFavorite(character: Character) {
        val current = _favoriteCharacters.value.toMutableMap()
        if (current.containsKey(character.id)) {
            current.remove(character.id)
        } else {
            current[character.id] = character
        }
        _favoriteCharacters.value = current
    }
}
