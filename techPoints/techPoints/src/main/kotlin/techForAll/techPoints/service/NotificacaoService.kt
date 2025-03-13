package techForAll.techPoints.service

import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import techForAll.techPoints.domain.Aluno

import techForAll.techPoints.domain.Notificacao
import techForAll.techPoints.domain.Recrutador
import techForAll.techPoints.repository.DadosEmpresaRepository
import techForAll.techPoints.repository.NotificacaoRepository

@Service
class NotificacaoService(
    private val notificacaoRepository: NotificacaoRepository,
    private val empresaRepository: DadosEmpresaRepository,
    private val mailSender: JavaMailSender
) {

    fun criarNotificacao(
        aluno: Aluno,
        recrutador: Recrutador,
        idEmpresa: Long,
        lista: String
    ): Notificacao {
        val empresa = empresaRepository.findById(idEmpresa).orElseThrow {
            IllegalArgumentException("Empresa com ID $idEmpresa não encontrada.")
        }

        val notificacao = Notificacao(
            aluno = aluno,
            recrutador = recrutador,
            empresa = empresa,
            lista = lista
        )
        notificacaoRepository.save(notificacao)

        val emailEnviado = enviarEmailNotificacaoLista(
            aluno.email, aluno.primeiroNome, recrutador.primeiroNome, empresa.nomeEmpresa, lista
        )

        if (!emailEnviado) {
            println("Erro ao enviar o e-mail para o aluno ${aluno.primeiroNome}.")
        }

        return notificacao
    }

    private fun enviarEmailNotificacaoLista(
        emailDestino: String,
        nomeAluno: String,
        nomeRecrutador: String,
        nomeEmpresa: String,
        tipoLista: String
    ): Boolean {
        return try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            val emailContent = """
            <html>
                <body style="font-family: Arial, sans-serif; color: #333; background-color: #f4f4f4; padding: 20px;">
                    <div style="max-width: 600px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                        <h2 style="text-align: center; color: #007bff;">Notificação de Adição à Lista</h2>
                        <p style="text-align: center;">Olá, $nomeAluno!</p>
                        <p style="text-align: center;">Você foi adicionado(a) à lista <strong>$tipoLista</strong> pelo(a) recrutador(a) <strong>$nomeRecrutador</strong> da empresa <strong>$nomeEmpresa</strong>.</p>
                        <p style="text-align: center;">Por favor, acesse sua conta para mais detalhes e acompanhe seu progresso.</p>
                        <p style="text-align: center;">Atenciosamente,<br>Equipe Tech4All</p>
                        <footer style="text-align: center; font-size: 0.8em; color: #666;">
                            <p>Tech4All - Rua Doutor Joviano Pacheco de Aguirre, 255 - Campo Limpo, São Paulo, SP, 05788-290</p>
                            <p><a href="https://www.tech4all.com" style="color: #007bff; text-decoration: none;">Visite nosso site</a></p>
                        </footer>
                    </div>
                </body>
            </html>
            """.trimIndent()

            helper.setTo(emailDestino)
            helper.setSubject("Você foi adicionado a uma nova lista")
            helper.setText(emailContent, true)

            mailSender.send(message)
            true
        } catch (e: Exception) {
            println("Erro ao enviar e-mail: ${e.message}")
            false
        }
    }

    fun marcarNotificacaoComoLida(idAluno: Long, idNotificacao: Long): Notificacao {
        val notificacao = notificacaoRepository.findById(idNotificacao).orElseThrow { NoSuchElementException("Notificação não encontrada") }

        if (notificacao.aluno.id != idAluno) {
            throw IllegalArgumentException("Notificação não pertence ao aluno especificado")
        }

        notificacao.status = true
        return notificacaoRepository.save(notificacao)
    }
    fun getNotificacoesPorAluno(idAluno: Long): List<Notificacao> {
        return notificacaoRepository.findByAlunoIdAndStatusFalse(idAluno)
    }
}
