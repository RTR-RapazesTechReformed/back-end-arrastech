package techForAll.techPoints.dtos

import techForAll.techPoints.domain.Recrutador
import java.time.LocalDateTime

data class NotificacaoDTO(
    val id: Long,
    val alunoNome: String,
    val alunoId: Long,
    val status: Boolean,
    val recrutador: String,
    val empresa: String,
    val lista: String?,
    val data: LocalDateTime
)