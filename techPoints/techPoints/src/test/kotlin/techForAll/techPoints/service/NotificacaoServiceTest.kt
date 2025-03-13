package techForAll.techPoints.service

import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.mail.javamail.JavaMailSender
import techForAll.techPoints.domain.*
import techForAll.techPoints.repository.DadosEmpresaRepository
import techForAll.techPoints.repository.NotificacaoRepository
import techForAll.techPoints.service.NotificacaoService
import java.time.LocalDate
import java.util.*

class NotificacaoServiceTest {

    private val notificacaoRepository = mock(NotificacaoRepository::class.java)
    private val empresaRepository = mock(DadosEmpresaRepository::class.java)
    private val mailSender = mock(JavaMailSender::class.java)
    private val notificacaoService = NotificacaoService(notificacaoRepository, empresaRepository, mailSender)

    private fun criarAluno(id: Long = 1L): Aluno = Aluno(
        escolaridade = "Ensino Médio",
        sexo = "Masculino",
        etnia = "Branco",
        endereco = mock(Endereco::class.java),
        descricao = null,
        dtNasc = LocalDate.of(2000, 1, 1),
        nomeUsuario = "alunoTeste",
        cpf = "12345678901",
        senha = "senha123",
        primeiroNome = "Aluno",
        sobrenome = "Teste",
        email = "aluno@example.com",
        telefone = "11999999999",
        imagemPerfil = null,
        autenticado = true
    ).apply { this.id = id }

    private fun criarRecrutador(id: Long = 1L): Recrutador = Recrutador(
        favoritos = emptyList(),
        interessados = emptyList(),
        processoSeletivo = emptyList(),
        contratados = emptyList(),
        cancelados = emptyList(),
        empresa = mock(Empresa::class.java),
        cargoUsuario = "Recrutador de TI",
        nomeUsuario = "recrutadorTeste",
        cpf = "98765432100",
        senha = "senha123",
        primeiroNome = "Recrutador",
        sobrenome = "Teste",
        email = "recrutador@example.com",
        telefone = "11988887777",
        imagemPerfil = null,
        autenticado = true
    ).apply { this.id = id }

    private fun criarEmpresa(): Empresa = Empresa(
        id = 1L,
        nomeEmpresa = "Tech4All",
        cnpj = "12345678000123",
        emailCorporativo = "empresa@tech4all.com",
        telefoneContato = "11999999999",
        setorIndustria = "Tecnologia",
        endereco = mock()
    )

    @Test
    fun `criarNotificacao deve salvar notificacao com sucesso`() {
        val aluno = criarAluno()
        val recrutador = criarRecrutador()
        val empresa = criarEmpresa()
        val mimeMessage = mock(MimeMessage::class.java)

        `when`(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa))
        `when`(notificacaoRepository.save(any(Notificacao::class.java)))
            .thenAnswer { it.arguments[0] }
        `when`(mailSender.createMimeMessage()).thenReturn(mimeMessage)
        doNothing().`when`(mailSender).send(any(MimeMessage::class.java))

        val notificacao = notificacaoService.criarNotificacao(aluno, recrutador, 1L, "Lista de Contratados")

        assertNotNull(notificacao)
        assertEquals(aluno, notificacao.aluno)
        assertEquals(recrutador, notificacao.recrutador)
        assertEquals(empresa, notificacao.empresa)
        assertEquals("Lista de Contratados", notificacao.lista)

        verify(mailSender, times(1)).send(mimeMessage)
    }

    @Test
    fun `criarNotificacao deve falhar quando empresa nao for encontrada`() {
        val aluno = criarAluno()
        val recrutador = criarRecrutador()

        `when`(empresaRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<IllegalArgumentException> {
            notificacaoService.criarNotificacao(aluno, recrutador, 1L, "Lista")
        }

        assertEquals("Empresa com ID 1 não encontrada.", exception.message)
    }

    @Test
    fun `criarNotificacao deve logar erro quando email nao for enviado`() {
        val aluno = criarAluno()
        val recrutador = criarRecrutador()
        val empresa = criarEmpresa()

        `when`(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa))
        `when`(notificacaoRepository.save(any(Notificacao::class.java)))
            .thenAnswer { it.arguments[0] }
        `when`(mailSender.createMimeMessage()).thenThrow(RuntimeException("Erro ao criar mensagem"))

        val notificacao = notificacaoService.criarNotificacao(aluno, recrutador, 1L, "Lista de Contratados")

        assertNotNull(notificacao)
        assertEquals(aluno, notificacao.aluno)
        assertEquals(recrutador, notificacao.recrutador)
        assertEquals(empresa, notificacao.empresa)

        verify(mailSender, times(1)).createMimeMessage()
    }

    @Test
    fun `marcarNotificacaoComoLida deve marcar notificacao como lida`() {
        val aluno = criarAluno(id = 1L)
        val recrutador = criarRecrutador()
        val empresa = criarEmpresa()
        val notificacao = Notificacao(
            id = 1L,
            aluno = aluno,
            recrutador = recrutador,
            empresa = empresa,
            status = false
        )

        `when`(notificacaoRepository.findById(1L)).thenReturn(Optional.of(notificacao))
        `when`(notificacaoRepository.save(any(Notificacao::class.java)))
            .thenAnswer { it.arguments[0] }

        val result = notificacaoService.marcarNotificacaoComoLida(1L, 1L)

        assertTrue(result.status)
    }

    @Test
    fun `marcarNotificacaoComoLida deve falhar quando notificacao nao for encontrada`() {
        `when`(notificacaoRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<NoSuchElementException> {
            notificacaoService.marcarNotificacaoComoLida(1L, 1L)
        }

        assertEquals("Notificação não encontrada", exception.message)
    }

    @Test
    fun `marcarNotificacaoComoLida deve falhar quando notificacao nao pertence ao aluno`() {
        val alunoNaoRelacionado = criarAluno(id = 2L)
        val recrutador = criarRecrutador()
        val empresa = criarEmpresa()
        val notificacao = Notificacao(
            id = 1L,
            aluno = alunoNaoRelacionado,
            recrutador = recrutador,
            empresa = empresa,
            status = false
        )

        `when`(notificacaoRepository.findById(1L)).thenReturn(Optional.of(notificacao))

        val exception = assertThrows<IllegalArgumentException> {
            notificacaoService.marcarNotificacaoComoLida(1L, 1L)
        }

        assertEquals("Notificação não pertence ao aluno especificado", exception.message)
    }

    @Test
    fun `getNotificacoesPorAluno deve retornar notificacoes nao lidas`() {
        val alunoId = 1L
        val aluno = criarAluno(id = alunoId)
        val recrutador = criarRecrutador()
        val empresa = criarEmpresa()
        val notificacoes = listOf(
            Notificacao(
                id = 1L,
                aluno = aluno,
                recrutador = recrutador,
                empresa = empresa,
                status = false
            )
        )

        `when`(notificacaoRepository.findByAlunoIdAndStatusFalse(alunoId)).thenReturn(notificacoes)

        val result = notificacaoService.getNotificacoesPorAluno(alunoId)

        assertEquals(notificacoes, result)
    }
}
