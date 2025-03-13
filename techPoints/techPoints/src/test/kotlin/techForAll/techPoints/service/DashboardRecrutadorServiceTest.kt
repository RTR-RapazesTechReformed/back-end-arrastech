package techForAll.techPoints.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import techForAll.techPoints.domain.*
import techForAll.techPoints.repository.*
import java.time.LocalDate
import java.util.*

class DashboardRecrutadorServiceTest {

    private val recrutadorRepository = mock(RecrutadorRepository::class.java)
    private val alunoRepository = mock(AlunoRepository::class.java)
    private val cursoRepository = mock(CursoMoodleRepository::class.java)
    private val notificacaoService = mock(NotificacaoService::class.java)
    private val service = DashboardRecrutadorService(recrutadorRepository, alunoRepository, cursoRepository, notificacaoService)

    private fun criarAluno(id: Long = 1L): Aluno {
        return Aluno(
            escolaridade = "Ensino Médio",
            sexo = "Masculino",
            etnia = "Branco",
            endereco = Endereco(
                id = 1L,
                rua = "Rua A",
                numero = "123",
                cidade = "São Paulo",
                estado = "SP",
                cep = "12345678"
            ),
            descricao = "Aluno dedicado",
            dtNasc = LocalDate.now().minusYears(18),
            nomeUsuario = "aluno1",
            cpf = "12345678901",
            senha = "senha123",
            primeiroNome = "João",
            sobrenome = "Silva",
            email = "joao.silva@example.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        ).apply { this.id = id }
    }

    private fun criarRecrutador(id: Long = 1L, empresa: Empresa): Recrutador {
        return Recrutador(
            favoritos = listOf(),
            interessados = listOf(),
            processoSeletivo = listOf(),
            contratados = listOf(),
            cancelados = listOf(),
            empresa = empresa,
            cargoUsuario = "Gerente",
            nomeUsuario = "recrutador1",
            cpf = "98765432100",
            senha = "senha123",
            primeiroNome = "Carlos",
            sobrenome = "Pereira",
            email = "carlos.pereira@example.com",
            telefone = "987654321",
            imagemPerfil = null,
            autenticado = true
        ).apply { this.id = id }
    }

    private fun criarEmpresa(): Empresa {
        return Empresa(
            id = 1L,
            nomeEmpresa = "Empresa X",
            cnpj = "12.345.678/0001-99",
            setorIndustria = "Tecnologia",
            telefoneContato = "1234-5678",
            emailCorporativo = "contato@empresax.com",
            endereco = Endereco(
                id = 1L,
                rua = "Rua das Flores",
                numero = "123",
                cidade = "São Paulo",
                estado = "SP",
                cep = "12345-678"
            ),
            representanteLegal = "João",
            sobrenomeRepresentante = "Silva"
        )
    }

    private fun criarCurso(id: Long = 1L, nome: String = "Curso Teste", categorias: String = "TI"): CursoMoodle {
        return CursoMoodle(
            id = id,
            nome = nome,
            categorias = categorias
        )
    }

    @Test
    fun `adicionarAluno deve adicionar aluno à lista e criar notificacao`() {
        val empresa = criarEmpresa()
        val recrutador = criarRecrutador(empresa = empresa)
        val aluno = criarAluno()

        `when`(recrutadorRepository.findById(1L)).thenReturn(Optional.of(recrutador))
        `when`(alunoRepository.findById(2L)).thenReturn(Optional.of(aluno))

        service.adicionarAluno(1L, 2L, "interessados")

        assertTrue(recrutador.interessados.contains(2L))
        verify(notificacaoService, times(1)).criarNotificacao(aluno, recrutador, empresa.id, "interessados")
        verify(recrutadorRepository, times(1)).save(recrutador)
    }

    @Test
    fun `adicionarAluno deve lançar excecao se recrutador nao for encontrado`() {
        `when`(recrutadorRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            service.adicionarAluno(1L, 2L, "interessados")
        }

        assertEquals("Recrutador não encontrado", exception.message)
    }

    @Test
    fun `moverParaLista deve mover aluno para lista correta`() {
        val empresa = criarEmpresa()
        val recrutador = criarRecrutador(empresa = empresa)
        recrutador.favoritos = listOf(1L)

        service.moverParaLista(1L, recrutador, "interessados")

        assertTrue(recrutador.interessados.contains(1L))
        assertFalse(recrutador.favoritos.contains(1L))
    }

    @Test
    fun `removerAluno deve remover aluno da lista correta`() {
        val empresa = criarEmpresa()
        val recrutador = criarRecrutador(empresa = empresa)
        recrutador.interessados = listOf(2L)

        `when`(recrutadorRepository.findById(1L)).thenReturn(Optional.of(recrutador))

        service.removerAluno(1L, 2L, "interessados")

        assertFalse(recrutador.interessados.contains(2L))
        verify(recrutadorRepository, times(1)).save(recrutador)
    }

    @Test
    fun `listarAlunos deve retornar lista de alunos para a lista correta`() {
        val empresa = criarEmpresa()
        val recrutador = criarRecrutador(empresa = empresa)
        val aluno = criarAluno()

        recrutador.favoritos = listOf(1L)

        `when`(recrutadorRepository.findById(1L)).thenReturn(Optional.of(recrutador))
        `when`(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno))

        val result = service.listarAlunos(1L, "favoritos")

        assertEquals(1, result.size)
        assertEquals("João", result[0]["primeiroNome"])
    }

    @Test
    fun `listarTodosOsCursos deve retornar todos os cursos`() {
        val cursos = listOf(criarCurso(1L, "Curso A"), criarCurso(2L, "Curso B"))

        `when`(cursoRepository.findAll()).thenReturn(cursos)

        val result = service.listarTodosOsCursos()

        assertEquals(2, result.size)
        assertEquals("Curso A", result[0].nome)
    }

    @Test
    fun `listarCursosPorCategoria deve filtrar cursos pela categoria`() {
        val cursos = listOf(
            criarCurso(1L, "Curso A", "TI"),
            criarCurso(2L, "Curso B", "Saúde"),
            criarCurso(3L, "Curso C", "TI")
        )

        `when`(cursoRepository.findAll()).thenReturn(cursos)

        val result = service.listarCursosPorCategoria("TI")

        assertEquals(2, result.size)
        assertTrue(result.all { it.categorias == "TI" })
    }
}