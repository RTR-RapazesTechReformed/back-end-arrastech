package techForAll.techPoints.domain

import jakarta.persistence.*

@Entity
@Table(name = "curso_moodle")
class CursoMoodle(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false)
    var nome: String,

    @Column(nullable = true)
    var categorias: String
)

