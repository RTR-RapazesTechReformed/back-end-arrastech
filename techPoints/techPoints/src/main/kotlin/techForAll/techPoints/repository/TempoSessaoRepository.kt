package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.TempoSessao

@Repository
interface TempoSessaoRepository : JpaRepository<TempoSessao, Long> {
    fun findAllByMetaEstudoSemanaId(metaEstudoSemanaId: Long): List<TempoSessao>
}