package techForAll.techPoints.domain

import jakarta.persistence.*


@Entity
class TempoSessao(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false)
    var diaSessao: String,

    @Column(nullable = false)
    var qtdTempoSessao: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    var aluno: Aluno,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meta_estudo_semana_id")
    var metaEstudoSemana: MetaEstudoSemana
)