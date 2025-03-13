package techForAll.techPoints.service

import jakarta.mail.internet.MimeMessage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.mail.javamail.JavaMailSender
import techForAll.techPoints.domain.Aluno
import techForAll.techPoints.domain.Endereco
import techForAll.techPoints.domain.RedefinicaoSenha
import techForAll.techPoints.domain.Usuario
import techForAll.techPoints.repository.RedefinicaoSenhaRepository
import techForAll.techPoints.repository.UsuarioRepository
import java.time.LocalDate
import java.time.LocalDateTime

class ResetSenhaServiceTest {

    private val redefinicaoSenhaRepository = mock(RedefinicaoSenhaRepository::class.java)
    private val usuarioRepository = mock(UsuarioRepository::class.java)
    private val emailSender = mock(JavaMailSender::class.java)
    private val resetSenhaService = ResetSenhaService(redefinicaoSenhaRepository, usuarioRepository, emailSender)

    fun criarAluno(email: String = "test@example.com"): Aluno {
        val endereco = Endereco(
            id = 1L,
            rua = "Rua 1",
            numero = "123",
            cidade = "Cidade Teste",
            estado = "Estado Teste",
            cep = "12345-678"
        )
        return Aluno(
            escolaridade = "Ensino Médio",
            sexo = "Masculino",
            etnia = "Branco",
            endereco = endereco,
            dtNasc = LocalDate.of(2000, 1, 1),
            nomeUsuario = "aluno1",
            cpf = "12345678901",
            senha = "senha",
            primeiroNome = "Aluno",
            sobrenome = "Teste",
            email = email,
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )
    }

    fun criarRedefinicaoSenha(aluno: Aluno, token: String): RedefinicaoSenha {
        return RedefinicaoSenha(
            codigoRedefinicao = token,
            dataCriacao = LocalDateTime.now(),
            dataExpiracao = LocalDateTime.now().plusMinutes(30),
            valido = true,
            emailRedefinicao = aluno.email,
            usuarioRedefinicao = aluno
        )
    }

    @Test
    fun `senhaReset deve enviar email com sucesso`() {
        val emailUser = "test@example.com"
        val aluno = criarAluno(email = emailUser)
        val currentTime = LocalDateTime.of(2023, 1, 1, 12, 0)

        `when`(usuarioRepository.findByEmail(emailUser)).thenReturn(aluno)

        `when`(
            redefinicaoSenhaRepository.findByEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                emailUser, true, currentTime
            )
        ).thenReturn(emptyList())

        val mimeMessage = mock(MimeMessage::class.java)
        `when`(emailSender.createMimeMessage()).thenReturn(mimeMessage)
        doNothing().`when`(emailSender).send(any(MimeMessage::class.java))

        val response = resetSenhaService.senhaReset(emailUser)

        assertEquals(200, response.statusCodeValue)
        val responseBody = response.body as Map<*, *>
        assertTrue(responseBody["message"].toString().contains("Solicitação de redefinição de senha enviada para o e-mail"))
    }

    @Test
    fun `senhaReset deve retornar erro quando Aluno não encontrado`() {
        val emailUser = "test@example.com"
        `when`(usuarioRepository.findByEmail(emailUser)).thenReturn(null)

        val response = resetSenhaService.senhaReset(emailUser)

        assertEquals(404, response.statusCodeValue)
        assertEquals("Usuário não encontrado", (response.body as Map<*, *>)["message"])
    }

    @Test
    fun `atualizarSenha deve atualizar senha com sucesso`() {
        val emailUser = "test@example.com"
        val token = "validToken"
        val novaSenha = "newPassword"
        val aluno = criarAluno(email = emailUser)
        val redefinicaoSenha = criarRedefinicaoSenha(aluno, token)

        `when`(
            redefinicaoSenhaRepository.findByCodigoRedefinicaoAndEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                token, emailUser, true, LocalDateTime.now()
            )
        ).thenReturn(redefinicaoSenha)

        val response = resetSenhaService.atualizarSenha(emailUser, novaSenha, token)

        assertEquals(200, response.statusCodeValue)
        assertEquals("Senha atualizada com sucesso", (response.body as Map<*, *>)["message"])
        assertEquals(novaSenha, aluno.senha)
    }

    @Test
    fun `atualizarSenha deve retornar erro para token inválido`() {
        val emailUser = "test@example.com"
        val token = "invalidToken"
        val novaSenha = "newPassword"
        `when`(
            redefinicaoSenhaRepository.findByCodigoRedefinicaoAndEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                token, emailUser, true, LocalDateTime.now()
            )
        ).thenReturn(null)

        val response = resetSenhaService.atualizarSenha(emailUser, novaSenha, token)

        assertEquals(400, response.statusCodeValue)
        assertEquals("Token inválido ou expirado", (response.body as Map<*, *>)["message"])
    }

    @Test
    fun `verificarToken deve retornar sucesso para token válido`() {
        val emailUser = "test@example.com"
        val token = "validToken"
        val redefinicaoSenha = criarRedefinicaoSenha(criarAluno(email = emailUser), token)

        `when`(
            redefinicaoSenhaRepository.findByCodigoRedefinicaoAndEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                token, emailUser, true, LocalDateTime.now()
            )
        ).thenReturn(redefinicaoSenha)

        val response = resetSenhaService.verificarToken(token, emailUser)

        assertEquals(200, response.statusCodeValue)
    }

    @Test
    fun `verificarToken deve retornar erro para token inválido`() {
        val emailUser = "test@example.com"
        val token = "invalidToken"
        `when`(
            redefinicaoSenhaRepository.findByCodigoRedefinicaoAndEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                token, emailUser, true, LocalDateTime.now()
            )
        ).thenReturn(null)

        val response = resetSenhaService.verificarToken(token, emailUser)

        assertEquals(400, response.statusCodeValue)
        assertEquals("Token ou email inválido", (response.body as Map<*, *>)["message"])
    }

}