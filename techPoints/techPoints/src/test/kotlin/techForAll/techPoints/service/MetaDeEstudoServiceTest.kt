package techForAll.techPoints.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import techForAll.techPoints.domain.*
import techForAll.techPoints.dtos.MetaDiariaDto
import techForAll.techPoints.dtos.SessoesDto
import techForAll.techPoints.repository.AlunoRepository
import techForAll.techPoints.repository.MetaEstudoSemanaRepository
import techForAll.techPoints.repository.TempoEstudoRepository
import techForAll.techPoints.repository.TempoSessaoRepository
import java.time.LocalDate
import java.util.*
import kotlin.NoSuchElementException

@ExtendWith(MockitoExtension::class)
class MetaDeEstudoServiceTest {

    @Mock
    private lateinit var metaSemanalRepository: MetaEstudoSemanaRepository

    @Mock
    private lateinit var alunoRepository: AlunoRepository

    @Mock
    private lateinit var sessaoRepository: TempoSessaoRepository

    @Mock
    private lateinit var metaDiariaRepository: TempoEstudoRepository

    @InjectMocks
    private lateinit var metaDeEstudoService: MetaDeEstudoService

    private fun criarAluno(): Aluno {
        return Aluno(
            escolaridade = "Ensino Médio",
            sexo = "Masculino",
            etnia = "Branco",
            endereco = Endereco(
                cep = "12345678",
                rua = "Rua das Laranjeiras",
                numero = "123",
                cidade = "São Paulo",
                estado = "São Paulo"
            ),
            dtNasc = LocalDate.of(2000, 1, 1),
            nomeUsuario = "Juninho",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Junior",
            sobrenome = "Games",
            email = "juninho@email.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )
    }

    private fun criarMetaEstudoSemana(aluno: Aluno): MetaEstudoSemana {
        return MetaEstudoSemana(
            id = 1L,
            aluno = aluno,
            diasAtivos = emptyList(),
            horasTotal = 0.0,
            tempoSessao = emptyList()
        )
    }

    private fun criarTempoEstudo(meta: MetaEstudoSemana): TempoEstudo {
        return TempoEstudo(
            id = 1L,
            nomeDia = "segunda",
            data = "2024-01-01",
            qtdTempoEstudo = "2",
            ativado = true,
            metaEstudoSemana = meta
        )
    }

    private fun mapMetaDiariaDto(tempoEstudo: TempoEstudo): MetaDiariaDto {
        return MetaDiariaDto(
            id = tempoEstudo.id,
            nomeDia = tempoEstudo.nomeDia,
            data = tempoEstudo.data,
            qtdTempoEstudo = tempoEstudo.qtdTempoEstudo,
            ativado = tempoEstudo.ativado,
            metaEstudoSemana = tempoEstudo.metaEstudoSemana.id!!
        )
    }

    private fun mapToSessoesDto(tempoSessao: TempoSessao): SessoesDto {
        return SessoesDto(
            id = tempoSessao.id,
            diaSessao = tempoSessao.diaSessao,
            qtdTempoSessao = tempoSessao.qtdTempoSessao,
            metaEstudoSemana = tempoSessao.metaEstudoSemana.id!!
        )
    }

    @Test
    fun `cadastrarMetaDiaria deve salvar nova MetaDiaria quando não existir`() {
        val metaEstudoSemanaId = 1L
        val nomeDia = "segunda"
        val data = "2023-10-01"
        val qtdTempoEstudo = "2h"
        val ativado = true

        val aluno = criarAluno()
        val metaEstudoSemana = criarMetaEstudoSemana(aluno)

        `when`(metaSemanalRepository.findById(metaEstudoSemanaId)).thenReturn(Optional.of(metaEstudoSemana))
        `when`(metaDiariaRepository.findByMetaEstudoSemanaIdAndNomeDia(metaEstudoSemanaId, nomeDia)).thenReturn(null)

        val result = metaDeEstudoService.cadastrarMetaDiaria(metaEstudoSemanaId, nomeDia, data, qtdTempoEstudo, ativado)

        assertNotNull(result)
        assertEquals(nomeDia, result.nomeDia)
        assertEquals(data, result.data)
        assertEquals(qtdTempoEstudo, result.qtdTempoEstudo)
        assertEquals(ativado, result.ativado)
    }

    @Test
    fun `Cadastrar Meta Diaria deve salvar nova meta se não existir`() {
        val metaEstudoSemanaId = 1L
        val nomeDia = "segunda"
        val data = "2024-01-01"
        val qtdTempoEstudo = "2h"
        val ativado = true

        val aluno = criarAluno()

        val metaEstudoSemana = criarMetaEstudoSemana(aluno)

        `when`(metaSemanalRepository.findById(metaEstudoSemanaId)).thenReturn(Optional.of(metaEstudoSemana))
        `when`(metaDiariaRepository.findByMetaEstudoSemanaIdAndNomeDia(metaEstudoSemanaId, nomeDia)).thenReturn(null)

        val result = metaDeEstudoService.cadastrarMetaDiaria(
            metaEstudoSemanaId = metaEstudoSemanaId,
            nomeDia = nomeDia,
            data = data,
            qtdTempoEstudo = qtdTempoEstudo,
            ativado = ativado
        )

        assertEquals(nomeDia, result.nomeDia)
        assertEquals(data, result.data)
        assertEquals(qtdTempoEstudo, result.qtdTempoEstudo)
        assertEquals(ativado, result.ativado)

        verify(metaSemanalRepository, times(1)).findById(metaEstudoSemanaId)
        verify(metaDiariaRepository, times(1)).findByMetaEstudoSemanaIdAndNomeDia(metaEstudoSemanaId, nomeDia)
        verify(metaDiariaRepository, times(1)).save(any(TempoEstudo::class.java))
    }

    @Test
    fun `Cadastrar Meta Diaria deve atualizar meta existente`() {
        val metaEstudoSemanaId = 1L
        val nomeDia = "segunda"
        val data = "2024-01-01"
        val qtdTempoEstudo = "3h"
        val ativado = false

        val aluno = criarAluno()

        val metaEstudoSemana = criarMetaEstudoSemana(aluno)

        val metaDiariaExistente = criarTempoEstudo(metaEstudoSemana)

        `when`(metaSemanalRepository.findById(metaEstudoSemanaId)).thenReturn(Optional.of(metaEstudoSemana))
        `when`(metaDiariaRepository.findByMetaEstudoSemanaIdAndNomeDia(metaEstudoSemanaId, nomeDia))
            .thenReturn(metaDiariaExistente)

        val result = metaDeEstudoService.cadastrarMetaDiaria(
            metaEstudoSemanaId = metaEstudoSemanaId,
            nomeDia = nomeDia,
            data = data,
            qtdTempoEstudo = qtdTempoEstudo,
            ativado = ativado
        )

        assertEquals(nomeDia, result.nomeDia)
        assertEquals(data, result.data)
        assertEquals(qtdTempoEstudo, result.qtdTempoEstudo)
        assertEquals(ativado, result.ativado)

        verify(metaSemanalRepository, times(1)).findById(metaEstudoSemanaId)
        verify(metaDiariaRepository, times(1)).findByMetaEstudoSemanaIdAndNomeDia(metaEstudoSemanaId, nomeDia)
        verify(metaDiariaRepository, times(1)).save(any(TempoEstudo::class.java))
    }

    @Test
    fun `obterMetaEstudoSemana deve retornar MetaEstudoSemanaDto`() {
        val metaEstudoSemanaId = 1L
        val aluno = criarAluno()

        val metaEstudoSemana = MetaEstudoSemana(
            id = metaEstudoSemanaId,
            aluno = aluno,
            diasAtivos = null,
            horasTotal = 2.0,
            tempoSessao = emptyList()
        )

        val tempoEstudo = TempoEstudo(
            id = 1L,
            nomeDia = "segunda",
            data = "2023-10-01",
            qtdTempoEstudo = "2",
            ativado = true,
            metaEstudoSemana = metaEstudoSemana
        )
        metaEstudoSemana.diasAtivos = listOf(tempoEstudo)

        tempoEstudo.metaEstudoSemana = metaEstudoSemana

        `when`(metaSemanalRepository.findById(metaEstudoSemanaId)).thenReturn(Optional.of(metaEstudoSemana))

        val result = metaDeEstudoService.obterMetaEstudoSemana(metaEstudoSemanaId)

        assertNotNull(result)
        assertEquals(metaEstudoSemanaId, result.id)
        assertEquals(aluno.id, result.alunoId)
        result.diasAtivos?.let { assertEquals(1, it.size) }
        assertEquals(2.0, result.horasTotal)
        result.sessoes?.let { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `obterMetaEstudoSemana deve lançar exceção quando MetaEstudoSemana não for encontrada`() {
        val metaEstudoSemanaId = 1L

        `when`(metaSemanalRepository.findById(metaEstudoSemanaId)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            metaDeEstudoService.obterMetaEstudoSemana(metaEstudoSemanaId)
        }

        assertEquals("Meta de estudo semanal não encontrada", exception.message)
    }

    @Test
    fun `Deve mapear TempoEstudo para MetaDiariaDto`() {
        val tempoEstudo = criarTempoEstudo(criarMetaEstudoSemana(criarAluno()))
        val dto = mapMetaDiariaDto(tempoEstudo)
        assertEquals(tempoEstudo.nomeDia, dto.nomeDia)
        assertEquals(tempoEstudo.qtdTempoEstudo, dto.qtdTempoEstudo)
    }

    @Test
    fun `Cadastrar Meta Diaria deve lançar exceção para nomeDia inválido`() {
        val aluno = criarAluno()

        val metaEstudoSemana = criarMetaEstudoSemana(aluno)

        `when`(metaSemanalRepository.findById(1L)).thenReturn(Optional.of(metaEstudoSemana))

        val exception = assertThrows<IllegalArgumentException> {
            metaDeEstudoService.cadastrarMetaDiaria(1L, "diaInvalido", "2023-10-01", "2h", true)
        }

        assertEquals("Nome do dia inválido", exception.message)

        verify(metaDiariaRepository, never()).save(any())
    }

    @Test
    fun `cadastrarMetaDiaria deve criar nova MetaEstudoSemana quando não existir`() {
        val alunoId = 1L
        val nomeDia = "terça"
        val data = "2023-11-01"
        val qtdTempoEstudo = "4"
        val ativado = true

        val aluno = criarAluno()

        `when`(metaSemanalRepository.findById(alunoId)).thenReturn(Optional.empty())
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno))

        val result = metaDeEstudoService.cadastrarMetaDiaria(
            metaEstudoSemanaId = alunoId,
            nomeDia = nomeDia,
            data = data,
            qtdTempoEstudo = qtdTempoEstudo,
            ativado = ativado
        )

        assertNotNull(result)
        assertEquals(nomeDia, result.nomeDia)
        assertEquals(qtdTempoEstudo, result.qtdTempoEstudo)
        verify(metaSemanalRepository, times(1)).save(any(MetaEstudoSemana::class.java))
    }

    @Test
    fun `cadastrarMetaDiaria deve lançar exceção se aluno não for encontrado ao criar nova MetaEstudoSemana`() {
        val alunoId = 1L
        val nomeDia = "terça"
        val data = "2023-11-01"
        val qtdTempoEstudo = "4"
        val ativado = true

        `when`(metaSemanalRepository.findById(alunoId)).thenReturn(Optional.empty())
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            metaDeEstudoService.cadastrarMetaDiaria(
                metaEstudoSemanaId = alunoId,
                nomeDia = nomeDia,
                data = data,
                qtdTempoEstudo = qtdTempoEstudo,
                ativado = ativado
            )
        }

        assertEquals("Aluno não encontrado", exception.message)
        verify(metaSemanalRepository, never()).save(any())
    }

    @Test
    fun `Deve mapear TempoSessao para SessoesDto`() {
        val sessao = TempoSessao(
            id = 1L,
            diaSessao = "2023-10-01",
            qtdTempoSessao = 2,
            metaEstudoSemana = criarMetaEstudoSemana(criarAluno()),
            aluno = criarAluno()
        )

        val dto = mapToSessoesDto(sessao)

        assertEquals(sessao.id, dto.id)
        assertEquals(sessao.diaSessao, dto.diaSessao)
        assertEquals(sessao.qtdTempoSessao, dto.qtdTempoSessao)
    }

    @Test
    fun `obterMetaEstudoSemana deve retornar várias metas diárias`() {
        val metaEstudoSemanaId = 1L
        val aluno = criarAluno()

        val metaEstudoSemana = criarMetaEstudoSemana(aluno)
        val metasDiarias = listOf(
            criarTempoEstudo(metaEstudoSemana),
            criarTempoEstudo(metaEstudoSemana).apply { nomeDia = "terça"; qtdTempoEstudo = "3" }
        )
        metaEstudoSemana.diasAtivos = metasDiarias

        `when`(metaSemanalRepository.findById(metaEstudoSemanaId)).thenReturn(Optional.of(metaEstudoSemana))

        val result = metaDeEstudoService.obterMetaEstudoSemana(metaEstudoSemanaId)

        assertNotNull(result)
        result.diasAtivos?.let {
            assertEquals(2, it.size)
            assertEquals("segunda", it[0].nomeDia)
            assertEquals("terça", it[1].nomeDia)
        }
    }

}
