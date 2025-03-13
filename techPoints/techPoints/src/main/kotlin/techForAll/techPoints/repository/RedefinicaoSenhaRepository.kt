package techForAll.techPoints.repository
import org.springframework.data.jpa.repository.JpaRepository
import techForAll.techPoints.domain.RedefinicaoSenha
import java.time.LocalDateTime


interface RedefinicaoSenhaRepository : JpaRepository<RedefinicaoSenha, Long> {
    fun findByEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
        email: String,
        valido: Boolean,
        dataExpiracao: LocalDateTime
    ): List<RedefinicaoSenha>

    fun findByCodigoRedefinicaoAndEmailRedefinicaoAndValidoAndDataExpiracaoAfter(
        codigoRedefinicao: String,
        emailRedefinicao: String,
        valido: Boolean,
        dataExpiracao: LocalDateTime
    ): RedefinicaoSenha?
}