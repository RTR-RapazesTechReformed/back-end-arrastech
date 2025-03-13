package techForAll.techPoints.dtos

import techForAll.techPoints.domain.Aluno

data class DemografiaDto(
    var sexo: MutableMap<String, Int> = mutableMapOf(),
    var etnia: MutableMap<String, Int> = mutableMapOf(),
    var escolaridade: MutableMap<String, Int> = mutableMapOf(),
    var cidade: MutableMap<String, Int> = mutableMapOf(),
    val cursosFeitos: MutableMap<String, Int> = mutableMapOf()
) {
    fun incrementarSexo(sexo: String) {
        this.sexo[sexo] = this.sexo.getOrDefault(sexo, 0) + 1
    }

    fun incrementarEtnia(etnia: String) {
        this.etnia[etnia] = this.etnia.getOrDefault(etnia, 0) + 1
    }

    fun incrementarEscolaridade(escolaridade: String) {
        this.escolaridade[escolaridade] = this.escolaridade.getOrDefault(escolaridade, 0) + 1
    }

    fun incrementarCidade(cidade: String) {
        this.cidade[cidade] = this.cidade.getOrDefault(cidade, 0) + 1
    }

    fun processarAluno(aluno: Aluno) {
        aluno.sexo?.let { incrementarSexo(it) }
        aluno.etnia?.let { incrementarEtnia(it) }
        incrementarEscolaridade(aluno.escolaridade)
        incrementarCidade(aluno.endereco.cidade)
    }
}