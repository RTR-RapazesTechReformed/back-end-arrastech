package techForAll.techPoints.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.Period

@Entity
@Table(name = "aluno")
class Aluno(

    @Column(nullable = false)
    var escolaridade: String,

    @Column(nullable = true)
    var sexo: String?,

    @Column(nullable = true)
    var etnia: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id")
    var endereco: Endereco,

    @Column(nullable = true)
    var descricao: String? = null,

    @Column(nullable = false)
    var dtNasc: LocalDate,

    @OneToMany(mappedBy = "aluno", cascade = [CascadeType.ALL], orphanRemoval = true)
    var pontuacoes: MutableList<Pontuacao> = mutableListOf(),

    @OneToMany(mappedBy = "aluno", cascade = [CascadeType.ALL], orphanRemoval = true)
    var cursos: MutableList<Curso> = mutableListOf(),

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
    tipoUsuario = 1,
    autenticado = autenticado
) {
    override fun criarUsuario(endereco: Endereco?, empresa: Empresa?): Usuario {
        if (endereco == null) throw IllegalArgumentException("Endereço é obrigatório para Aluno")
        return Aluno(
            escolaridade = this.escolaridade,
            sexo = this.sexo,
            etnia = this.etnia,
            endereco = endereco,
            descricao = this.descricao,
            dtNasc = this.dtNasc,
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

    fun calcularIdade(): Int {
        return Period.between(this.dtNasc, LocalDate.now()).years
    }
}


