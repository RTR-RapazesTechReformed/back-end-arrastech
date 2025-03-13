package techForAll.techPoints.service

import techForAll.techPoints.domain.Endereco
import techForAll.techPoints.repository.EnderecoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.Mockito
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class EnderecoServiceTest {

    @Mock
    private lateinit var enderecoRepository: EnderecoRepository

    @InjectMocks
    private lateinit var enderecoService: EnderecoService

    @Test
    fun `cadastrarEndereco deve salvar e retornar um novo endereco`() {
        val novoEndereco = Endereco(
            id = 1,
            cep = "12345678",
            rua = "Rua das Laranjeiras",
            numero = "123",
            cidade = "São Paulo",
            estado = "SP",
            dataCriacao = LocalDateTime.now(),
            dataAtualizacao = null
        )

        `when`(enderecoRepository.save(novoEndereco)).thenReturn(novoEndereco)

        val resultado = enderecoService.cadastrarEndereco(novoEndereco)

        assertEquals(novoEndereco, resultado)
        verify(enderecoRepository, times(1)).save(novoEndereco)
    }

    @Test
    fun `listarEnderecos deve retornar lista de enderecos`() {
        val enderecos = listOf(
            Endereco(1L, "12345678", "Rua das Laranjeiras", "123", "São Paulo", "SP", LocalDateTime.now(), null),
            Endereco(2L, "87654321", "Rua das Oliveiras", "456", "Rio de Janeiro", "RJ", LocalDateTime.now(), null)
        )

        `when`(enderecoRepository.findAll()).thenReturn(enderecos)

        val resultado = enderecoService.listarEnderecos()

        assertEquals(2, resultado.size)
        assertEquals(enderecos, resultado)
        verify(enderecoRepository, times(1)).findAll()
    }

    @Test
    fun `listarEnderecos deve lançar exceção se a lista estiver vazia`() {
        `when`(enderecoRepository.findAll()).thenReturn(emptyList())

        val exception = assertThrows<NoSuchElementException> {
            enderecoService.listarEnderecos()
        }

        assertEquals("Nenhum endereço encontrado", exception.message)
        verify(enderecoRepository, times(1)).findAll()
    }

    @Test
    fun `buscarEnderecoPorId deve retornar endereco existente`() {
        val endereco = Endereco(1L, "12345678", "Rua das Laranjeiras", "123", "São Paulo", "SP", LocalDateTime.now(), null)
        `when`(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco))

        val resultado = enderecoService.buscarEnderecoPorId(1L)

        assertEquals(endereco, resultado)
        verify(enderecoRepository, times(1)).findById(1L)
    }

    @Test
    fun `buscarEnderecoPorId deve lançar exceção se endereco não for encontrado`() {
        `when`(enderecoRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            enderecoService.buscarEnderecoPorId(1L)
        }

        assertEquals("Endereço não encontrado com o ID: 1", exception.message)
        verify(enderecoRepository, times(1)).findById(1L)
    }

    @Test
    fun `atualizarEndereco deve atualizar e retornar endereco existente`() {
        val enderecoExistente = Endereco(
            id = 1L,
            cep = "12345678",
            rua = "Rua das Laranjeiras",
            numero = "123",
            cidade = "São Paulo",
            estado = "SP",
            dataCriacao = LocalDateTime.now(),
            dataAtualizacao = null
        )
        val enderecoAtualizado = mapOf(
            "cep" to "87654321",
            "rua" to "Rua das Oliveiras",
            "cidade" to "Rio de Janeiro",
            "estado" to "RJ"
        )

        `when`(enderecoRepository.findById(1L)).thenReturn(Optional.of(enderecoExistente))
        `when`(enderecoRepository.save(Mockito.any(Endereco::class.java))).thenAnswer { it.arguments[0] }

        val resultado = enderecoService.atualizarEndereco(1L, enderecoAtualizado)

        assertEquals("87654321", resultado.cep)
        assertEquals("Rua das Oliveiras", resultado.rua)
        assertEquals("Rio de Janeiro", resultado.cidade)
        assertEquals("RJ", resultado.estado)
        assertNotNull(resultado.dataAtualizacao)
        verify(enderecoRepository, times(1)).findById(1L)
        verify(enderecoRepository, times(1)).save(enderecoExistente)
    }

    @Test
    fun `atualizarEndereco deve lançar exceção se endereco não for encontrado`() {
        `when`(enderecoRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            enderecoService.atualizarEndereco(1L, emptyMap())
        }

        assertEquals("Endereço não encontrado com o ID: 1", exception.message)
        verify(enderecoRepository, times(1)).findById(1L)
    }

    @Test
    fun `deletarEndereco deve remover endereco existente`() {
        val endereco = Endereco(1L, "12345678", "Rua das Laranjeiras", "123", "São Paulo", "SP", LocalDateTime.now(), null)
        `when`(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco))

        enderecoService.deletarEndereco(1L)

        verify(enderecoRepository, times(1)).findById(1L)
        verify(enderecoRepository, times(1)).delete(endereco)
    }

    @Test
    fun `deletarEndereco deve lançar exceção se endereco não for encontrado`() {
        `when`(enderecoRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            enderecoService.deletarEndereco(1L)
        }

        assertEquals("Endereço não encontrado com o ID: 1", exception.message)
        verify(enderecoRepository, times(1)).findById(1L)
    }
}
