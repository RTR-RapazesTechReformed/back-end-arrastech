package techForAll.techPoints.dtos

import techForAll.techPoints.domain.Endereco
import java.time.LocalDateTime

data class EmpresaComRecrutadoresDto(
    val id: Long,
    val nomeEmpresa: String,
    val cnpj: String,
    val setorIndustria: String,
    val telefoneContato: String,
    val emailCorporativo: String,
    val endereco: Endereco,
    val dataCriacao: LocalDateTime,
    val recrutadores: List<String>
)