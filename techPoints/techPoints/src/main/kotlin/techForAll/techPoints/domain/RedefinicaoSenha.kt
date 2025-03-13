package techForAll.techPoints.domain

import jakarta.persistence.*
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import java.time.LocalDateTime


@Entity
class RedefinicaoSenha(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var codigoRedefinicao: String,

    @Column(nullable = false)
    var dataCriacao: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var dataExpiracao: LocalDateTime,

    @Column(nullable = false)
    var valido: Boolean,

    @Column(nullable = false)
    var emailRedefinicao: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuarioRedefinicao: Usuario
) {

    fun enviarEmailRecuperacaoSenha(mailSender: JavaMailSender): Boolean {
        return try {
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            val emailContent = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333; background-color: #f4f4f4; padding: 20px;">
            <div style="max-width: 600px; margin: auto; background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                <h2 style="text-align: center; color: #007bff;">Recuperação de Senha</h2>
                <p style="text-align: center;">Olá!</p>
                <p style="text-align: center;">Para redefinir sua senha no Tech4Points, use o seguinte código:</p>
                <h3 style="text-align: center; color: #007bff; font-size: 24px; padding: 10px; border: 2px solid #007bff; border-radius: 5px; display: inline-block;">$codigoRedefinicao</h3>
                <p style="text-align: center;">Este código é válido até ${dataExpiracao.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}.</p>
                <p style="text-align: center;">Se você não solicitou a redefinição de senha, ignore este e-mail.</p>
                <p style="text-align: center;">Atenciosamente,<br>Equipe Tech4All</p>
                <footer style="text-align: center; font-size: 0.8em; color: #666;">
                    <p>Tech4All - Rua Doutor Joviano Pacheco de Aguirre, 255 - Campo Limpo, São Paulo, SP, 05788-290</p>
                    <p><a href="https://www.tech4all.com" style="color: #007bff; text-decoration: none;">Visite nosso site</a></p>
                </footer>
            </div>
        </body>
        </html>
        """.trimIndent()

            helper.setTo(emailRedefinicao)
            helper.setSubject("Recuperação de Senha")
            helper.setText(emailContent, true)

            mailSender.send(message)
            true
        } catch (e: Exception) {
            false
        }
    }

}
