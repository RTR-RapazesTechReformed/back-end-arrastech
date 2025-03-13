package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.Curso
import techForAll.techPoints.dtos.CursoAlunosDto

@Repository
interface DashboardAdmRepository : JpaRepository<Curso, Long> {

    @Query(value = "SELECT contratados_json, interessados_json, processo_seletivo_json  FROM Recrutador WHERE empresa_id = :idEmpresa", nativeQuery = true)
    fun findIdsTodosByEmpresa(idEmpresa: Long): List<Any>

    @Query(value = "SELECT contratados_json FROM Recrutador WHERE empresa_id = :idEmpresa", nativeQuery = true)
    fun findIdsContratadosByEmpresa(idEmpresa: Long): List<Any>

    @Query(value = "SELECT interessados_json FROM Recrutador WHERE empresa_id = :idEmpresa", nativeQuery = true)
    fun findIdsInteressadosByEmpresa(idEmpresa: Long): List<Any>

    @Query(value = "SELECT processo_seletivo_json FROM Recrutador WHERE empresa_id = :idEmpresa", nativeQuery = true)
    fun findIdsProcessoSeletivoByEmpresa(idEmpresa: Long): List<Any>

    @Query(value = "SELECT contratados_json, interessados_json, processo_seletivo_json FROM Recrutador", nativeQuery = true)
    fun findIdsTodos(): List<Any>

    @Query(value = "SELECT contratados_json FROM Recrutador", nativeQuery = true)
    fun findIdsContratados(): List<Any>

    @Query(value = "SELECT interessados_json FROM Recrutador", nativeQuery = true)
    fun findIdsInteressados(): List<Any>

    @Query(value = "SELECT processo_seletivo_json FROM Recrutador", nativeQuery = true)
    fun findIdsProcessoSeletivo(): List<Any>

    @Query(value = "SELECT id, contratados_json FROM Recrutador WHERE empresa_id = :idEmpresa", nativeQuery = true)
    fun findIdsRecrutadorAndContratadosByEmpresa(idEmpresa: Long): List<Any>

    @Query(value = "SELECT id, interessados_json FROM Recrutador WHERE empresa_id = :idEmpresa", nativeQuery = true)
    fun findIdsRecrutadorAndInteressadosByEmpresa(idEmpresa: Long): List<Any>

    @Query(value = "SELECT id, processo_seletivo_json FROM Recrutador WHERE empresa_id = :idEmpresa", nativeQuery = true)
    fun findIdsRecrutadorAndProcessoSeletivoByEmpresa(idEmpresa: Long): List<Any>

}