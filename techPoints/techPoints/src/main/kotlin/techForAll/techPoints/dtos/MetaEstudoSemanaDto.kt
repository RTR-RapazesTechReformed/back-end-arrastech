package techForAll.techPoints.dtos

data class MetaEstudoSemanaDto(
    var id: Long?,
    var alunoId: Long,
    var diasAtivos: List<MetaDiariaDto>?,
    var sessoes: List<SessoesDto>?,
    var horasTotal: Double?
)