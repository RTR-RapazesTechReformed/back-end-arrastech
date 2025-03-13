package techForAll.techPoints.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import techForAll.techPoints.domain.MetaEstudoSemana
import techForAll.techPoints.repository.MetaEstudoSemanaRepository
import techForAll.techPoints.service.MetaDeEstudoService


@RestController
@RequestMapping("/meta-de-estudo")
@Validated
class MetaDeEstudoController @Autowired constructor(
    private val metaService: MetaDeEstudoService,
    private val metaEstudoSemana: MetaEstudoSemanaRepository
) {

    @Operation(summary = "Cadastrar uma nova meta de estudo", description = "Retorna os detalhes da meta de estudo cadastrada")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Meta de estudo cadastrada com sucesso"),
            ApiResponse(responseCode = "400", description = "Meta de estudo já cadastrada para este aluno"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/cadastro")
    fun cadastrarMetaDiaria(
        @RequestParam metaEstudoSemanaId: Long,
        @RequestParam nomeDia: String,
        @RequestParam data: String,
        @RequestParam qtdTempoEstudo: String,
        @RequestParam ativado: Boolean): ResponseEntity<Any> {
        return try {
            val novaMetaDiaria  = metaService.cadastrarMetaDiaria(
                metaEstudoSemanaId,
                nomeDia,
                data,
                qtdTempoEstudo,
                ativado
            )
            ResponseEntity.status(201).body(novaMetaDiaria)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to e.message))
        }
    }

    @Operation(summary = "Obter meta de estudo semanal", description = "Retorna todos os dados da meta de estudo semanal, incluindo a soma das horas totais")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Meta de estudo retornada com sucesso"),
            ApiResponse(responseCode = "404", description = "Meta de estudo não encontrada")
        ]
    )
    @GetMapping("/{metaEstudoSemanaId}")
    fun obterMetaEstudoSemana(@PathVariable metaEstudoSemanaId: Long): ResponseEntity<Any> {
        return try {
            val metaEstudoSemana = metaService.obterMetaEstudoSemana(metaEstudoSemanaId)
            ResponseEntity.status(200).body(metaEstudoSemana)
        } catch (e: Exception) {
            ResponseEntity.status(404).body(null)
        }
    }
}