package techForAll.techPoints.service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import techForAll.techPoints.domain.Endereco
import techForAll.techPoints.dtos.CursoAlunosDto
import techForAll.techPoints.dtos.DemografiaDto
import techForAll.techPoints.dtos.UsuarioInput

import techForAll.techPoints.repository.AlunoRepository
import techForAll.techPoints.repository.DashboardAdmRepository
import techForAll.techPoints.repository.EnderecoRepository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.concurrent.ArrayBlockingQueue


@Service
class DashboardAdmService@Autowired constructor(
    private val dashAdmRepositoy: DashboardAdmRepository,
    private val enderecoRepository: EnderecoRepository,
    private val enderecoService: EnderecoService,
    private val alunoRepository: AlunoRepository,
    private val pontuacaoService: PontuacaoService,
    private val usuarioService: UsuarioService
){
    fun getAlunosPorCurso(): List<CursoAlunosDto> {
        val rankingPorCurso = pontuacaoService.recuperarRankingPorCurso()

        return rankingPorCurso.map { (cursoId, cursoData) ->
            val nomeCurso = cursoData["nomeCurso"] as String
            val ranking = cursoData["ranking"] as List<Map<String, Any>>
            val quantidadeAlunos = ranking.size

            CursoAlunosDto(
                nomeCurso = nomeCurso,
                quantidadeAlunos = quantidadeAlunos
            )
        }
    }

    fun getDemografiaPorTipoLista(tipoLista: String, idEmpresa: Long?): DemografiaDto {

        val ids = getAlunosPorTipoLista(tipoLista, idEmpresa)

        val idsFila = processarIdsJson(ids)
        if (idsFila.isEmpty()){
            throw NoSuchElementException("Nenhum aluno encontrado")
        }
        val demografia = processarFilaDemografia(idsFila)
        demografia.cursosFeitos.putAll(processarFilaCursosFeitosPorAlunos(idsFila))
        return demografia
    }
    fun gerarRelatorioDemografiaEmpresas(tipoLista: String, idEmpresa: Long?): String {

        val ids = getAlunosPorTipoLista(tipoLista, idEmpresa)

        val idsFila = processarIdsJson(ids)

        return gerarRelatorioCSV(idsFila.toList())
    }

    fun gerarRelatorioDemografiaAlunos(
        sexo: String?,
        etnia: String?,
        idadeMaxima: Int?,
        cidade: String?,
        escolaridade: String?,
        arquivo: String
    ): String {
        val alunos = alunoRepository.findAlunosFiltrados(sexo, etnia, idadeMaxima, cidade, escolaridade)
        if (arquivo == "csv") {
            return gerarRelatorioAlunos(alunos)
        } else  return gerarRelatorioTXT(alunos)
    }

    fun gerarRelatorioCSV(ids: List<Long>): String {
        val csvHeader = listOf(
            "ID", "Nome de Usuário", "CPF", "Primeiro Nome", "Sobrenome", "Email", "Telefone", "Tipo Usuário", "Sexo",
            "Etnia", "Escolaridade", "Data de Nascimento", "Deletado", "CEP", "Rua", "Cidade", "Estado", "Cursos"
        )

        val csvRows = ids.map { id ->
            val alunoComCursos = comporAlunoComCursos(id)

            val cursos = alunoComCursos["cursos"] as? Map<Long, Map<String, Any>>
            val endereco = alunoComCursos["endereco"] as Map<String, String>

            listOf(
                alunoComCursos["id"],
                alunoComCursos["nomeUsuario"],
                alunoComCursos["cpf"],
                alunoComCursos["primeiroNome"],
                alunoComCursos["sobrenome"],
                alunoComCursos["email"],
                alunoComCursos["telefone"],
                alunoComCursos["tipoUsuario"],
                alunoComCursos["sexo"],
                alunoComCursos["etnia"],
                alunoComCursos["escolaridade"],
                alunoComCursos["dataNascimento"],
                alunoComCursos["deletado"],
                endereco["cep"],
                endereco["rua"],
                endereco["cidade"],
                endereco["estado"],
                cursos!!.entries.joinToString("; ") { (cursoId, cursoData) ->
                    "${cursoData["nomeCurso"]} (Pontos: ${cursoData["pontosTotais"]})"
                }
            ).joinToString(",")
        }

        return (listOf(csvHeader.joinToString(",")) + csvRows).joinToString("\n")
    }

    fun gerarRelatorioTXT(ids: List<Long>): String {
        val dataAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val txtHeader = "00ARQUIVO_USUARIOS${dataAtual.padEnd(24)}V1"

        val txtRows = ids.map { id ->
            val alunoComDados = comporAlunoComCursos(id)
            val endereco = alunoComDados["endereco"] as Map<String, String>


            val dataNascimento = alunoComDados["dataNascimento"]

            println(dataNascimento)

            "02" + listOf(
                alunoComDados["id"].toString().padEnd(3).substring(0, 3),
                alunoComDados["nomeUsuario"].toString().padEnd(20).substring(0, 20),
                alunoComDados["cpf"].toString().padEnd(11).substring(0, 11),
                alunoComDados["email"]?.toString()?.padEnd(40)?.substring(0, 40),
                alunoComDados["primeiroNome"].toString().padEnd(20).substring(0, 20),
                alunoComDados["sobrenome"].toString().padEnd(20).substring(0, 20),
                alunoComDados["telefone"].toString().padEnd(11).substring(0, 11),
                alunoComDados["dataNascimento"].toString().padEnd(10).substring(0, 10),
                alunoComDados["escolaridade"].toString().padEnd(22).substring(0, 22),
                alunoComDados["sexo"].toString().padEnd(9).substring(0, 9),
                alunoComDados["etnia"].toString().padEnd(8).substring(0, 8),
                alunoComDados["deletado"].toString().padEnd(5).substring(0, 5),
                endereco["cep"].toString().padEnd(9).substring(0, 9),
                endereco["rua"].toString().padEnd(30).substring(0, 30),
                endereco["numero"].toString().padEnd(5).substring(0, 5),
                endereco["cidade"].toString().padEnd(30).substring(0, 30),
                endereco["estado"].toString().padEnd(2).substring(0, 2)
            ).joinToString("")
        }

        val txtTrailer = "01${ids.size.toString().padStart(10, '0')}"

        return (listOf(txtHeader) + txtRows + txtTrailer).joinToString("\r\n")
    }

    fun gerarRelatorioAlunos(ids: List<Long>): String {
        val csvHeader = listOf(
            "ID", "Nome de Usuário", "CPF", "Primeiro Nome", "Sobrenome", "Email", "Telefone", "Tipo Usuário", "Sexo",
            "Etnia", "Escolaridade", "Data de Nascimento", "Deletado", "CEP", "Rua", "Cidade", "Estado",
        )

        val csvRows = ids.map { id ->
            val alunoComCursos = comporAlunoComCursos(id)

            val endereco = alunoComCursos["endereco"] as Map<String, String>

            listOf(
                alunoComCursos["id"],
                alunoComCursos["nomeUsuario"],
                alunoComCursos["cpf"],
                alunoComCursos["primeiroNome"],
                alunoComCursos["sobrenome"],
                alunoComCursos["email"],
                alunoComCursos["telefone"],
                alunoComCursos["tipoUsuario"],
                alunoComCursos["sexo"],
                alunoComCursos["etnia"],
                alunoComCursos["escolaridade"],
                alunoComCursos["dataNascimento"],
                alunoComCursos["deletado"],
                endereco["cep"],
                endereco["rua"],
                endereco["cidade"],
                endereco["estado"],
            ).joinToString(",")
        }

        return (listOf(csvHeader.joinToString(",")) + csvRows).joinToString("\n")
    }

    fun comporAlunoComCursos(idAluno: Long): Map<String, Any?> {

        val aluno = usuarioService.buscarUsuarioPorId(idAluno)

        val cursos = pontuacaoService.recuperarPontosTotaisPorCurso(idAluno)

        return aluno.toMutableMap().apply {
            this["cursos"] = cursos

            val endereco = aluno["endereco"]
            this["endereco"] = if (endereco is Endereco) {

                mapOf(
                    "cep" to endereco.cep,
                    "rua" to endereco.rua,
                    "numero" to endereco.numero,
                    "cidade" to endereco.cidade,
                    "estado" to endereco.estado
                )
            } else {
                throw IllegalStateException("Endereço não pode ser nulo. Verifique o ID informado e tente novamente.")
            }
        }
    }

    fun processarFilaDemografia(idsFila: ArrayBlockingQueue<Long>): DemografiaDto {
        val demografia = DemografiaDto()

        val tamanhoInicial = idsFila.size

        for (i in 0 until tamanhoInicial) {
            val id = idsFila.poll()
            val aluno = alunoRepository.findById(id).orElse(null)

            if (aluno != null) {
                demografia.processarAluno(aluno)
            }

            idsFila.add(id)
        }

        return demografia
    }

    fun getAlunosPorTipoLista(tipoLista: String, idEmpresa: Long?): List<Any>{
        val ids = when (tipoLista) {
            "todos" -> if (idEmpresa != null)dashAdmRepositoy.findIdsTodosByEmpresa(idEmpresa) else dashAdmRepositoy.findIdsTodos()
            "contratados" -> if (idEmpresa != null) dashAdmRepositoy.findIdsContratadosByEmpresa(idEmpresa) else dashAdmRepositoy.findIdsContratados()
            "interessados" -> if (idEmpresa != null) dashAdmRepositoy.findIdsInteressadosByEmpresa(idEmpresa) else dashAdmRepositoy.findIdsInteressados()
            "processoSeletivo" -> if (idEmpresa != null) dashAdmRepositoy.findIdsProcessoSeletivoByEmpresa(idEmpresa) else dashAdmRepositoy.findIdsProcessoSeletivo()
            else -> throw IllegalArgumentException("Tipo de lista inválido")
        }
        return ids
    }

    fun processarIdsJson(idStrings: List<Any>): ArrayBlockingQueue<Long> {

        val ids = mutableListOf<Long>()

        /*
            pode receber um objeto desse tipo:
            idStrings = {ArrayList@18270}  size = 1
            0 = {Object[3]@18281}
                0 = "[7]"
                1 = "[5, 4]"
                2 = "[]"
         */

        for (idString in idStrings) {

            if (idString is Array<*>) {

                for (elemento in idString) {

                    if (elemento is String && elemento.isNotBlank() && elemento != "[]") {
                        elemento
                            .removeSurrounding("[", "]")
                            .split(",")
                            .mapNotNull { it.trim().toLongOrNull() }
                            .forEach { ids.add(it) }
                    }
                }
            }

            else if (idString is String && idString.isNotBlank() && idString != "[]") {

                idString
                    .removeSurrounding("[", "]")
                    .split(",")
                    .mapNotNull { it.trim().toLongOrNull() }
                    .forEach { ids.add(it) }
            }
        }
        if (ids.isNotEmpty()) {
            val idsFila = ArrayBlockingQueue<Long>(ids.size)
            idsFila.addAll(ids)
            return idsFila
        } else  throw NoSuchElementException("Nenhum aluno encontrado")
    }

    fun processarFilaCursosFeitosPorAlunos(idsFila: ArrayBlockingQueue<Long>): Map<String, Int> {
        val cursosFeitos = mutableMapOf<String, Int>()

        while (idsFila.isNotEmpty()) {
            val idAluno = idsFila.poll()
            val pontosPorCurso = pontuacaoService.recuperarPontosTotaisPorCurso(idAluno)
            pontosPorCurso.forEach { (_, cursoInfo) ->
                val nomeCurso = cursoInfo["nomeCurso"] as String
                val pontosTotais = cursoInfo["pontosTotais"] as Int
                if (pontosTotais > 0) {
                    cursosFeitos[nomeCurso] = cursosFeitos.getOrDefault(nomeCurso, 0) + 1
                }
            }
        }

        return cursosFeitos
    }

    fun processarArquivoCsv(file: MultipartFile): Map<String, Any> {

        if (file.isEmpty || !file.originalFilename!!.endsWith(".csv")) {
            throw IllegalArgumentException("Arquivo inválido. Apenas arquivos CSV são permitidos.")
        }

        val bufferedReader = BufferedReader(InputStreamReader(file.inputStream, Charsets.UTF_8))
        val alunosCadastrados = mutableListOf<UsuarioInput>()
        val erros = mutableListOf<String>()

        bufferedReader.useLines { lines ->

            lines.drop(1).forEachIndexed { index, line ->
                try {
                    val data = line.split(",").map { it.trim() }

                    if (data.size == 16) {

                        val nomeUsuario = data[0]
                        val cpf = data[1]
                        val email = data[2]
                        val primeiroNome = data[3]
                        val sobrenome = data[4]
                        val telefone = data[5]
                        val senha = data[6]
                        val dtNasc = LocalDate.parse(data[7])
                        val escolaridade = data[8]
                        val sexo = data[9]
                        val etnia = data[10]

                        val cep = data[11]
                        val rua = data[12]
                        val numero = data[13]
                        val cidade = data[14]
                        val estado = data[15]

                        val enderecoCadastro = Endereco(
                            cep = cep,
                            rua = rua,
                            numero = numero,
                            cidade = cidade,
                            estado = estado
                        )
                        enderecoService.cadastrarEndereco(enderecoCadastro)

                        var endereco = enderecoRepository.findByCepAndNumero(cep, numero)

                        val alunoInput = UsuarioInput(
                            nomeUsuario = nomeUsuario,
                            cpf = cpf,
                            senha = senha,
                            primeiroNome = primeiroNome,
                            sobrenome = sobrenome,
                            email = email,
                            telefone = telefone,
                            tipoUsuario = 1,
                            autenticado = false,
                            enderecoId = endereco!!.id,
                            dtNasc = dtNasc,
                            escolaridade = escolaridade,
                            sexo = sexo,
                            etnia = etnia,
                            cnpj = null,
                            cargoUsuario = null,
                            imagemPerfil =  null,
                            descricao =  null,
                            nivelAcesso = null
                        )

                        usuarioService.cadastrarUsuario(alunoInput)

                        alunosCadastrados.add(alunoInput)

                    } else {
                        erros.add("Linha ${index + 2}: Número de colunas incorreto")
                    }

                } catch (ex: Exception) {
                    erros.add("Linha ${index + 2}: ${ex.message}")
                }
            }
        }

        return mapOf(
            "sucesso" to alunosCadastrados.size,
            "erros" to erros
        )
    }

    fun processarArquivoTxt(file: MultipartFile): Map<String, Any> {
        if (file.isEmpty || !file.originalFilename!!.endsWith(".txt")) {
            throw IllegalArgumentException("Arquivo inválido. Apenas arquivos TXT são permitidos.")
        }

        val bufferedReader = BufferedReader(InputStreamReader(file.inputStream, Charsets.UTF_8))
        val usuariosCadastrados = mutableListOf<UsuarioInput>()
        val erros = mutableListOf<String>()
        var qtdRegistrosEsperados = 0

        bufferedReader.useLines { lines ->
            lines.forEachIndexed { index, line ->
                try {
                    when {

                        line.startsWith("00") -> {
                            if (line.length < 32) {
                                erros.add("Linha ${index + 1}: Header com tamanho inválido.")
                            }
                        }


                        line.startsWith("02") -> {
                            if (line.length < 249) {
                                erros.add("Linha ${index + 1}: Registro com tamanho insuficiente.")
                                return@forEachIndexed
                            }

                            val nomeUsuario = line.substring(2, 22).trim()       // Tamanho: 20
                            val cpf = line.substring(22, 33).trim()              // Tamanho: 11
                            val email = line.substring(33, 73).trim()            // Tamanho: 40
                            val primeiroNome = line.substring(73, 93).trim()     // Tamanho: 20
                            val sobrenome = line.substring(93, 113).trim()       // Tamanho: 20
                            val telefone = line.substring(113, 124).trim()       // Tamanho: 11
                            var dtNasc = line.substring(124, 134).trim()         // Tamanho: 10
                            val escolaridade = line.substring(134, 156).trim()   // Tamanho: 22
                            val sexo = line.substring(156, 165).trim()           // Tamanho: 9
                            val etnia = line.substring(165, 173).trim()          // Tamanho: 8
                            val cep = line.substring(173, 182).trim()            // Tamanho: 9
                            val rua = line.substring(182, 212).trim()            // Tamanho: 30
                            val numero = line.substring(212, 217).trim()         // Tamanho: 5
                            val cidade = line.substring(217, 247).trim()         // Tamanho: 30
                            val estado = line.substring(247, 249).trim()         // Tamanho: 2

                            val senhaGerada =  "@Arast$cpf"

                            val dataFormatada =  LocalDate.parse(dtNasc, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                            val enderecoCadastro = Endereco(
                                cep = cep,
                                rua = rua,
                                numero = numero,
                                cidade = cidade,
                                estado = estado
                            )

                            enderecoService.cadastrarEndereco(enderecoCadastro)
                            val endereco = enderecoRepository.findByCepAndNumero(cep, numero)
                                ?: throw IllegalStateException("Endereço não encontrado após cadastro.")


                            val usuarioInput = UsuarioInput(
                                nomeUsuario = nomeUsuario,
                                cpf = cpf,
                                senha = senhaGerada,
                                primeiroNome = primeiroNome,
                                sobrenome = sobrenome,
                                email = email,
                                telefone = telefone,
                                tipoUsuario = 1,
                                autenticado = false,
                                enderecoId = endereco.id,
                                dtNasc = dataFormatada,
                                escolaridade = escolaridade,
                                sexo = sexo,
                                etnia = etnia,
                                cnpj = null,
                                cargoUsuario = null,
                                imagemPerfil =  null,
                                descricao =  null,
                                nivelAcesso =  null
                            )

                            usuarioService.cadastrarUsuario(usuarioInput)
                            usuariosCadastrados.add(usuarioInput)
                        }

                        line.startsWith("01") -> {
                            if (line.length >= 12) {
                                qtdRegistrosEsperados = line.substring(2, 12).trim().toInt()
                            } else {
                                erros.add("Linha ${index + 1}: Trailer com tamanho insuficiente.")
                            }
                        }

                        else -> {
                            erros.add("Linha ${index + 1}: Registro inválido ou desconhecido.")
                        }
                    }
                } catch (ex: Exception) {
                    erros.add("Linha ${index + 1}: Erro ao processar - ${ex.message}")
                }
            }
        }

        if (qtdRegistrosEsperados != usuariosCadastrados.size) {
            erros.add(
                "Quantidade de registros informada no trailer ($qtdRegistrosEsperados) " +
                        "não corresponde ao número de registros processados (${usuariosCadastrados.size})."
            )
        }

        return mapOf(
            "sucesso" to usuariosCadastrados.size,
            "erros" to erros
        )
    }
}