package techForAll.techPoints.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import techForAll.techPoints.domain.Empresa
import techForAll.techPoints.domain.Endereco
import techForAll.techPoints.dtos.EmpresaInput
import techForAll.techPoints.repository.DadosEmpresaRepository
import techForAll.techPoints.repository.EnderecoRepository
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class DadosEmpresaServiceTest {

    @InjectMocks
    private lateinit var dadoEmpresaService: DadosEmpresaService

    @Mock
    private lateinit var empresaRepository: DadosEmpresaRepository

    @Mock
    private lateinit var enderecoRepository: EnderecoRepository


    @Test
    fun `cadastrarEmpresa deve salvar e retornar empresa criada`() {
        val enderecoId = 1L
        val enderecoMock = mock<Endereco>()
        val novaEmpresa = EmpresaInput(
            nomeEmpresa = "TechForAll",
            cnpj = "12345678901234",
            setorIndustria = "Tecnologia",
            telefoneContato = "999999999",
            emailCorporativo = "contato@techforall.com",
            enderecoId = enderecoId
        )

        val empresaEsperada = Empresa(
            id = 1L,
            nomeEmpresa = novaEmpresa.nomeEmpresa,
            cnpj = novaEmpresa.cnpj,
            setorIndustria = novaEmpresa.setorIndustria,
            telefoneContato = novaEmpresa.telefoneContato,
            emailCorporativo = novaEmpresa.emailCorporativo,
            endereco = enderecoMock
        )

        `when`(enderecoRepository.findById(enderecoId)).thenReturn(Optional.of(enderecoMock))
        `when`(empresaRepository.save(any(Empresa::class.java))).thenReturn(empresaEsperada)

        val resultado = dadoEmpresaService.cadastrarEmpresa(novaEmpresa)

        assertNotNull(resultado)
        assertEquals(empresaEsperada.nomeEmpresa, resultado.nomeEmpresa)
        assertEquals(empresaEsperada.cnpj, resultado.cnpj)
        verify(enderecoRepository, times(1)).findById(enderecoId)
        verify(empresaRepository, times(1)).save(any(Empresa::class.java))
    }

    @Test
    fun `listarEmpresas deve retornar uma lista de DTOs quando houver empresas`() {
        val empresasMock = listOf(
            Empresa(
                id = 1L,
                nomeEmpresa = "TechForAll",
                cnpj = "12345678901234",
                setorIndustria = "Tecnologia",
                telefoneContato = "999999999",
                emailCorporativo = "contato@techforall.com",
                endereco = mock(),
                dataCriacao = LocalDateTime.now(),
                recrutadores = emptyList()
            )
        )

        `when`(empresaRepository.findAll()).thenReturn(empresasMock)

        val resultado = dadoEmpresaService.listarEmpresas()

        assertNotNull(resultado)
        assertEquals(1, resultado.size)
        assertEquals("TechForAll", resultado[0].nomeEmpresa)
        verify(empresaRepository, times(1)).findAll()
    }

    @Test
    fun `listarEmpresas deve lançar NoSuchElementException quando não houver empresas registradas`() {

        `when`(empresaRepository.findAll()).thenReturn(emptyList())

        val exception = assertThrows<NoSuchElementException> {
            dadoEmpresaService.listarEmpresas()
        }

        assertEquals("Nenhuma empresa encontrada", exception.message)

        verify(empresaRepository, times(1)).findAll()
    }


    @Test
    fun `buscarEmpresaPorId deve retornar DTO quando encontrar empresa`() {
        val empresaMock = Empresa(
            id = 1,
            nomeEmpresa = "TechForAll",
            cnpj = "12345678901234",
            setorIndustria = "Tecnologia",
            telefoneContato = "999999999",
            emailCorporativo = "contato@techforall.com",
            endereco = mock(),
            dataCriacao = LocalDateTime.now(),
            recrutadores = emptyList()
        )

        `when`(empresaRepository.findById(1)).thenReturn(Optional.of(empresaMock))

        val resultado = dadoEmpresaService.buscarEmpresaPorId(1)

        assertNotNull(resultado)
        assertEquals("TechForAll", resultado.nomeEmpresa)
        verify(empresaRepository, times(1)).findById(1)
    }

    @Test
    fun `atualizarEmpresa deve lançar NoSuchElementException quando a empresa não for encontrada`() {

        `when`(empresaRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            dadoEmpresaService.atualizarEmpresa(1L, mapOf("nomeEmpresa" to "Nova Empresa"))
        }

        assertEquals("Empresa não encontrada com o ID: 1", exception.message)

        verify(empresaRepository, times(1)).findById(1L)
    }

    @Test
    fun `atualizarEmpresa deve atualizar corretamente os dados da empresa`() {
       
        val empresaExistente = Empresa(
            id = 1L,
            nomeEmpresa = "Empresa Antiga",
            cnpj = "12345678900001",
            setorIndustria = "Tecnologia",
            telefoneContato = "987654321",
            emailCorporativo = "contato@empresa.com",
            endereco = Endereco(
                id = 1L,
                rua = "Rua 1",
                cidade = "Cidade",
                estado = "Estado",
                cep = "09340-214",
                numero = "356",
                dataCriacao = LocalDateTime.now(),
                dataAtualizacao = null
            ),
            dataCriacao = LocalDateTime.now(),
            dataAtualizacao = null,
            recrutadores = emptyList()
        )

        `when`(empresaRepository.findById(1L)).thenReturn(Optional.of(empresaExistente))

        `when`(empresaRepository.save(any(Empresa::class.java))).thenReturn(empresaExistente)

        val empresaAtualizada = mapOf("nomeEmpresa" to "Nova Empresa", "setorIndustria" to "Saúde")

        val resultado = dadoEmpresaService.atualizarEmpresa(1L, empresaAtualizada)

        assertEquals("Nova Empresa", resultado.nomeEmpresa)
        assertEquals("Saúde", resultado.setorIndustria)
        assertEquals("12345678900001", resultado.cnpj)
        assertEquals("987654321", resultado.telefoneContato)
        assertEquals("contato@empresa.com", resultado.emailCorporativo)

        verify(empresaRepository, times(1)).save(any(Empresa::class.java))
    }
    
    @Test
    fun `deletarEmpresa deve remover empresa quando existir`() {
        val empresaMock = Empresa(
            id = 1L,
            nomeEmpresa = "TechForAll",
            cnpj = "12345678901234",
            setorIndustria = "Tecnologia",
            telefoneContato = "999999999",
            emailCorporativo = "contato@techforall.com",
            endereco = mock()
        )

        `when`(empresaRepository.findById(1L)).thenReturn(Optional.of(empresaMock))

        dadoEmpresaService.deletarEmpresa(1L)

        verify(empresaRepository, times(1)).findById(1L)
        verify(empresaRepository, times(1)).delete(empresaMock)
    }

    @Test
    fun `buscarEmpresaPorCnpj deve retornar DTO quando encontrar empresa pelo CNPJ`() {
        val empresaMock = Empresa(
            id = 1L,
            nomeEmpresa = "TechForAll",
            cnpj = "12345678901234",
            setorIndustria = "Tecnologia",
            telefoneContato = "999999999",
            emailCorporativo = "contato@techforall.com",
            endereco = Endereco(
                id = 1L,
                rua = "Rua 1",
                cidade = "Cidade X",
                estado = "Estado Y",
                cep = "12345-678",
                numero = "123",
                dataCriacao = LocalDateTime.now(),
                dataAtualizacao = null
            )
        )

        `when`(empresaRepository.findByCnpj("12345678901234")).thenReturn(Optional.of(empresaMock))

        val resultado = dadoEmpresaService.buscarEmpresaPorCnpj("12345678901234")

        assertNotNull(resultado)
        assertEquals(empresaMock.id, resultado.id)
        assertEquals(empresaMock.nomeEmpresa, resultado.nomeEmpresa)
        assertEquals(empresaMock.cnpj, resultado.cnpj)
        assertEquals(empresaMock.setorIndustria, resultado.setorIndustria)
        assertEquals(empresaMock.telefoneContato, resultado.telefoneContato)
        assertEquals(empresaMock.emailCorporativo, resultado.emailCorporativo)
        assertEquals(empresaMock.endereco, resultado.endereco)

        verify(empresaRepository, times(1)).findByCnpj("12345678901234")
    }

    @Test
    fun `buscarEmpresaPorCnpj deve lançar NoSuchElementException quando o CNPJ não for encontrado`() {
        val cnpj = "98765432109876"

        `when`(empresaRepository.findByCnpj(cnpj)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            dadoEmpresaService.buscarEmpresaPorCnpj(cnpj)
        }

        assertEquals("Empresa não encontrada com o CNPJ: $cnpj", exception.message)

        verify(empresaRepository, times(1)).findByCnpj(cnpj)
    }

}
