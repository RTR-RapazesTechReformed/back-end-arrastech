package techForAll.techPoints.dtos

import java.time.LocalDate

data class UsuarioInput (
    val nomeUsuario: String,
    val cpf: String,
    val senha: String,
    val primeiroNome: String,
    val sobrenome: String,
    val email: String,
    val telefone: String,
    val imagemPerfil: ByteArray? = null,
    val tipoUsuario: Int,
    val autenticado: Boolean,
    val enderecoId: Long?,
    val cnpj: String?,
    val dtNasc: LocalDate?,
    val escolaridade: String?,
    val sexo: String?,
    val etnia: String?,
    val cargoUsuario: String?,
    val descricao: String?,
    val nivelAcesso: Int?
)