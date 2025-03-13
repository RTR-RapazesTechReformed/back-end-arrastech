package techForAll.techPoints.domain

import jakarta.persistence.*


    @Entity
    class TempoEstudo(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long,

        @Column(name = "nome_dia",nullable = false)
        var nomeDia: String,

        @Column(name = "data",nullable = false)
        var data: String,

        @Column(name = "qtd_tempo_estudo", nullable = false)
        var qtdTempoEstudo: String,

        @Column(name = "ativado",nullable = false)
        var ativado: Boolean,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "meta_estudo_semana_id")
        var metaEstudoSemana: MetaEstudoSemana
    )