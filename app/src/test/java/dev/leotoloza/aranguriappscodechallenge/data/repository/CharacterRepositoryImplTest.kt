package dev.leotoloza.aranguriappscodechallenge.data.repository

import dev.leotoloza.aranguriappscodechallenge.data.network.service.DisneyApiService
import dev.leotoloza.aranguriappscodechallenge.data.network.dto.CharacterDto
import dev.leotoloza.aranguriappscodechallenge.data.network.dto.CharacterResponseDto
import dev.leotoloza.aranguriappscodechallenge.data.network.dto.CharactersListResponseDto
import dev.leotoloza.aranguriappscodechallenge.data.network.dto.InfoDto
import dev.leotoloza.aranguriappscodechallenge.domain.model.CharacterFilter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Pruebas unitarias para la implementación concreta de [CharacterRepositoryImpl].
 */
class CharacterRepositoryImplTest {

    private val apiService: DisneyApiService = mockk()
    private val repository = CharacterRepositoryImpl(apiService)

    private companion object {
        const val TEST_ID = 1
        const val TEST_PAGE = 1
        const val TEST_PAGE_SIZE = 50
        const val TEST_COUNT = 1
        const val TEST_TOTAL_PAGES = 1
        const val TEST_NAME = "Mickey Mouse"
        const val TEST_IMAGE_URL = "http://mickey.jpg"
        const val TEST_URL = "http://mickey-api"
        const val ERROR_CODE_500 = 500
        const val ERROR_MESSAGE_NOT_FOUND = "Personaje no encontrado"
        const val ERROR_MESSAGE_API = "Error de API"
        const val SEARCH_QUERY = "Mickey"
        const val FILM_QUERY = "Fantasia"
    }

    private val mockInfoDto = InfoDto(
        count = TEST_COUNT,
        totalPages = TEST_TOTAL_PAGES,
        previousPage = null,
        nextPage = null
    )

    private val mockCharacterDto = CharacterDto(
        id = TEST_ID,
        name = TEST_NAME,
        imageUrl = TEST_IMAGE_URL,
        url = TEST_URL,
        films = emptyList(),
        shortFilms = emptyList(),
        tvShows = emptyList(),
        videoGames = emptyList()
    )

    /**
     * Verifica que [CharacterRepositoryImpl.getCharacters] retorne una lista exitosa de personajes
     * cuando la API responda con código 200 y datos correctos.
     */
    @Test
    fun getCharacters_success_returns_mapped_data() = runTest {
        // Given (Dado que la API retorna una respuesta exitosa con personajes)
        val responseDto = CharactersListResponseDto(
            info = mockInfoDto,
            data = listOf(mockCharacterDto)
        )
        coEvery {
            apiService.getCharacters(page = TEST_PAGE, pageSize = TEST_PAGE_SIZE)
        } returns Response.success(responseDto)

        // When (Cuando se llama a getCharacters)
        val result = repository.getCharacters(TEST_PAGE)

        // Then (Entonces el resultado debe ser exitoso y contener los datos mapeados)
        assertTrue(result.isSuccess)
        val characters = result.getOrThrow()
        assertEquals(TEST_COUNT, characters.size)
        assertEquals(TEST_ID, characters[0].id)
        assertEquals(TEST_NAME, characters[0].name)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.getCharacters] capture errores HTTP fallidos
     * y los retorne dentro de un resultado fallido.
     */
    @Test
    fun getCharacters_apiError_returns_failure() = runTest {
        // Given (Dado que la API retorna un error 500)
        coEvery {
            apiService.getCharacters(page = TEST_PAGE, pageSize = TEST_PAGE_SIZE)
        } returns Response.error(ERROR_CODE_500, "".toResponseBody(null))

        // When (Cuando se solicita el listado)
        val result = repository.getCharacters(TEST_PAGE)

        // Then (Entonces se captura el error HTTP)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception?.message?.contains(ERROR_CODE_500.toString()) == true)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.getCharacters] capture excepciones de red
     * y las devuelva apropiadamente como fallos del Result.
     */
    @Test
    fun getCharacters_networkException_returns_failure() = runTest {
        // Given (Dado un fallo de red o IOException)
        coEvery {
            apiService.getCharacters(page = TEST_PAGE, pageSize = TEST_PAGE_SIZE)
        } throws IOException(ERROR_MESSAGE_API)

        // When (Cuando se solicita el listado)
        val result = repository.getCharacters(TEST_PAGE)

        // Then (Entonces se captura la excepción como fallo)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertEquals(ERROR_MESSAGE_API, exception?.message)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.filterCharacters] con filtro por Nombre llame
     * correctamente a la API y retorne los resultados correspondientes.
     */
    @Test
    fun filterCharacters_byName_success_returns_mapped_data() = runTest {
        // Given (Dado un filtro de búsqueda por nombre)
        val filter = CharacterFilter.ByName(SEARCH_QUERY)
        val responseDto = CharactersListResponseDto(
            info = mockInfoDto,
            data = listOf(mockCharacterDto)
        )
        coEvery {
            apiService.filterCharacters(name = SEARCH_QUERY, page = TEST_PAGE)
        } returns Response.success(responseDto)

        // When (Cuando se ejecuta el filtrado)
        val result = repository.filterCharacters(filter, TEST_PAGE)

        // Then (Entonces se obtienen los personajes mapeados correctamente)
        assertTrue(result.isSuccess)
        val characters = result.getOrThrow()
        assertEquals(TEST_ID, characters[0].id)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.filterCharacters] con filtro por Película llame
     * correctamente a la API y retorne los resultados mapeados.
     */
    @Test
    fun filterCharacters_byFilm_success_returns_mapped_data() = runTest {
        // Given (Dado un filtro de búsqueda por película)
        val filter = CharacterFilter.ByFilm(FILM_QUERY)
        val responseDto = CharactersListResponseDto(
            info = mockInfoDto,
            data = listOf(mockCharacterDto)
        )
        coEvery {
            apiService.filterCharacters(films = FILM_QUERY, page = TEST_PAGE)
        } returns Response.success(responseDto)

        // When (Cuando se ejecuta el filtrado)
        val result = repository.filterCharacters(filter, TEST_PAGE)

        // Then (Entonces retorna los personajes correspondientes)
        assertTrue(result.isSuccess)
        val characters = result.getOrThrow()
        assertEquals(TEST_ID, characters[0].id)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.filterCharacters] capture errores de la API
     * y retorne un Result fallido.
     */
    @Test
    fun filterCharacters_apiError_returns_failure() = runTest {
        // Given (Dado un filtro y que la API falla con código 500)
        val filter = CharacterFilter.ByName(SEARCH_QUERY)
        coEvery {
            apiService.filterCharacters(name = SEARCH_QUERY, page = TEST_PAGE)
        } returns Response.error(ERROR_CODE_500, "".toResponseBody(null))

        // When (Cuando se llama al filtro)
        val result = repository.filterCharacters(filter, TEST_PAGE)

        // Then (Entonces el resultado es fallido)
        assertTrue(result.isFailure)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.getCharacterById] retorne el personaje correspondiente
     * de forma exitosa.
     */
    @Test
    fun getCharacterById_success_returns_character() = runTest {
        // Given (Dado que la API retorna un personaje específico por ID)
        val responseDto = CharacterResponseDto(
            info = mockInfoDto,
            data = mockCharacterDto
        )
        coEvery {
            apiService.getCharacterById(TEST_ID)
        } returns Response.success(responseDto)

        // When (Cuando se busca por ID)
        val result = repository.getCharacterById(TEST_ID)

        // Then (Entonces el resultado contiene el personaje correspondiente)
        assertTrue(result.isSuccess)
        val character = result.getOrThrow()
        assertEquals(TEST_ID, character.id)
        assertEquals(TEST_NAME, character.name)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.getCharacterById] retorne un error cuando
     * el cuerpo de la respuesta sea nulo (no encontrado).
     */
    @Test
    fun getCharacterById_nullBody_returns_failure() = runTest {
        // Given (Dado que la API responde con éxito pero con un cuerpo nulo)
        coEvery {
            apiService.getCharacterById(TEST_ID)
        } returns Response.success(null)

        // When (Cuando se busca por ID)
        val result = repository.getCharacterById(TEST_ID)

        // Then (Entonces se retorna una excepción indicando que no se encontró)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertEquals(ERROR_MESSAGE_NOT_FOUND, exception?.message)
    }

    /**
     * Verifica que [CharacterRepositoryImpl.getCharacterById] retorne un fallo cuando
     * ocurre una excepción de red al buscar el ID.
     */
    @Test
    fun getCharacterById_networkException_returns_failure() = runTest {
        // Given (Dado una excepción de red)
        coEvery {
            apiService.getCharacterById(TEST_ID)
        } throws IOException(ERROR_MESSAGE_API)

        // When (Cuando se consulta por ID)
        val result = repository.getCharacterById(TEST_ID)

        // Then (Entonces retorna fallo con la excepción correspondiente)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertEquals(ERROR_MESSAGE_API, exception?.message)
    }
}
