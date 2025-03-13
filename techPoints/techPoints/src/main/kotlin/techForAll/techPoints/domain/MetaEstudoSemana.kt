package techForAll.techPoints.domain

import jakarta.persistence.*


@Entity
class MetaEstudoSemana(
    @Id
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    var aluno: Aluno,

    @OneToMany(mappedBy = "metaEstudoSemana", cascade = [CascadeType.ALL], orphanRemoval = true)
    var diasAtivos: List<TempoEstudo>?,

    @Column(nullable = false)
    var horasTotal: Double?,

    @OneToMany(mappedBy = "metaEstudoSemana", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tempoSessao: List<TempoSessao>?
)