package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.TempoEstudo

@Repository
interface TempoEstudoRepository : JpaRepository<TempoEstudo, Long> {
    fun findByMetaEstudoSemanaIdAndNomeDia(metaEstudoSemanaId: Long, nomeDia: String): TempoEstudo?
}