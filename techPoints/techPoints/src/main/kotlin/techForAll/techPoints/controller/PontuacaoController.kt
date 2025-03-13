package techForAll.techPoints.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import techForAll.techPoints.dtos.PontuacaoComPontosDTO
import techForAll.techPoints.service.PontuacaoService
import java.time.LocalDate
import java.time.YearMonth

@RestController
@RequestMapping("/pontuacoes")
class PontuacaoController @Autowired constructor(
    private val service: PontuacaoService
) {

    @Operation(
        summary = "Recuperar pontos agrupados por curso",
        description = "Retorna as pontuações agrupadas por curso de um aluno, podendo filtrar por um intervalo de datas."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Pontuações retornadas com sucesso."),
            ApiResponse(responseCode = "400", description = "Erro no processamento dos dados de entrada."),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        ]
    )
    @GetMapping("/{idAluno}")
    fun recuperarPontosAtividadeAgrupadoCurso(
        @PathVariable
        @Parameter(description = "ID do aluno") idAluno: Long,
        @RequestParam(required = false) dataInicio: String?,
        @RequestParam(required = false) dataFim: String?
    ): Map<Long, List<PontuacaoComPontosDTO>> {
        val dataInicioParsed = dataInicio?.let { LocalDate.parse(it) }
        val dataFimParsed = dataFim?.let { LocalDate.parse(it) }
        return service.recuperarTodosCursosAlunoPontuacao(idAluno, dataInicioParsed, dataFimParsed)
    }

    @Operation(
        summary = "Recuperar KPI semanal",
        description = "Retorna as métricas de desempenho do aluno na semana atual e anterior."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "KPIs retornados com sucesso."),
            ApiResponse(responseCode = "404", description = "Aluno não encontrado."),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        ]
    )
    @GetMapping("/kpi-semana/{idAluno}")
    fun recuperarKPISemana(
        @PathVariable
        @Parameter(description = "ID do aluno") idAluno: Long
    ): Map<String, Map<Long, Int>> {
        return service.recuperarKPISemanaPassadaAndAtual(idAluno)
    }

    @Operation(
        summary = "Recuperar KPI de entregas",
        description = "Retorna as métricas de entregas realizadas pelo aluno em um intervalo de datas."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "KPIs retornados com sucesso."),
            ApiResponse(responseCode = "404", description = "Aluno não encontrado."),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        ]
    )
    @GetMapping("/kpi-entregas/{idAluno}")
    fun recuperarKPIEntrega(
        @PathVariable
        @Parameter(description = "ID do aluno") idAluno: Long,
        @RequestParam(required = false) dataInicio: String?,
        @RequestParam(required = false) dataFim: String?
    ): Map<String, Int> {
        return service.recuperarKPIEntregas(idAluno, dataInicio, dataFim)
    }

    @Operation(
        summary = "Recuperar pontuação mensal",
        description = "Retorna a pontuação acumulada por mês agrupada por cursos do aluno."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Pontuações retornadas com sucesso."),
            ApiResponse(responseCode = "404", description = "Aluno não encontrado."),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        ]
    )
    @GetMapping("/pontos-mes/{idAluno}")
    fun recuperarPontosPorMes(
        @PathVariable
        @Parameter(description = "ID do aluno") idAluno: Long
    ): Map<Pair<Long, String>, Map<YearMonth, Int>> {
        return service.recuperarPontosConquistadosPorMes(idAluno)
    }

    @Operation(
        summary = "Recuperar listas do aluno",
        description = "Retorna as listas associadas a um aluno pelo ID."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Listas recuperadas com sucesso."),
            ApiResponse(responseCode = "204", description = "Nenhuma lista encontrada."),
            ApiResponse(responseCode = "400", description = "ID inválido."),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        ]
    )
    @GetMapping("/lista")
    fun recuperarAlunoLista(
        @RequestParam
        @Parameter(description = "ID do aluno") idAluno: Long
    ): ResponseEntity<Any> {
        return try {
            if (idAluno <= 0) {
                ResponseEntity.status(400).body(mapOf("message" to "ID do aluno inválido"))
            } else {
                val listas = service.buscarListasComAluno(idAluno)
                if (listas.isEmpty()) {
                    ResponseEntity.status(204).build()
                } else {
                    ResponseEntity.status(200).body(listas)
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }
}
