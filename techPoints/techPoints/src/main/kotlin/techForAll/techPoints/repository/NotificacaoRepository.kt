package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.Notificacao

@Repository
interface NotificacaoRepository : JpaRepository<Notificacao, Long>{
    fun findByAlunoIdAndStatusFalse(idAluno: Long): List<Notificacao>
}
