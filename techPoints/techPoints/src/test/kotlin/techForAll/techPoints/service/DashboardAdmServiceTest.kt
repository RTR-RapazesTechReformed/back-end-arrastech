package techForAll.techPoints.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.mock.web.MockMultipartFile
import techForAll.techPoints.domain.Aluno
import techForAll.techPoints.domain.Endereco
import techForAll.techPoints.dtos.DemografiaDto
import techForAll.techPoints.dtos.UsuarioInput
import techForAll.techPoints.repository.*
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ArrayBlockingQueue

class DashboardAdmServiceTest {

    private val dashAdmRepository: DashboardAdmRepository = mock()
    private val enderecoRepository: EnderecoRepository = mock()
    private val enderecoService: EnderecoService = mock()
    private val alunoRepository: AlunoRepository = mock()
    private val pontuacaoService: PontuacaoService = mock()
    private val usuarioService: UsuarioService = mock()
    private val dashAdmService: DashboardAdmService = DashboardAdmService(
        dashAdmRepository, enderecoRepository, enderecoService, alunoRepository,
        pontuacaoService, usuarioService
    )

    val endereco = Endereco(
        id = 1L,
        cidade = "São Paulo",
        estado = "SP",
        rua = "Rua Teste",
        numero = "123",
        cep = "03360330",
        dataCriacao = LocalDateTime.now(),
    )
    val usuario = Aluno(
        nomeUsuario = "aluno01",
        cpf = "12345678900",
        senha = "senha123",
        primeiroNome = "João",
        sobrenome = "Silva",
        email = "joao.silva@email.com",
        telefone = "123456789",
        escolaridade = "Ensino Médio",
        sexo = "Masculino",
        etnia = "Branco",
        autenticado = false,
        endereco = endereco,
        descricao = null,
        dtNasc = LocalDate.of(2000, 1, 1),
        imagemPerfil = null
    )

    @Test
    fun `getAlunosPorCurso deve retornar lista de CursoAlunosDto corretamente`() {

        val rankingPorCurso = mapOf(
            1L to mapOf(
                "nomeCurso" to "Curso A",
                "ranking" to listOf(
                    mapOf("idAluno" to 1L, "nome" to "Aluno 1"),
                    mapOf("idAluno" to 2L, "nome" to "Aluno 2")
                )
            ),
            2L to mapOf(
                "nomeCurso" to "Curso B",
                "ranking" to listOf(
                    mapOf("idAluno" to 3L, "nome" to "Aluno 3")
                )
            )
        )

        `when`(pontuacaoService.recuperarRankingPorCurso()).thenReturn(rankingPorCurso)

        val resultado = dashAdmService.getAlunosPorCurso()

        assertEquals(2, resultado.size)
        assertEquals("Curso A", resultado[0].nomeCurso)
        assertEquals(2, resultado[0].quantidadeAlunos)
        assertEquals("Curso B", resultado[1].nomeCurso)
        assertEquals(1, resultado[1].quantidadeAlunos)
    }

    @Test
    fun `deve lancar excecao quando lista de alunos estiver vazia`() {

        val tipoLista = "contratados"
        val idEmpresa: Long? = 1L

        `when`(dashAdmService.getAlunosPorTipoLista(tipoLista, idEmpresa)).thenReturn(emptyList())

        val exception = assertThrows<NoSuchElementException> {
            dashAdmService.getDemografiaPorTipoLista(tipoLista, idEmpresa)
        }

        assertEquals("Nenhum aluno encontrado", exception.message)
    }


    @Test
    fun `deve processar lista de strings e retornar ArrayBlockingQueue com ids`() {

        val idStrings = listOf("[1, 2, 3]", "[4, 5]")

        val resultado = dashAdmService.processarIdsJson(idStrings)

        assertEquals(5, resultado.size)
        assertTrue(resultado.containsAll(listOf(1L, 2L, 3L, 4L, 5L)))
    }

    @Test
    fun `deve processar array de strings e retornar ArrayBlockingQueue com ids`() {

        val idStrings = listOf(arrayOf("[1, 2]", "[3, 4]"))

        val resultado = dashAdmService.processarIdsJson(idStrings)

        assertEquals(4, resultado.size)
        assertTrue(resultado.containsAll(listOf(1L, 2L, 3L, 4L)))
    }

    @Test
    fun `deve lançar exceção quando entrada estiver vazia ou inválida`() {

        val idStrings = listOf<String>()

        assertThrows<NoSuchElementException> {
            dashAdmService.processarIdsJson(idStrings)
        }
    }

    @Test
    fun `deve ignorar valores nao numericos na lista`() {

        val idStrings = listOf("[1, 2, abc, 4]", "[5, xyz]")


        val resultado = dashAdmService.processarIdsJson(idStrings)

        assertEquals(4, resultado.size)
        assertTrue(resultado.containsAll(listOf(1L, 2L, 4L, 5L)))
    }

        @Test
    fun `gerarRelatorioTXT deve executar sem erros`() {


        val alunoComCursosMock = mapOf<String, Any>(
            "id" to 1,
            "nomeUsuario" to "usuario_teste",
            "cpf" to "12345678900",
            "primeiroNome" to "João",
            "sobrenome" to "Silva",
            "email" to "joao.silva@email.com",
            "telefone" to "11987654321",
            "tipoUsuario" to "Aluno",
            "sexo" to "Masculino",
            "etnia" to "Branco",
            "escolaridade" to "Ensino Superior",
            "dataNascimento" to "1990-05-10",
            "deletado" to "false",
            "endereco" to endereco,
            "cursos" to mapOf<Long, Map<String, Any>>(
                101L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 100),
                102L to mapOf("nomeCurso" to "Curso B", "pontosTotais" to 80)
            )
        )

        `when`(usuarioService.buscarUsuarioPorId(1)).thenReturn(alunoComCursosMock)
        `when`(dashAdmService.comporAlunoComCursos(1)).thenReturn(alunoComCursosMock)

        val ids = listOf(1L)

        val txt = assertDoesNotThrow {
            dashAdmService.gerarRelatorioTXT(ids)
        }

        assertNotNull(txt)
    }


    @Test
    fun `comporAlunoComCursos deve retornar mapa com dados corretos`() {
        val idAluno = 1L

        val enderecoMock = Endereco(
            cep = "12345-678",
            rua = "Rua das Flores",
            numero = "123",
            cidade = "São Paulo",
            estado = "SP"
        )

        val alunoMock = mapOf<String, Any?>(
            "id" to idAluno,
            "nomeUsuario" to "usuario_teste",
            "cpf" to "12345678900",
            "primeiroNome" to "João",
            "sobrenome" to "Silva",
            "email" to "joao.silva@email.com",
            "telefone" to "11987654321",
            "tipoUsuario" to "Aluno",
            "sexo" to "Masculino",
            "etnia" to "Branco",
            "escolaridade" to "Ensino Superior",
            "dataNascimento" to "1990-05-10",
            "deletado" to "false",
            "endereco" to enderecoMock
        )

        val cursosMock = mapOf<Long, Map<String, Any>>(
            101L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 100),
            102L to mapOf("nomeCurso" to "Curso B", "pontosTotais" to 80)
        )

        `when`(usuarioService.buscarUsuarioPorId(idAluno)).thenReturn(alunoMock)
        `when`(pontuacaoService.recuperarPontosTotaisPorCurso(idAluno)).thenReturn(cursosMock)

        val resultado = dashAdmService.comporAlunoComCursos(idAluno)

        assertNotNull(resultado)
        assertEquals(idAluno, resultado["id"])
        assertEquals("usuario_teste", resultado["nomeUsuario"])
        assertTrue(resultado["cursos"] is Map<*, *>)
        assertEquals(cursosMock, resultado["cursos"])

        val endereco = resultado["endereco"] as Map<*, *>
        assertEquals("Rua das Flores", endereco["rua"])
        assertEquals("São Paulo", endereco["cidade"])
        assertEquals("SP", endereco["estado"])
    }

    @Test
    fun `comporAlunoComCursos deve lançar IllegalStateException quando o endereco for nulo`() {
        val idAluno = 1L

        val alunoMock = mapOf<String, Any?>(
            "id" to idAluno,
            "nomeUsuario" to "usuario_teste",
            "cpf" to "12345678900",
            "primeiroNome" to "João",
            "sobrenome" to "Silva",
            "email" to "joao.silva@email.com",
            "telefone" to "11987654321",
            "tipoUsuario" to "Aluno",
            "sexo" to "Masculino",
            "etnia" to "Branco",
            "escolaridade" to "Ensino Superior",
            "dataNascimento" to "1990-05-10",
            "deletado" to "false",
            "endereco" to null
        )

        val cursosMock = mapOf<Long, Map<String, Any>>(
            101L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 100),
            102L to mapOf("nomeCurso" to "Curso B", "pontosTotais" to 80)
        )

        `when`(usuarioService.buscarUsuarioPorId(idAluno)).thenReturn(alunoMock)
        `when`(pontuacaoService.recuperarPontosTotaisPorCurso(idAluno)).thenReturn(cursosMock)

        val exception = assertThrows(IllegalStateException::class.java) {
            dashAdmService.comporAlunoComCursos(idAluno)
        }

        assertEquals("Endereço não pode ser nulo. Verifique o ID informado e tente novamente.", exception.message)
    }


    @Test
    fun `getAlunosPorTipoLista deve retornar lista de IDs quando tipoLista for 'todos' e idEmpresa for nulo`() {
        val tipoLista = "todos"
        val idEmpresa: Long? = null
        val expectedIds = listOf(1L, 2L, 3L)

        `when`(dashAdmRepository.findIdsTodos()).thenReturn(expectedIds)

        val result = dashAdmService.getAlunosPorTipoLista(tipoLista, idEmpresa)

        assertEquals(expectedIds, result)

        verify(dashAdmRepository).findIdsTodos()
    }

    @Test
    fun `getAlunosPorTipoLista deve retornar lista de IDs quando tipoLista for 'contratados' e idEmpresa for nulo`() {
        val tipoLista = "contratados"
        val idEmpresa: Long? = null
        val expectedIds = listOf(4L, 5L)


        `when`(dashAdmRepository.findIdsContratados()).thenReturn(expectedIds)

        val result = dashAdmService.getAlunosPorTipoLista(tipoLista, idEmpresa)

        assertEquals(expectedIds, result)

        verify(dashAdmRepository).findIdsContratados()
    }

    @Test
    fun `getAlunosPorTipoLista deve retornar lista de IDs quando tipoLista for 'interessados' e idEmpresa for nulo`() {
        val tipoLista = "interessados"
        val idEmpresa: Long? = null
        val expectedIds = listOf(6L, 7L)

        `when`(dashAdmRepository.findIdsInteressados()).thenReturn(expectedIds)

        val result = dashAdmService.getAlunosPorTipoLista(tipoLista, idEmpresa)

        assertEquals(expectedIds, result)

        verify(dashAdmRepository).findIdsInteressados()
    }

    @Test
    fun `getAlunosPorTipoLista deve lançar IllegalArgumentException quando tipoLista for inválido`() {
        val tipoLista = "invalido"
        val idEmpresa: Long? = null

        val exception = assertThrows<IllegalArgumentException> {
            dashAdmService.getAlunosPorTipoLista(tipoLista, idEmpresa)
        }

        assertEquals("Tipo de lista inválido", exception.message)
    }

    @Test
    fun `deve processar arquivo CSV com sucesso`() {

        val conteudoCsv = """
            nomeUsuario,cpf,email,primeiroNome,sobrenome,telefone,senha,dtNasc,escolaridade,sexo,etnia,cep,rua,numero,cidade,estado
            user1,12345678901,user1@email.com,User,One,123456789,@Senha123,2000-01-01,Ensino Médio,Masc,Branco,12345000,Rua 1,100,Cidade 1,SP
        """.trimIndent()

        val arquivo = MockMultipartFile(
            "file",
            "usuarios.csv",
            "text/csv",
            ByteArrayInputStream(conteudoCsv.toByteArray())
        )

        val endereco = Endereco(id = 1L, cep = "12345000", rua = "Rua 1", numero = "100", cidade = "Cidade 1", estado = "SP")

        `when`(enderecoRepository.findByCepAndNumero("12345000", "100"))
            .thenReturn(endereco)

        val resultado = dashAdmService.processarArquivoCsv(arquivo)


        assertEquals(1, resultado["sucesso"])
        assertTrue((resultado["erros"] as List<*>).isEmpty())
    }

    @Test
    fun `deve retornar erro ao processar arquivo vazio`() {

        val arquivoVazio = MockMultipartFile("file", "vazio.csv", "text/csv", byteArrayOf())


        val exception = assertThrows<IllegalArgumentException> {
            dashAdmService.processarArquivoCsv(arquivoVazio)
        }
        assertEquals("Arquivo inválido. Apenas arquivos CSV são permitidos.", exception.message)
    }

    @Test
    fun `deve retornar erro ao processar arquivo com formato inválido`() {

        val arquivoInvalido = MockMultipartFile("file", "arquivo.txt", "text/plain", "conteúdo inválido".toByteArray())

        val exception = assertThrows<IllegalArgumentException> {
            dashAdmService.processarArquivoCsv(arquivoInvalido)
        }
        assertEquals("Arquivo inválido. Apenas arquivos CSV são permitidos.", exception.message)
    }

    @Test
    fun `deve retornar erro para linha com número de colunas incorreto`() {

        val conteudoCsv = """
            nomeUsuario,cpf,email,primeiroNome,sobrenome,telefone,senha,dtNasc,escolaridade,sexo,etnia,cep,rua,numero,cidade,estado
            user1,12345678901,user1@email.com,User,One,123456789,@Senha123,2000-01-01,Ensino Médio,Masc
        """.trimIndent()

        val arquivo = MockMultipartFile(
            "file",
            "usuarios.csv",
            "text/csv",
            ByteArrayInputStream(conteudoCsv.toByteArray())
        )

        val resultado = dashAdmService.processarArquivoCsv(arquivo)

        assertEquals(0, resultado["sucesso"])
        val erros = resultado["erros"] as List<*>
        assertEquals(1, erros.size)
        assertTrue(erros[0].toString().contains("Número de colunas incorreto"))

    }
    @Test
    fun `deve retornar erro ao processar arquivo TXT com registros inválidos`() {

        val conteudo = """
            00HEADER COM DADOS DO ARQUIVO
            02usuario01          1234567ERRO  João                Silva               11987654321 1990-05-10Ensino Superior    MasculinoNegro   123456789Rua A                      00001São Paulo               SP
            01        1
        """.trimIndent()

        val arquivo = MockMultipartFile(
            "file", "usuarios.txt", "text/plain", conteudo.toByteArray(StandardCharsets.UTF_8)
        )

        val resultado = dashAdmService.processarArquivoTxt(arquivo)

        assertEquals(0, resultado["sucesso"])

        val mockInput =  mock(UsuarioInput::class.java)

        verify(usuarioService, never()).cadastrarUsuario(mockInput)
    }

    @Test
    fun `deve retornar erro ao processar arquivo TXT inválido`() {
        val arquivo = MockMultipartFile(
            "file", "usuarios.pdf", "application/pdf", ByteArray(0)
        )

        val exception = assertThrows<IllegalArgumentException> {
            dashAdmService.processarArquivoTxt(arquivo)
        }

        assertEquals("Arquivo inválido. Apenas arquivos TXT são permitidos.", exception.message)
    }

    @Test
    fun `deve processar fila e retornar contagem correta de cursos`() {

        val idsFila = ArrayBlockingQueue<Long>(10)
        idsFila.addAll(listOf(1L, 2L))

        `when`(pontuacaoService.recuperarPontosTotaisPorCurso(1L)).thenReturn(
            mapOf(
                1L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 100),
                2L to mapOf("nomeCurso" to "Curso B", "pontosTotais" to 50)
            )
        )
        `when`(pontuacaoService.recuperarPontosTotaisPorCurso(2L)).thenReturn(
            mapOf(
                1L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 80),
                3L to mapOf("nomeCurso" to "Curso C", "pontosTotais" to 70)
            )
        )

        val resultado = dashAdmService.processarFilaCursosFeitosPorAlunos(idsFila)

        assertEquals(3, resultado.size)
        assertEquals(2, resultado["Curso A"])
        assertEquals(1, resultado["Curso B"])
        assertEquals(1, resultado["Curso C"])
    }

    @Test
    fun `deve retornar mapa vazio quando fila estiver vazia`() {

        val idsFila = ArrayBlockingQueue<Long>(10)

        val resultado = dashAdmService.processarFilaCursosFeitosPorAlunos(idsFila)

        assertTrue(resultado.isEmpty())
        verifyNoInteractions(pontuacaoService)
    }

    @Test
    fun `deve ignorar cursos com pontuação zero`() {

        val idsFila = ArrayBlockingQueue<Long>(10)
        idsFila.add(1L)

        `when`(pontuacaoService.recuperarPontosTotaisPorCurso(1L)).thenReturn(
            mapOf(
                1L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 0),
                2L to mapOf("nomeCurso" to "Curso B", "pontosTotais" to 100)
            )
        )

        val resultado = dashAdmService.processarFilaCursosFeitosPorAlunos(idsFila)

        assertEquals(1, resultado.size)
        assertEquals(1, resultado["Curso B"])
        assertNull(resultado["Curso A"])
    }

    @Test
    fun `deve acumular contagem de cursos para múltiplos alunos`() {

        val idsFila = ArrayBlockingQueue<Long>(10)
        idsFila.addAll(listOf(1L, 2L))

        `when`(pontuacaoService.recuperarPontosTotaisPorCurso(1L)).thenReturn(
            mapOf(
                1L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 100),
                2L to mapOf("nomeCurso" to "Curso B", "pontosTotais" to 50)
            )
        )
        `when`(pontuacaoService.recuperarPontosTotaisPorCurso(2L)).thenReturn(
            mapOf(
                3L to mapOf("nomeCurso" to "Curso A", "pontosTotais" to 80),
                4L to mapOf("nomeCurso" to "Curso B", "pontosTotais" to 90)
            )
        )

        val resultado = dashAdmService.processarFilaCursosFeitosPorAlunos(idsFila)

        assertEquals(2, resultado["Curso A"])
        assertEquals(2, resultado["Curso B"])
    }

}
