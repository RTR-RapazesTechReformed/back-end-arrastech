package techForAll.techPoints.controller

import techForAll.techPoints.domain.Endereco
import techForAll.techPoints.service.EnderecoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/enderecos")
@Validated
class EnderecoController @Autowired constructor(
    private val service: EnderecoService
) {

    @Operation(summary = "Cadastrar um novo endereço", description = "Retorna os detalhes do endereço cadastrado")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Endereço cadastrado com sucesso"),
            ApiResponse(responseCode = "400", description = "Requisição inválida"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/cadastro")
    fun post(@RequestBody @Valid novoEndereco: Endereco): ResponseEntity<Any> {
        return try {
            val enderecoCadastrado = service.cadastrarEndereco(novoEndereco)
            ResponseEntity.status(201).body(enderecoCadastrado)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Listar todos os endereços", description = "Retorna uma lista de todos os endereços cadastrados")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de endereços retornada com sucesso"),
            ApiResponse(responseCode = "204", description = "Nenhum endereço encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/listar")
    fun listar(): ResponseEntity<Any> {
        return try {
            val enderecos = service.listarEnderecos()
            if (enderecos.isNotEmpty()) {
                ResponseEntity.status(200).body(enderecos)
            } else {
                ResponseEntity.status(204).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Buscar endereço pelo ID", description = "Retorna o endereço correspondente ao ID fornecido")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Endereço encontrado com sucesso"),
            ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/buscar/{idEndereco}")
    fun buscar(@PathVariable idEndereco: Long): ResponseEntity<Any> {
        return try {
            val enderecoEncontrado = service.buscarEnderecoPorId(idEndereco)
            ResponseEntity.status(200).body(enderecoEncontrado)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Endereço não encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Atualizar endereço pelo ID", description = "Atualiza e retorna o endereço correspondente ao ID fornecido")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PatchMapping("/atualizar/{idEndereco}")
    fun patch(@PathVariable idEndereco: Long, @RequestBody enderecoAtualizado: Map<String, Any>): ResponseEntity<Any> {
        return try {
            val endereco = service.atualizarEndereco(idEndereco, enderecoAtualizado)
            ResponseEntity.status(200).body(endereco)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Endereço não encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Deletar endereço", description = "Deleta um endereço pelo ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso"),
            ApiResponse(responseCode = "404", description = "Endereço não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @DeleteMapping("/deletar/{idEndereco}")
    fun deletarEndereco(@PathVariable idEndereco: Long): ResponseEntity<Any> {
        return try {
            service.deletarEndereco(idEndereco)
            ResponseEntity.status(204).build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Endereço não encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }
}
