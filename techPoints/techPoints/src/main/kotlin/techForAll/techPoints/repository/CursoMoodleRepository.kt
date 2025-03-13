package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.CursoMoodle

@Repository
interface CursoMoodleRepository: JpaRepository<CursoMoodle, Long> {


}