package techForAll.techPoints.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Endereco(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var cep: String,

    @Column(nullable = false)
    var rua: String,

    @Column(nullable = false)
    var numero: String,

    @Column(nullable = false)
    var cidade: String,

    @Column(nullable = false)
    var estado: String,

    @Column(nullable = false)
    var dataCriacao: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var dataAtualizacao: LocalDateTime? = LocalDateTime.now(),

)


