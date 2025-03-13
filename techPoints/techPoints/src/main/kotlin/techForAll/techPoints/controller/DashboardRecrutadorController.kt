package techForAll.techPoints.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import techForAll.techPoints.service.DashboardRecrutadorService

@RestController
@RequestMapping("/dashboardRecrutador")
class DashboardRecrutadorController @Autowired constructor(
    private val recrutadorService: DashboardRecrutadorService
) {

    @Operation(summary = "Adicionar aluno a uma lista", description = "Adiciona um aluno a uma lista específica de um recrutador")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Aluno adicionado à lista com sucesso"),
            ApiResponse(responseCode = "404", description = "Recrutador ou Aluno não encontrado"),
            ApiResponse(responseCode = "409", description = "Aluno já está na lista"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/{idRecrutador}/{tipoLista}/{idAluno}")
    fun adicionarAluno(
        @PathVariable idRecrutador: Long,
        @PathVariable idAluno: Long,
        @PathVariable tipoLista: String
    ): ResponseEntity<Any> {
        return try {
            recrutadorService.adicionarAluno(idRecrutador, idAluno, tipoLista)
            ResponseEntity.status(200).build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Recrutador ou Aluno não encontrado"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(409).body(mapOf("message" to "Aluno já está na lista de $tipoLista"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor ${e.message}"))
        }
    }

    @Operation(summary = "Remover aluno de uma lista", description = "Remove um aluno de uma lista específica de um recrutador")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Aluno removido da lista com sucesso"),
            ApiResponse(responseCode = "404", description = "Recrutador ou Aluno não encontrado"),
            ApiResponse(responseCode = "409", description = "Aluno não está na lista"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @DeleteMapping("/{idRecrutador}/{tipoLista}/{idAluno}")
    fun removerAluno(
        @PathVariable idRecrutador: Long,
        @PathVariable idAluno: Long,
        @PathVariable tipoLista: String
    ): ResponseEntity<Any> {
        return try {
            recrutadorService.removerAluno(idRecrutador, idAluno, tipoLista)
            ResponseEntity.status(200).build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Recrutador ou Aluno não encontrado"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(409).body(mapOf("message" to "Aluno não está na lista de $tipoLista"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor ${e.message}"))
        }
    }

    @Operation(summary = "Listar alunos de uma lista", description = "Lista todos os alunos de uma lista específica de um recrutador")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso"),
            ApiResponse(responseCode = "404", description = "Recrutador não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/{idRecrutador}/listar/{tipoLista}")
    fun listarAlunos(
        @PathVariable idRecrutador: Long,
        @PathVariable tipoLista: String
    ): ResponseEntity<Any> {
        return try {
            val alunos = recrutadorService.listarAlunos(idRecrutador, tipoLista)
            ResponseEntity.ok(alunos)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Recrutador não encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(
        summary = "Listar cursos cadastrados",
        description = "Lista todos os cursos ou filtra pela categoria especificada"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de cursos retornada com sucesso"),
            ApiResponse(responseCode = "204", description = "Nenhum curso encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/listar")
    fun listarCursos(
        @RequestParam(required = false) categoria: String?
    ): ResponseEntity<Any> {
        return try {
            val cursos = if (categoria.isNullOrEmpty()) {
                recrutadorService.listarTodosOsCursos()
            } else {
                recrutadorService.listarCursosPorCategoria(categoria)
            }

            if (cursos.isEmpty()) {
                ResponseEntity.status(204).body(mapOf("message" to "Nenhum curso encontrado"))
            } else {
                ResponseEntity.status(200).body(cursos)
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }
}