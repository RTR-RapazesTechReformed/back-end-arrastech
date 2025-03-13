package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import techForAll.techPoints.domain.Aluno
import techForAll.techPoints.domain.Pontuacao
import java.time.LocalDate

interface PontuacaoRepository : JpaRepository<Pontuacao, Long> {

    fun findByAlunoOrderByCurso(aluno: Aluno): List<Pontuacao>;

    @Query("""
    SELECT p 
    FROM Pontuacao p
    WHERE p.aluno.id = :idAluno
    AND (:dataInicio IS NULL OR p.dataCriacao  >= :dataInicio)
    AND (:dataFim IS NULL OR p.dataCriacao  <= :dataFim)
""")
    fun findAtividadesByAlunoAndPeriodo(
        idAluno: Long,
        dataInicio: String? = null,
        dataFim: String? = null
    ): List<Pontuacao>


    @Query("SELECT p FROM Pontuacao p WHERE p.dataEntrega IS NOT NULL")
    fun findAllPontuacoes(): List<Pontuacao>



}