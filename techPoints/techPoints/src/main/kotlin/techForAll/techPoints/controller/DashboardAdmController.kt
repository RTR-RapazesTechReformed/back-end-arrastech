package techForAll.techPoints.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import techForAll.techPoints.dtos.CursoAlunosDto
import techForAll.techPoints.service.DashboardAdmService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/dashboardAdm")
class DashboardAdmController(private val dashAdmService: DashboardAdmService){

    @Operation(summary = "Obter quantidade de alunos por curso", description = "Retorna a quantidade de alunos por curso")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Quantidade de alunos por curso retornada com sucesso"),
            ApiResponse(responseCode = "204", description = "Nenhum dado encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/alunos-por-curso")
    fun getAlunosPorCurso(): ResponseEntity<List<CursoAlunosDto>> {
        return try {
            val alunosPorCurso = dashAdmService.getAlunosPorCurso()
            if (alunosPorCurso.isEmpty()) {
                ResponseEntity.status(204).build()
            } else {
                ResponseEntity.status(200).body(alunosPorCurso)
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(null)
        }
    }

    @Operation(summary = "Obter demografia dos alunos", description = "Retorna a demografia dos alunos em diferentes listas")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Demografia dos alunos retornada com sucesso"),
            ApiResponse(responseCode = "204", description = "Nenhum dado encontrado"),
            ApiResponse(responseCode = "400", description = "Tipo de lista inválido"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/demografia-alunos")
    fun getDemografiaAlunos(@RequestParam tipoLista: String, @RequestParam(required = false) idEmpresa: Long?): ResponseEntity<Any> {
        return try {
            val demografia = dashAdmService.getDemografiaPorTipoLista(tipoLista, idEmpresa)
            ResponseEntity.status(200).body(demografia)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to "Tipo de lista inválido"))

        } catch (e: NoSuchElementException) {
            ResponseEntity.status(204).body(mapOf("message" to "Nenhum dado encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }
    @Operation(summary = "Gerar relatório CSV de demografia das listas", description = "Retorna um arquivo CSV com a demografia dos alunos inseridos nas listas dos recrutadores")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Relatório CSV gerado com sucesso"),
            ApiResponse(responseCode = "400", description = "Tipo de lista inválido"),
            ApiResponse(responseCode = "204", description = "Nenhum dado encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/relatorio-demografia-listas")
    fun gerarRelatorioDemografiaListas(@RequestParam tipoLista: String, @RequestParam(required = false) idEmpresa: Long?): ResponseEntity<ByteArray> {
        return try {
            val csv = dashAdmService.gerarRelatorioDemografiaEmpresas(tipoLista, idEmpresa)
            val arquivoCsv = csv.toByteArray(Charsets.UTF_8)
            val dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=\"relatorio_demografia_listas_$dataAtual.csv\"")
                .body(arquivoCsv)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body("Tipo de lista inválido".toByteArray(Charsets.UTF_8))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(204).body("Nenhum dado encontrado".toByteArray(Charsets.UTF_8))
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Erro interno do servidor: ${e.message}".toByteArray(Charsets.UTF_8))
        }
    }
    @Operation(summary = "Gerar relatório CSV/TXT de demografia dos alunos", description = "Retorna um arquivo CSV/TXT com a demografia" +
            " dos alunos cadastrados no site")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            ApiResponse(responseCode = "400", description = "Tipo de lista inválido"),
            ApiResponse(responseCode = "204", description = "Nenhum dado encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/relatorio-alunos")
    fun gerarRelatorioAlunosFiltrados(
        @RequestParam(required = false) sexo: String?,
        @RequestParam(required = false) etnia: String?,
        @RequestParam(required = false) idadeMaxima: Int?,
        @RequestParam(required = false) cidade: String?,
        @RequestParam(required = false) escolaridade: String?,
        @RequestParam(required = true) arquivo: String
    ): ResponseEntity<ByteArray> {
        return try {
            val dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            when (arquivo.lowercase()) {
                "csv" -> {
                    val csv = dashAdmService.gerarRelatorioDemografiaAlunos(sexo, etnia, idadeMaxima, cidade, escolaridade, arquivo)
                    val arquivoCsv = csv.toByteArray(Charsets.UTF_8)

                    ResponseEntity.status(200)
                        .header("Content-Type", "text/csv; charset=UTF-8")
                        .header("Content-Disposition", "attachment; filename=\"relatorio_alunos_$dataAtual.csv\"")
                        .body(arquivoCsv)
                }
                "txt" -> {
                    val txt = dashAdmService.gerarRelatorioDemografiaAlunos(sexo, etnia, idadeMaxima, cidade, escolaridade, arquivo)
                    val arquivoTxt = txt.toByteArray(Charsets.UTF_8)

                    ResponseEntity.status(200)
                        .header("Content-Type", "text/plain; charset=UTF-8")
                        .header("Content-Disposition", "attachment; filename=\"relatorio_alunos_$dataAtual.txt\"")
                        .body(arquivoTxt)
                }
                else -> {
                    ResponseEntity.status(400)
                        .body("Formato de arquivo inválido. Use 'csv' ou 'txt'.".toByteArray(Charsets.UTF_8))
                }
            }
        } catch (e: Exception) {
            ResponseEntity.status(500)
                .body("Erro interno do servidor: ${e.message}".toByteArray(Charsets.UTF_8))
        }
    }


    @Operation(
        summary = "Cadastrar alunos em lote via CSV/TXT",
        description = "Realiza o cadastro de vários alunos a partir de um arquivo CSV ou TXT"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Alunos cadastrados com sucesso"),
            ApiResponse(responseCode = "400", description = "Erro no formato do arquivo ou dados inválidos"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/cadastrar-alunos-lote")
    fun cadastrarAlunosLote(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        return try {

            val resultado = when {
                file.originalFilename!!.endsWith(".csv") -> dashAdmService.processarArquivoCsv(file)
                file.originalFilename!!.endsWith(".txt") -> dashAdmService.processarArquivoTxt(file)
                else -> throw IllegalArgumentException("Tipo de arquivo não suportado. Apenas CSV e TXT são permitidos.")
            }
            
            ResponseEntity.status(201).body(resultado)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

}
