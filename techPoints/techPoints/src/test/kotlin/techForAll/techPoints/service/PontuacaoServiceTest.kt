package techForAll.techPoints.service

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import techForAll.techPoints.domain.*
import techForAll.techPoints.repository.AlunoRepository
import techForAll.techPoints.repository.DadosEmpresaRepository
import techForAll.techPoints.repository.DashboardAdmRepository
import techForAll.techPoints.repository.PontuacaoRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

@ExtendWith(MockitoExtension::class)
class PontuacaoServiceTest{

    @Mock
    private lateinit var entityManager: EntityManager

    @Mock
    private lateinit var pontuacaoRepository: PontuacaoRepository

    @Mock
    private lateinit var alunoRepository: AlunoRepository

    @Mock
    private lateinit var dashboardAdmRepository: DashboardAdmRepository

    @Mock
    private lateinit var dadosEmpresaRepository: DadosEmpresaRepository

    @Mock
    private lateinit var usuarioService: UsuarioService

    @InjectMocks
    private lateinit var pontuacaoService: PontuacaoService

    @Test
    fun `Aluno Existe deve retornar um Aluno caso exista`() {
        val alunoId = 1L
        val aluno = Aluno(
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
            nomeUsuario = "junior",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Junior",
            sobrenome = "Games",
            email = "junior@email.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno))

        val result = pontuacaoService.alunoExiste(alunoId)

        assertEquals(aluno, result)
    }

    @Test
    fun `Aluno Existe deve lançar exceção se Aluno não existir`() {
        val alunoId = 2L
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            pontuacaoService.alunoExiste(alunoId)
        }

        assertEquals("Aluno não Encontrado!", exception.message)
    }

    @Test
    fun `Recuperar Todos Cursos Aluno Pontuacao deve retornar mapa de pontuacoes`() {
        val alunoId = 1L
        val aluno = Aluno(
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
            nomeUsuario = "junior",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Junior",
            sobrenome = "Games",
            email = "junior@email.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )
        val pontuacao = Pontuacao(
            id = 1L,
            dataEntrega = "2023-01-01 12:00:00",
            nomeAtividade = "Atividade 1",
            notaAtividade = 10.0,
            notaAluno = 9.0,
            curso = CursoMoodle
                (
                id = 1L,
                nome = "Curso 1",
                categorias = "Tecnologia"
            ),
            aluno = aluno,
            aluno_email = "junior@email.com",
            curso_nome = "Curso 1",
            dataCriacao = "2024-12-03"
        )
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno))
        `when`(pontuacaoRepository.findByAlunoOrderByCurso(aluno)).thenReturn(listOf(pontuacao))

        val result = pontuacaoService.recuperarTodosCursosAlunoPontuacao(alunoId)

        assertEquals(1, result.size)
        assertEquals(1, result[1L]?.size)
        assertEquals(pontuacao.id, result[1L]?.get(0)?.id)
    }

    @Test
    fun `Recuperar KPI Semana Passada And Atual deve retornar mapa de pontos`() {
        val alunoId = 1L
        val aluno = Aluno(
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
            nomeUsuario = "junior",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Junior",
            sobrenome = "Games",
            email = "junior@email.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )
        val pontuacao = Pontuacao(
            id = 1L,
            dataEntrega = LocalDateTime.now().plusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            nomeAtividade = "Atividade 1",
            notaAtividade = 10.0,
            notaAluno = 9.0,
            curso = CursoMoodle
                (
                id = 1L,
                nome = "Curso 1",
                categorias = "Tecnologia"
            ),
            aluno = aluno,
            aluno_email = "junior@email.com",
            curso_nome = "Curso 1",
            dataCriacao = "2024-12-03"
        )
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno))
        `when`(pontuacaoRepository.findByAlunoOrderByCurso(aluno)).thenReturn(listOf(pontuacao))

        val result = pontuacaoService.recuperarKPISemanaPassadaAndAtual(alunoId)

        assertEquals(90, result["semanaAtual"]?.values?.first())
        assertEquals(0, result["semanaPassada"]?.values?.first())
    }

    @Test
    fun `Recuperar Pontos Conquistados Por Mês deve retornar mapa de pontos`() {
        val alunoId = 1L
        val aluno = Aluno(
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
            nomeUsuario = "junior",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Junior",
            sobrenome = "Games",
            email = "junior@email.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )
        val pontuacao = Pontuacao(
            id = 1L,
            dataEntrega = "2023-01-01 12:00:00",
            nomeAtividade = "Atividade 1",
            notaAtividade = 10.0,
            notaAluno = 9.0,
            curso = CursoMoodle
                (
                id = 1L,
                nome = "Curso 1",
                categorias = "Tecnologia"
            ),
            aluno = aluno,
            aluno_email = "junior@email.com",
            curso_nome = "Curso 1",
            dataCriacao = "2024-12-03"
        )
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno))
        `when`(pontuacaoRepository.findByAlunoOrderByCurso(aluno)).thenReturn(listOf(pontuacao))

        val result = pontuacaoService.recuperarPontosConquistadosPorMes(alunoId)

        assertEquals(1, result.size)
        assertEquals(1, result[Pair(1L, "Curso 1")]?.size)
        assertEquals(90, result[Pair(1L, "Curso 1")]?.get(YearMonth.of(2023, 1)))
    }

    @Test
    fun `Recuperar Pontos Totais Por Curso deve retornar mapa de pontos`() {
        val alunoId = 1L
        val aluno = Aluno(
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
            nomeUsuario = "junior",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Junior",
            sobrenome = "Games",
            email = "junior@email.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )
        val pontuacao = Pontuacao(
            id = 1L,
            dataEntrega = "2023-01-01 12:00:00",
            nomeAtividade = "Atividade 1",
            notaAtividade = 10.0,
            notaAluno = 9.0,
            curso = CursoMoodle
                (
                id = 1L,
                nome = "Curso 1",
                categorias = "Tecnologia"
            ),
            aluno = aluno,
            aluno_email = "junior@email.com",
            curso_nome = "Curso 1",
            dataCriacao = "2024-12-03"
        )
        `when`(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno))
        `when`(pontuacaoRepository.findByAlunoOrderByCurso(aluno)).thenReturn(listOf(pontuacao))

        val result = pontuacaoService.recuperarPontosTotaisPorCurso(alunoId)

        assertEquals(1, result.size)
        assertEquals("Curso 1", result[1L]?.get("nomeCurso"))
        assertEquals(90, result[1L]?.get("pontosTotais"))
    }

    @Test
    fun `Buscar Listas Com Aluno deve retornar listas com aluno`() {
        val alunoId = 1L
        val empresa = Empresa(
            id = 1L,
            cnpj = "12345678901234",
            emailCorporativo = "mariazinha@email.com",
            endereco = Endereco(
                cep = "12345678",
                rua = "Rua das Laranjeiras",
                numero = "123",
                cidade = "São Paulo",
                estado = "São Paulo"
            ),
            setorIndustria = "Tecnologia",
            telefoneContato = "123456789",
            nomeEmpresa = "Empresa 1"
        )
        val usuario = Aluno(
            nomeUsuario = "recrutador",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Recrutador",
            sobrenome = "Empresa",
            email = "recrutador@empresa.com",
            telefone = "123456789",
            imagemPerfil = null,
            sexo = "Masculino",
            dtNasc = LocalDate.of(2000, 1, 1),
            escolaridade = "Ensino Médio",
            etnia = "Branco",
            endereco = Endereco(
                cep = "12345678",
                rua = "Rua das Laranjeiras",
                numero = "123",
                cidade = "São Paulo",
                estado = "São Paulo"
            ),
            autenticado = true,
        )
        `when`(dadosEmpresaRepository.findAll()).thenReturn(listOf(empresa))
        `when`(dashboardAdmRepository.findIdsRecrutadorAndContratadosByEmpresa(1L)).thenReturn(listOf(arrayOf(1L, "[1]")))
        `when`(usuarioService.buscarUsuarioPorId(1L)).thenReturn(mapOf("email" to "recrutador@empresa.com"))

        val result = pontuacaoService.buscarListasComAluno(alunoId)

        assertEquals(1, result.size)
        assertEquals("Empresa 1", result[0].nomeEmpresa)
        assertEquals("recrutador@empresa.com", result[0].emailRecrutador)
        assertEquals("contratados", result[0].lista)
    }
}