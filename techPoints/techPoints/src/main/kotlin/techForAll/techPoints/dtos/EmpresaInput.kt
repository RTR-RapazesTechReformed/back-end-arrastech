package techForAll.techPoints.dtos


data class EmpresaInput (
    val nomeEmpresa: String,
    val cnpj: String,
    val setorIndustria: String,
    val telefoneContato: String,
    val emailCorporativo: String,
    val enderecoId: Long,
)