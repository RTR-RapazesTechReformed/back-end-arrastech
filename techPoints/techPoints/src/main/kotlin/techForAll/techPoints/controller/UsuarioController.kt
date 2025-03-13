package techForAll.techPoints.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import techForAll.techPoints.dtos.UsuarioInput
import techForAll.techPoints.service.UsuarioService

@RestController
@RequestMapping("/usuarios")
@Validated
class UsuarioController @Autowired constructor(
    private val usuarioService: UsuarioService
) {

    @Operation(summary = "Cadastrar um novo usuário", description = "Retorna os detalhes do usuário cadastrado")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            ApiResponse(responseCode = "400", description = "Email já cadastrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/cadastro")
    fun cadastrarUsuario(@RequestBody request: UsuarioInput): ResponseEntity<Any> {
        return try {
            val usuario = usuarioService.cadastrarUsuario(request)

            ResponseEntity.status(201).body(usuario)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to e.message))
        }
    }

    @Operation(summary = "Soft delete de um usuário", description = "Marca o usuário como deletado, sem remover do banco")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
            ApiResponse(responseCode = "400", description = "Credenciais inválidas"),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/deletar")
    fun softDelete(@RequestBody @Valid requestBody: Map<String, String>): ResponseEntity<Any> {
        val email = requestBody["email"]
        val senha = requestBody["senha"]

        return try {
            if (email != null && senha != null) {
                usuarioService.softDeleteUsuario(email, senha)
                ResponseEntity.status(200).build()
            } else {
                ResponseEntity.status(400).build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Usuário não encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Hard delete de um usuário", description = "Remove o usuário do banco de dados permanentemente")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário deletado com sucesso"),
            ApiResponse(responseCode = "400", description = "Credenciais inválidas"),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @DeleteMapping("/deletar")
    @Transactional
    fun hardDelete(@RequestBody @Valid requestBody: Map<String, String>): ResponseEntity<Any> {
        val email = requestBody["email"]
        val senha = requestBody["senha"]

        return try {
            if (email != null && senha != null) {
                usuarioService.hardDeleteUsuario(email, senha)
                ResponseEntity.status(200).build()
            } else {
                ResponseEntity.status(400).build()
            }
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Usuário não encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/listar")
    fun listar(@RequestParam(required = false) tipo: String?): ResponseEntity<Any> {
        return try {
            val usuarios = usuarioService.listarUsuarios(tipo)
            if (usuarios.isNotEmpty()) {
                ResponseEntity.status(200).body(usuarios)
            } else {
                ResponseEntity.status(204).build()
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Buscar usuário pelo ID", description = "Retorna o usuário correspondente ao ID fornecido")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/buscar/{idUsuario}")
    fun buscar(@PathVariable idUsuario: Long): ResponseEntity<Any> {
        return try {
            val usuarioSemSenha = usuarioService.buscarUsuarioPorId(idUsuario)
            ResponseEntity.status(200).body(usuarioSemSenha)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }


    @Operation(summary = "Login de usuário", description = "Autentica um usuário com email e senha")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário autenticado com sucesso"),
            ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody loginData: Map<String, String>): ResponseEntity<Any> {
        val email = loginData["email"]
        val senha = loginData["senha"]

        if (email.isNullOrBlank() || senha.isNullOrBlank()) {
            return ResponseEntity.status(400).body(mapOf("message" to "Email e senha são obrigatórios"))
        }

        return try {
            val usuario = usuarioService.loginUsuario(email, senha)

            ResponseEntity.status(200).body(usuario)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(401).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Logoff de usuário", description = "Desautentica um usuário pelo ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário desautenticado com sucesso"),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/logoff")
    fun logoff(@RequestParam idUsuario: Long): ResponseEntity<Any> {
        return try {
            usuarioService.logoffUsuario(idUsuario)
            ResponseEntity.status(200).build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Buscar usuário pelo e-mail", description = "Retorna o usuário correspondente ao e-mail fornecido")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/buscar/email/{email}")
    fun buscarPorEmail(@PathVariable email: String): ResponseEntity<Any> {
        return try {
            val usuarioSemSenha = usuarioService.buscarUsuarioPorEmail(email)
            ResponseEntity.status(200).body(usuarioSemSenha)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

    @Operation(summary = "Atualizar informações do usuário", description = "Atualiza as informações do usuário")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PatchMapping("/atualizar/{idUsuario}")
    fun patchUsuario(
        @PathVariable idUsuario: Long,
        @RequestBody atualizacao: Map<String, Any>
    ): ResponseEntity<Any> {
        return try {

            val usuarioAtualizado = usuarioService.atualizarUsuario(idUsuario, atualizacao)

            ResponseEntity.status(200).body(usuarioAtualizado)
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to "Usuário não encontrado"))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }


    @Operation(summary = "Atualizar a imagem de perfil de usuário", description = "Atualiza a imagem de perfil de um usuário pelo ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", description = "Imagem de perfil atualizada com sucesso"),
            ApiResponse(responseCode = "400", description = "Requisição inválida"),
            ApiResponse(responseCode = "404", description = "ID do usuário não encontrado"),
            ApiResponse(responseCode = "415", description = "Tipo de mídia não suportado"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PatchMapping("/atualizar-imagem/{idUsuario}", consumes = ["multipart/form-data"])
    fun atualizarImagemPerfil(
        @PathVariable idUsuario: Long,
        @RequestPart("imagemPerfil") novaFoto: MultipartFile
    ): ResponseEntity<Any> {
        return try {
            if (novaFoto.isEmpty) {
                return ResponseEntity.status(400).body(mapOf("message" to "Imagem não pode estar vazia"))
            }

            if (novaFoto.contentType == null || !novaFoto.contentType!!.startsWith("image/")) {
                return ResponseEntity.status(415).body(mapOf("message" to "Tipo de mídia não suportado"))
            }

            usuarioService.atualizarImagemUsuario(idUsuario, novaFoto.bytes)

            ResponseEntity.status(204).build()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            ResponseEntity.status(400).body(mapOf("message" to "Requisição inválida: ${e.message}"))
        } catch (e: NoSuchElementException) {
            e.printStackTrace()
            ResponseEntity.status(404).body(mapOf("message" to "ID do usuário não encontrado: ${e.message}"))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }


    @Operation(summary = "Obter imagem de perfil do usuário", description = "Retorna a imagem de perfil do usuário pelo ID")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Imagem de perfil encontrada e retornada com sucesso"),
            ApiResponse(responseCode = "204", description = "Usuário não encontrado ou imagem de perfil não encontrada"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @GetMapping("/imagem/{idUsuario}", produces = ["image/jpeg", "image/png", "image/jpg"])
    fun getImagemPerfil(@PathVariable idUsuario: Long): ResponseEntity<Any> {
        return try {
            val imagemPerfil = usuarioService.obterImagemPerfil(idUsuario)
            val byteArrayResource = ByteArrayResource(imagemPerfil)
            ResponseEntity.status(200)
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(imagemPerfil.size.toLong())
                .body(byteArrayResource)
        } catch (e: NoSuchElementException) {

            ResponseEntity.noContent().build()
        } catch (e: Exception) {

            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }


    @Operation(summary = "Reativar usuário", description = "Reativa o usuário correspondente ao e-mail e senha fornecidos")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Usuário reativado com sucesso"),
            ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            ApiResponse(responseCode = "400", description = "Usuário já está ativo"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    @PostMapping("/reativar")
    fun reativarUsuario(@RequestBody credenciais: Map<String, String>): ResponseEntity<Any> {
        val email = credenciais["email"]
        val senha = credenciais["senha"]

        return try {
            if (email != null && senha != null) {
                usuarioService.reativarUsuario(email, senha)
                ResponseEntity.status(200).body(mapOf("message" to "Usuário reativado com sucesso"))
            } else {
                ResponseEntity.status(400).body(mapOf("message" to "Dados de login inválidos"))
            }
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(mapOf("message" to e.message))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(400).body(mapOf("message" to e.message))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("message" to "Erro interno do servidor: ${e.message}"))
        }
    }

}
