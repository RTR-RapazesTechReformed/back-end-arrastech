package techForAll.techPoints.domain

import jakarta.persistence.*


@Entity
@Table(name = "curso_aluno")
class  Curso(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false)
    var curso_id_moodle: Long,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = false)
    var aluno_email: String,

    @Column(nullable = false)
    var totalAtividades: Int,

    @Column(nullable = false)
    var totalAtividadesDoAluno: Int,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "aluno_id")
    var aluno: Aluno
)

