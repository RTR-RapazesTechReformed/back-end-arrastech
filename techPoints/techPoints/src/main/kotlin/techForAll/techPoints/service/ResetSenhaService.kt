
package techForAll.techPoints.service
import techForAll.techPoints.domain.RedefinicaoSenha
import org.springframework.http.ResponseEntity
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import techForAll.techPoints.repository.RedefinicaoSenhaRepository
import techForAll.techPoints.repository.UsuarioRepository
import java.time.LocalDateTime
import java.util.*


@Service
class ResetSenhaService(
    private val redefinicaoSenhaRepository: RedefinicaoSenhaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val emailSender: JavaMailSender
) {
    fun senhaReset(emailUser: String): ResponseEntity<Any> {
        return try {
            val trocasSenhaAtivas = redefinicaoSenhaRepository.findByEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                emailUser, true, LocalDateTime.now()
            )

            val usuario = usuarioRepository.findByEmail(emailUser)

            if (usuario == null) {
                return ResponseEntity.status(404).body(mapOf("message" to "Usuário não encontrado"))
            }


            val redefinicaoSenha = RedefinicaoSenha(
                codigoRedefinicao = gerarResetCode(),
                dataCriacao = LocalDateTime.now(),
                dataExpiracao = calcularValidade(),
                valido = true,
                emailRedefinicao = emailUser,
                usuarioRedefinicao = usuario
            )

            redefinicaoSenhaRepository.save(redefinicaoSenha)

            val emailEnviadoComSucesso = redefinicaoSenha.enviarEmailRecuperacaoSenha(emailSender)

            return if (emailEnviadoComSucesso) {
                val response = mapOf(
                    "message" to "Solicitação de redefinição de senha enviada para o e-mail",
                    "redefinicaoSenha" to mapOf(
                        "codigoRedefinicao" to redefinicaoSenha.codigoRedefinicao,
                        "dataCriacao" to redefinicaoSenha.dataCriacao,
                        "dataExpiracao" to redefinicaoSenha.dataExpiracao,
                        "emailUsuario" to redefinicaoSenha.usuarioRedefinicao.email
                    )
                )
                ResponseEntity.status(200).body(response)
            } else {
                ResponseEntity.status(500).body(mapOf("message" to "Erro ao enviar o e-mail"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor"))
        }
    }

    fun atualizarSenha(emailUser: String, novaSenha: String, token: String): ResponseEntity<Any> {
        return try {
            val redefinicaoSenha = redefinicaoSenhaRepository.findByCodigoRedefinicaoAndEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                token, emailUser, true, LocalDateTime.now()
            )

            if (redefinicaoSenha != null) {

                if (redefinicaoSenha.valido == false){
                    return ResponseEntity.status(400).body(mapOf("message" to "Token inválido ou expirado"))
                }
                val usuario = redefinicaoSenha.usuarioRedefinicao
                usuario.senha = novaSenha

                redefinicaoSenha.valido = false

                redefinicaoSenhaRepository.save(redefinicaoSenha)
                usuarioRepository.save(usuario)

                ResponseEntity.status(200).body(mapOf("message" to "Senha atualizada com sucesso"))
            } else {
                ResponseEntity.status(400).body(mapOf("message" to "Token inválido ou expirado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor"))
        }
    }

    fun verificarToken(codigoRedefinicao: String, emailUser: String): ResponseEntity<Any> {
        return try {
            val redefinicaoSenha = redefinicaoSenhaRepository.findByCodigoRedefinicaoAndEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
                codigoRedefinicao, emailUser, true, LocalDateTime.now()
            )

            if (redefinicaoSenha != null) {
                ResponseEntity.status(200).build()
            } else {
                ResponseEntity.status(400).body(mapOf("message" to "Token ou email inválido"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor"))
        }
    }

    private fun gerarResetCode(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
    private fun calcularValidade(): LocalDateTime {
        return LocalDateTime.now().plusMinutes(30)
    }
}