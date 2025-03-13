package techForAll.techPoints.dtos

data class TempoSessaoMoodleDto(
    val alunoId: Long,
    val alunoEmail: String,
    val diaSessao: String,
    val qtdTempoSessao: Double
)