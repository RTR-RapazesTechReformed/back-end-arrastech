package techForAll.techPoints.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Empresa(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var nomeEmpresa: String,

    @Column(nullable = false, unique = true)
    var cnpj: String,

    @Column(nullable = false)
    var setorIndustria: String,

    @Column(nullable = false)
    var telefoneContato: String,

    @Column(nullable = false, unique = true)
    var emailCorporativo: String,

    @Column(nullable = true)
    var senhaRepresante: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id")
    var endereco: Endereco,

    @Column(nullable = false)
    var dataCriacao: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var dataDeletado: LocalDateTime ? = null,

    @Column(nullable = true)
    var dataAtualizacao: LocalDateTime? = null,

    @Column(nullable = true)
    var representanteLegal: String? = null,

    @Column(nullable = true)
    var sobrenomeRepresentante: String? = null,

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    var recrutadores: List<Recrutador> = listOf()
)