package techForAll.techPoints.controller

import techForAll.techPoints.repository.UsuarioRepository
import techForAll.techPoints.service.ResetSenhaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reset-senha")
class ResetSenhaController @Autowired constructor(
    private val resetService: ResetSenhaService,
    private val usuarioRepository: UsuarioRepository
) {

    @Operation(
        summary = "Solicitar troca de senha",
        description = "Endpoint para solicitar troca de senha."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Troca de senha solicitada com sucesso."),
            ApiResponse(responseCode = "400", description = "Erro ao solicitar troca de senha."),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/solicitar-troca")
    fun solicitarTrocaSenha(@RequestBody request: Map<String, String>): ResponseEntity<Any> {
        return try {
            val emailUser = request["email"]

            if (emailUser.isNullOrBlank()) {
                ResponseEntity.badRequest().body(mapOf("message" to "Email é obrigatório"))
            } else if (usuarioRepository.existsByEmail(emailUser)) {
                resetService.senhaReset(emailUser)
            } else {
                ResponseEntity.status(404).body(mapOf("message" to "Usuário não encontrado"))
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(
        summary = "Atualizar senha do usuário",
        description = "Endpoint para atualizar a senha do usuário."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Senha do usuário atualizada com sucesso."),
            ApiResponse(responseCode = "400", description = "Erro ao atualizar a senha do usuário."),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PatchMapping("/nova-senha")
    fun atualizarSenha(@RequestBody request: Map<String, String>): ResponseEntity<Any> {
        val emailUser = request["email"]
        val novaSenha = request["novaSenha"]
        val token = request["token"]

        if (emailUser.isNullOrBlank() || novaSenha.isNullOrBlank() || token.isNullOrBlank()) {
            return ResponseEntity.badRequest().body(mapOf("message" to "Dados de solicitação de reset inválidos"))
        }

        return try {
            resetService.atualizarSenha(emailUser, novaSenha, token)
            ResponseEntity.status(200).body(mapOf("message" to "Senha atualizada com sucesso"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(
        summary = "Verificar token de redefinição de senha",
        description = "Endpoint para verificar se o token de redefinição de senha é válido."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Token válido."),
            ApiResponse(responseCode = "400", description = "Token ou e-mail inválido."),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor.")
        ]
    )
    @PostMapping("/verificar-token")
    fun verificarToken(@RequestBody request: Map<String, String>): ResponseEntity<Any> {
        val codigoRedefinicao = request["codigoRedefinicao"]
        val emailUser = request["emailUser"]

        if (codigoRedefinicao.isNullOrBlank() || emailUser.isNullOrBlank()) {
            return ResponseEntity.status(400).body(mapOf("message" to "Dados de verificação inválidos"))
        }

        return try {
            resetService.verificarToken(codigoRedefinicao, emailUser)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

}

