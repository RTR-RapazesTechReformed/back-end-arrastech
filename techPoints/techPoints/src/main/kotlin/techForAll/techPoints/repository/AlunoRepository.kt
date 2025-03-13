package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.Aluno
import java.util.*
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Repository
interface AlunoRepository : JpaRepository<Aluno, Long> {

    fun findByEmail(email: String): Optional<Aluno>;
  
    fun findByIdIn(ids: List<Any>): List<Aluno>

    @Query("""
        SELECT a.id FROM Aluno a
        JOIN a.endereco e
        WHERE (:sexo IS NULL OR a.sexo = :sexo)
        AND (:etnia IS NULL OR a.etnia = :etnia)
        AND (:idadeMaxima IS NULL OR (YEAR(CURRENT_DATE) - YEAR(a.dtNasc)) <= :idadeMaxima)
        AND (:cidade IS NULL OR e.cidade = :cidade)
        AND (:escolaridade IS NULL OR a.escolaridade = :escolaridade)
    """)
    fun findAlunosFiltrados(
        @Param("sexo") sexo: String?,
        @Param("etnia") etnia: String?,
        @Param("idadeMaxima") idadeMaxima: Int?,
        @Param("cidade") cidade: String?,
        @Param("escolaridade") escolaridade: String?
    ): List<Long>
}