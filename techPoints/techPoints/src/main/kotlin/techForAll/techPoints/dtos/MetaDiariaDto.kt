package techForAll.techPoints.dtos


data class MetaDiariaDto(

    var id: Long,

    var nomeDia: String,

    var data: String,

    var qtdTempoEstudo: String,

    var ativado: Boolean,

    var metaEstudoSemana: Long
)
