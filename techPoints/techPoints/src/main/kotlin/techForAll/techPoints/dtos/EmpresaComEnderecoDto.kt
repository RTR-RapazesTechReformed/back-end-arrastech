package techForAll.techPoints.dtos

import techForAll.techPoints.domain.Endereco

data class EmpresaComEnderecoDto(
    val id: Long,
    val nomeEmpresa: String,
    val cnpj: String,
    val setorIndustria: String,
    val telefoneContato: String,
    val emailCorporativo: String,
    val endereco: Endereco
)
