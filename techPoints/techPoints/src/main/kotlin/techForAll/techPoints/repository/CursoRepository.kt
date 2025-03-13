package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import techForAll.techPoints.domain.Curso
import java.util.Optional

interface CursoRepository: JpaRepository<Curso, Long> {

    fun findByNome(nome: String): Optional<Curso>;
}