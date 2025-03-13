package techForAll.techPoints.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import techForAll.techPoints.domain.MetaEstudoSemana
import techForAll.techPoints.domain.TempoEstudo
import techForAll.techPoints.domain.TempoSessao
import techForAll.techPoints.dtos.MetaDiariaDto
import techForAll.techPoints.dtos.MetaEstudoSemanaDto
import techForAll.techPoints.dtos.SessoesDto
import techForAll.techPoints.repository.*
import java.util.*
import kotlin.NoSuchElementException

@Service
class MetaDeEstudoService @Autowired constructor(
    private val metaSemanalRepository: MetaEstudoSemanaRepository,
    private val alunoRepository: AlunoRepository,
    private val metaDiariaRepository: TempoEstudoRepository
) {

    fun cadastrarMetaDiaria(
        metaEstudoSemanaId: Long,
        nomeDia: String,
        data: String,
        qtdTempoEstudo: String,
        ativado: Boolean
    ): MetaDiariaDto {
        val metaEstudoSemana = metaSemanalRepository.findById(metaEstudoSemanaId).orElseGet {
            val aluno = alunoRepository.findById(metaEstudoSemanaId)
                .orElseThrow { NoSuchElementException("Aluno não encontrado") }
            val novaMetaEstudoSemana = MetaEstudoSemana(
                id = metaEstudoSemanaId,
                aluno = aluno,
                diasAtivos = emptyList(),
                horasTotal = 0.0,
                tempoSessao = emptyList()
            )
            metaSemanalRepository.save(novaMetaEstudoSemana)
            novaMetaEstudoSemana
        }

        println("metaEstudoSemana: $metaEstudoSemana")
        val diaIdMap = mapOf(
            "segunda" to 1L,
            "terça" to 2L,
            "quarta" to 3L,
            "quinta" to 4L,
            "sexta" to 5L,
            "sábado" to 6L,
            "domingo" to 7L
        )

        val diaId = diaIdMap[nomeDia.lowercase()] ?: throw IllegalArgumentException("Nome do dia inválido")

        val metaDiariaExistente = metaDiariaRepository.findByMetaEstudoSemanaIdAndNomeDia(metaEstudoSemanaId, nomeDia)

        val tempoEstudo = if (metaDiariaExistente != null) {
            metaDiariaExistente.qtdTempoEstudo = qtdTempoEstudo
            metaDiariaExistente.ativado = ativado
            metaDiariaRepository.save(metaDiariaExistente)
            metaDiariaExistente
        } else {
            val novaMetaDiaria = TempoEstudo(
                id = diaId,
                nomeDia = nomeDia,
                data = data,
                qtdTempoEstudo = qtdTempoEstudo,
                ativado = ativado,
                metaEstudoSemana = metaEstudoSemana
            )
            metaDiariaRepository.save(novaMetaDiaria)
            novaMetaDiaria
        }

        return mapMetaDiariaDto(tempoEstudo)
    }

    private fun mapMetaDiariaDto(tempoEstudo: TempoEstudo): MetaDiariaDto {
        return MetaDiariaDto(
            id = tempoEstudo.id,
            nomeDia = tempoEstudo.nomeDia,
            data = tempoEstudo.data,
            qtdTempoEstudo = tempoEstudo.qtdTempoEstudo,
            ativado = tempoEstudo.ativado,
            metaEstudoSemana = tempoEstudo.metaEstudoSemana.id!!
        )
    }

    private fun mapToSessoesDto(tempoSessao: TempoSessao): SessoesDto {
        return SessoesDto(
            id = tempoSessao.id,
            diaSessao = tempoSessao.diaSessao,
            qtdTempoSessao = tempoSessao.qtdTempoSessao,
            metaEstudoSemana = tempoSessao.metaEstudoSemana.id!!
        )
    }

    fun obterMetaEstudoSemana(metaEstudoSemanaId: Long): MetaEstudoSemanaDto {
        val metaEstudoSemana = metaSemanalRepository.findById(metaEstudoSemanaId)
            .orElseThrow { NoSuchElementException("Meta de estudo semanal não encontrada") }

        val diasAtivos = metaEstudoSemana.diasAtivos ?: emptyList()
        val sessoes = metaEstudoSemana.tempoSessao ?: emptyList()
        val horasTotaisMeta = diasAtivos.sumOf { it.qtdTempoEstudo.toDouble() }
        metaEstudoSemana.horasTotal = horasTotaisMeta

        val metasDiariasDto = diasAtivos.map { mapMetaDiariaDto(it) }
        val sessoesDto = sessoes.map { mapToSessoesDto(it) }

        return MetaEstudoSemanaDto(
            id = metaEstudoSemana.id,
            alunoId = metaEstudoSemana.aluno.id!!,
            diasAtivos = metasDiariasDto,
            horasTotal = metaEstudoSemana.horasTotal,
            sessoes = sessoesDto
        )
    }
}