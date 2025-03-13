package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.Recrutador

@Repository
interface RecrutadorRepository : JpaRepository<Recrutador, Long> {
}