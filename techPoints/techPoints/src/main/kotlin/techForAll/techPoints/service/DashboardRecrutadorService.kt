package techForAll.techPoints.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import techForAll.techPoints.domain.*
import techForAll.techPoints.repository.AlunoRepository
import techForAll.techPoints.repository.CursoMoodleRepository
import techForAll.techPoints.repository.RecrutadorRepository

@Service
class DashboardRecrutadorService @Autowired constructor(
    private val recrutadorRepository: RecrutadorRepository,
    private val alunoRepository: AlunoRepository,
    private val cursoRepository: CursoMoodleRepository,
    private val notificacaoService: NotificacaoService
) {

    fun adicionarAluno(idRecrutador: Long, idAluno: Long, tipoLista: String) {
        val recrutador = recrutadorRepository.findById(idRecrutador)
            .orElseThrow { NoSuchElementException("Recrutador não encontrado") }

        val aluno = alunoRepository.findById(idAluno)
            .orElseThrow { NoSuchElementException("Aluno com ID $idAluno não encontrado") }

        moverParaLista(idAluno, recrutador, tipoLista)
        recrutadorRepository.save(recrutador)

        if (tipoLista != "favoritos" && tipoLista != "cancelados") {
            notificacaoService.criarNotificacao(aluno, recrutador, recrutador.empresa.id, tipoLista)
        }

    }

    fun moverParaLista(idAluno: Long, recrutador: Recrutador, novaLista: String) {

        recrutador.favoritos = recrutador.favoritos.filter { it != idAluno }
        recrutador.interessados = recrutador.interessados.filter { it != idAluno }
        recrutador.processoSeletivo = recrutador.processoSeletivo.filter { it != idAluno }
        recrutador.contratados = recrutador.contratados.filter { it != idAluno }
        recrutador.cancelados = recrutador.cancelados.filter { it != idAluno }

        when (novaLista) {
            "favoritos" -> recrutador.favoritos = recrutador.favoritos + idAluno
                "interessados" -> recrutador.interessados = recrutador.interessados + idAluno
            "processoSeletivo" -> recrutador.processoSeletivo = recrutador.processoSeletivo + idAluno
            "contratados" -> recrutador.contratados = recrutador.contratados + idAluno
            "cancelados" -> recrutador.cancelados = recrutador.cancelados + idAluno
            else -> throw IllegalArgumentException("Lista inválida")
        }
    }

     fun removerAluno(idRecrutador: Long, idAluno: Long, tipoLista: String) {
        val recrutador = recrutadorRepository.findById(idRecrutador)
            .orElseThrow { NoSuchElementException("Recrutador não encontrado") }

        when (tipoLista) {
            "favoritos" -> recrutador.favoritos = recrutador.favoritos.filter { it != idAluno }
            "interessados" -> recrutador.interessados = recrutador.interessados.filter { it != idAluno }
            "processoSeletivo" -> recrutador.processoSeletivo = recrutador.processoSeletivo.filter { it != idAluno }
            "contratados" -> recrutador.contratados = recrutador.contratados.filter { it != idAluno }
            "cancelados" -> recrutador.cancelados = recrutador.cancelados.filter { it != idAluno }
            else -> throw IllegalArgumentException("Lista inválida")
        }

        recrutadorRepository.save(recrutador)
    }

    fun listarAlunos(idRecrutador: Long, tipoLista: String): List<Map<String, Any?>> {
        val recrutador = recrutadorRepository.findById(idRecrutador)
            .orElseThrow { NoSuchElementException("Recrutador não encontrado") }

        val ids = when (tipoLista) {
            "favoritos" -> recrutador.favoritos
            "interessados" -> recrutador.interessados
            "processoSeletivo" -> recrutador.processoSeletivo
            "contratados" -> recrutador.contratados
            "cancelados" -> recrutador.cancelados
            else -> throw IllegalArgumentException("Lista inválida")
        }

        return listarAlunosIds(ids)
    }

    fun listarTodosOsCursos(): List<CursoMoodle> {
        return cursoRepository.findAll()
    }


    fun listarCursosPorCategoria(categoria: String): List<CursoMoodle> {
        return cursoRepository.findAll().filter { curso ->
            curso.categorias?.contains(categoria, ignoreCase = true) == true
        }
    }


    private fun listarAlunosIds(ids: List<Long>): List<Map<String, Any?>> {
        return ids.mapNotNull { alunoId ->
            alunoRepository.findById(alunoId).map { aluno ->
                mapAluno(aluno)
            }.orElse(null)
        }
    }

    private fun mapAluno(aluno: Aluno): Map<String, Any?> {
        return mapOf(
            "id" to aluno.id,
            "nomeUsuario" to aluno.nomeUsuario,
            "primeiroNome" to aluno.primeiroNome,
            "sobrenome" to aluno.sobrenome,
            "email" to aluno.email,
            "telefone" to aluno.telefone,
            "escolaridade" to aluno.escolaridade,
            "descricao" to aluno.descricao,
            "dtNasc" to aluno.dtNasc,
            "endereco" to mapOf(
                "cidade" to aluno.endereco.cidade,
                "estado" to aluno.endereco.estado
            ),
        )
    }
}