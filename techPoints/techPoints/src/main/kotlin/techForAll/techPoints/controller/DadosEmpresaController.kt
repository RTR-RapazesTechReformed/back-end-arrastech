package techForAll.techPoints.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import techForAll.techPoints.dtos.EmpresaInput
import techForAll.techPoints.service.DadosEmpresaService

@RestController
@RequestMapping("/empresa")
@Validated
class DadosEmpresaController @Autowired constructor(
    private val service: DadosEmpresaService
) {

    @Operation(
        summary = "Cadastrar dados de uma nova empresa",
        description = "Retorna os detalhes da empresa cadastrada"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Empresa cadastrada com sucesso"),
            ApiResponse(responseCode = "400", description = "Erro de validação dos dados"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/cadastro")
    fun post(@RequestBody @Valid novaEmpresa: EmpresaInput): ResponseEntity<Any> {
        return try {
            val empresaCadastrada = service.cadastrarEmpresa(novaEmpresa)
            ResponseEntity.status(201).body(empresaCadastrada)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }

    }

    @Operation(summary = "Listar todas as empresas", description = "Retorna uma lista de todas as empresas cadastradas")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de empresas retornada com sucesso"),
            ApiResponse(responseCode = "204", description = "Nenhuma empresa encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/listar")
    fun listar(): ResponseEntity<Any> {
        return try {
            val empresas = service.listarEmpresas()
            if (empresas.isNotEmpty()) {
                ResponseEntity.status(200).body(empresas)
            } else {
                ResponseEntity.status(204).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(
        summary = "Obter dados de uma empresa",
        description = "Retorna os detalhes da empresa pelo ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/buscar/{idEmpresa}")
    fun buscar(@PathVariable idEmpresa: Long): ResponseEntity<Any> {
        return try {
            val empresaEncontrada = service.buscarEmpresaPorId(idEmpresa)
            ResponseEntity.status(200).body(empresaEncontrada)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }


    @Operation(
        summary = "Atualizar dados de uma empresa",
        description = "Atualiza e retorna os detalhes da empresa pelo ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Empresa atualizada com sucesso"),
            ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            ApiResponse(responseCode = "400", description = "Erro de validação dos dados"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PatchMapping("/atualizar/{idEmpresa}")
    fun patch(@PathVariable idEmpresa: Long, @RequestBody empresaAtualizada: Map<String, Any>): ResponseEntity<Any> {
        return try {
            val empresaAtualizada = service.atualizarEmpresa(idEmpresa, empresaAtualizada)
            ResponseEntity.status(200).body(empresaAtualizada)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Empresa não encontrada"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Deletar empresa", description = "Deleta uma empresa pelo ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Empresa deletada com sucesso"),
            ApiResponse(responseCode = "404", description = "Empresa não encontrada"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @DeleteMapping("/deletar/{idEmpresa}")
    fun deletarEmpresa(@PathVariable idEmpresa: Long): ResponseEntity<Any> {
        return try {
            service.deletarEmpresa(idEmpresa)
            ResponseEntity.status(204).build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Empresa não encontrada"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @GetMapping("/buscar-por-cnpj/{cnpj}")
    fun buscarPorCnpj(@PathVariable cnpj: String): ResponseEntity<Any> {
        return try {
            val empresa = service.buscarEmpresaPorCnpj(cnpj)
            ResponseEntity.status(200).body(empresa)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }
}

