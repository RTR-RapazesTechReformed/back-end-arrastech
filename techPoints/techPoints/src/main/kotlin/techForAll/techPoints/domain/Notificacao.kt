package techForAll.techPoints.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notificacoes")
data class Notificacao(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "id_aluno", nullable = false)
    var aluno: Aluno,

    @ManyToOne
    @JoinColumn(name = "id_recrutador", nullable = false)
    var recrutador: Recrutador,

    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    var empresa: Empresa,

    @Column(nullable = false)
    var data: LocalDateTime = LocalDateTime.now(),

    @Column
    var lista: String? = null,

    @Column(nullable = false)
    var status: Boolean = false
)