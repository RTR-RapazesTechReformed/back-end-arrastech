package techForAll.techPoints.domain

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "administrador")
class Administrador(
    @Column(nullable = false)
    var cargo: String,

    @Column(nullable = true)
    var ultimoAcesso: LocalDateTime? = null,

    @Column(nullable = true)
    var descricao: String? = null,

    @Column(nullable = true)
    var nivelAcesso: Int,

    nomeUsuario: String,
    cpf: String,
    senha: String,
    primeiroNome: String,
    sobrenome: String,
    email: String,
    telefone: String,
    imagemPerfil: ByteArray?,
    autenticado: Boolean
) : Usuario(
    nomeUsuario = nomeUsuario,
    cpf = cpf,
    senha = senha,
    primeiroNome = primeiroNome,
    sobrenome = sobrenome,
    email = email,
    telefone = telefone,
    imagemPerfil = imagemPerfil,
    tipoUsuario = 3,
    autenticado = autenticado
) {
    override fun criarUsuario(endereco: Endereco?, empresa: Empresa?): Usuario {
        return Administrador(
            cargo = this.cargo,
            ultimoAcesso = this.ultimoAcesso,
            descricao = this.descricao,
            nivelAcesso = this.nivelAcesso,
            nomeUsuario = this.nomeUsuario,
            cpf = this.cpf,
            senha = this.senha,
            primeiroNome = this.primeiroNome,
            sobrenome = this.sobrenome,
            email = this.email,
            telefone = this.telefone,
            imagemPerfil = this.imagemPerfil,
            autenticado = this.autenticado
        )
    }
}
