package techForAll.techPoints.service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import techForAll.techPoints.domain.Administrador
import techForAll.techPoints.domain.Aluno
import techForAll.techPoints.domain.Curso
import techForAll.techPoints.domain.Recrutador
import techForAll.techPoints.dtos.UsuarioInput
import techForAll.techPoints.repository.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.ArrayBlockingQueue


@Service
class UsuarioService @Autowired constructor(
    private val alunoRepository: AlunoRepository,
    private val recrutadorRepository: RecrutadorRepository,
    private val usuarioRepository: UsuarioRepository,
    private val dadosEmpresaRepository: DadosEmpresaRepository,
    private val enderecoRepository: EnderecoRepository,
    private val administradorRepository: AdministradorRepository

    ) {

    fun cadastrarUsuario(request: UsuarioInput): Any {
        when (request.tipoUsuario) {
            1 -> {
                val endereco = enderecoRepository.findById(request.enderecoId!!)
                    .orElseThrow { IllegalArgumentException("Endereço não encontrado") }
                val aluno = Aluno(
                    nomeUsuario = request.nomeUsuario,
                    cpf = request.cpf,
                    senha = request.senha,
                    primeiroNome = request.primeiroNome,
                    sobrenome = request.sobrenome,
                    email = request.email,
                    telefone = request.telefone,
                    imagemPerfil = null,
                    dtNasc = request.dtNasc!!,
                    escolaridade = request.escolaridade!!,
                    sexo = request.sexo,
                    etnia = request.etnia,
                    autenticado = request.autenticado,
                    endereco = endereco
                )
                alunoRepository.save(aluno.criarUsuario(endereco, null) as Aluno)
            }

            2 -> {
                val empresa = dadosEmpresaRepository.findByCnpj(request.cnpj!!)
                    .orElseThrow { IllegalArgumentException("Empresa não encontrada com o CNPJ informado") }
                val recrutador = Recrutador(
                    favoritos = emptyList(),
                    interessados = emptyList(),
                    empresa = empresa,
                    cargoUsuario = request.cargoUsuario!!,
                    nomeUsuario = request.nomeUsuario,
                    cpf = request.cpf,
                    senha = request.senha,
                    primeiroNome = request.primeiroNome,
                    sobrenome = request.sobrenome,
                    email = request.email,
                    telefone = request.telefone,
                    imagemPerfil = null,
                    autenticado = request.autenticado
                )
                recrutadorRepository.save(recrutador.criarUsuario(null, empresa) as Recrutador)
            }
            3 -> {
                val administrador = Administrador(
                    cargo = request.cargoUsuario!!,
                    ultimoAcesso =  null,
                    descricao = request.descricao!!,
                    nivelAcesso = request.nivelAcesso!!,
                    nomeUsuario = request.nomeUsuario,
                    cpf = request.cpf,
                    senha = request.senha,
                    primeiroNome = request.primeiroNome,
                    sobrenome = request.sobrenome,
                    email = request.email,
                    telefone = request.telefone,
                    imagemPerfil = null,
                    autenticado = request.autenticado
                )
                administradorRepository.save(administrador.criarUsuario(null, null) as Administrador)
            }

            else -> throw IllegalArgumentException("Tipo de usuário inválido")
        }

        val usuario = usuarioRepository.findByEmail(request.email)
        return mapearUsuario(usuario)
    }

    fun softDeleteUsuario(email: String, senha: String) {
        val usuario = usuarioRepository.findByEmailAndSenha(email, senha)
            ?: throw IllegalArgumentException("Credenciais inválidas")

        usuario.deletado = true
        usuario.dataDeletado = LocalDateTime.now()
        usuario.dataAtualizacao = LocalDateTime.now()
        usuarioRepository.save(usuario)
    }

    fun hardDeleteUsuario(email: String, senha: String) {
        val usuario = usuarioRepository.findByEmailAndSenha(email, senha)
            ?: throw NoSuchElementException("Usuário não encontrado")

        usuarioRepository.delete(usuario)
    }

    fun listarUsuarios(tipo: String?): List<Map<String, Any?>> {
        return when (tipo?.lowercase()) {
            "aluno" -> usuarioRepository.findAll().filterIsInstance<Aluno>().map { aluno -> mapearUsuario(aluno) }
            "recrutador" -> usuarioRepository.findAll().filterIsInstance<Recrutador>().map { recrutador -> mapearUsuario(recrutador) }
            "administrador" -> usuarioRepository.findAll().filterIsInstance<Administrador>().map { administrador -> mapearUsuario(administrador) }
            else -> usuarioRepository.findAll().map { usuario -> mapearUsuario(usuario) }
        }
    }

    fun buscarUsuarioPorId(idUsuario: Long): Map<String, Any?> {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { NoSuchElementException("Usuário não encontrado") }
        return mapearUsuario(usuario)
    }

    fun loginUsuario(email: String, senha: String): Any {
        val usuario = usuarioRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Usuário não encontrado")

        if (usuario is Administrador) {
            usuario.ultimoAcesso = LocalDateTime.now()
        }

        usuario.login(senha)
        usuarioRepository.save(usuario)

        return mapearUsuario(usuario)
    }


    fun logoffUsuario(idUsuario: Long) {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { NoSuchElementException("Usuário não encontrado") }

        usuario.logoff()
        usuarioRepository.save(usuario)
    }

    fun buscarUsuarioPorEmail(email: String): Map<String, Any?> {
        val usuario = usuarioRepository.findByEmail(email)
            ?: throw NoSuchElementException("Usuário não encontrado")
        return mapearUsuario(usuario)
    }

    fun atualizarUsuario(idUsuario: Long, atualizacao: Map<String, Any>): Any {
            val usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow { NoSuchElementException("Usuário não encontrado com o ID: $idUsuario") }

            atualizacao["nomeUsuario"]?.let { usuarioExistente.nomeUsuario = it as String }
            atualizacao["cpf"]?.let { usuarioExistente.cpf = it as String }
            atualizacao["primeiroNome"]?.let { usuarioExistente.primeiroNome = it as String }
            atualizacao["sobrenome"]?.let { usuarioExistente.sobrenome = it as String }
            atualizacao["email"]?.let { usuarioExistente.email = it as String }
            atualizacao["telefone"]?.let { usuarioExistente.telefone = it as String }
            usuarioExistente.dataAtualizacao = LocalDateTime.now()

            if (usuarioExistente is Aluno) {
                atualizacao["escolaridade"]?.let { usuarioExistente.escolaridade = it as String }
                atualizacao["sexo"]?.let { usuarioExistente.sexo = it as String }
                atualizacao["etnia"]?.let { usuarioExistente.etnia = it as String }
                atualizacao["dataNascimento"]?.let { usuarioExistente.dtNasc = it as LocalDate }
                atualizacao["descricao"]?.let { usuarioExistente.descricao = it as String }
            }

            if (usuarioExistente is Recrutador) {
                atualizacao["cargoUsuario"]?.let { usuarioExistente.cargoUsuario = it as String }
            }

            if (usuarioExistente is Administrador) {
                atualizacao["cargo"]?.let { usuarioExistente.cargo = it as String }
                atualizacao["descricao"]?.let { usuarioExistente.descricao = it as String }
                atualizacao["nivelAcesso"]?.let { usuarioExistente.nivelAcesso = it as Int }
            }

        usuarioRepository.save(usuarioExistente)

        return mapearUsuario(usuarioExistente)
    }

    fun atualizarImagemUsuario(idUsuario: Long, novaFoto: ByteArray) {
        if (novaFoto.isEmpty()) {
            throw IllegalArgumentException("Requisição inválida: imagem vazia")
        }

        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { NoSuchElementException("Usuário não encontrado") }

        usuario.imagemPerfil = novaFoto
        usuarioRepository.save(usuario)
    }


    fun obterImagemPerfil(idUsuario: Long): ByteArray {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { NoSuchElementException("Usuário não encontrado") }

        return usuario.imagemPerfil ?: throw NoSuchElementException("Imagem de perfil não encontrada")
    }

    fun reativarUsuario(email: String, senha: String) {
        val usuario = usuarioRepository.findByEmailAndSenha(email, senha)
            ?: throw NoSuchElementException("Usuário não encontrado")
        try {
            usuario.reativar()
            usuarioRepository.save(usuario)
        } catch (e: IllegalArgumentException) {
            throw e
        }
    }

    fun mapearUsuario(usuario: Any): Map<String, Any?> {
        return when (usuario) {
            is Aluno -> mapOf(
                "id" to usuario.id,
                "nomeUsuario" to usuario.nomeUsuario,
                "cpf" to usuario.cpf,
                "primeiroNome" to usuario.primeiroNome,
                "sobrenome" to usuario.sobrenome,
                "email" to usuario.email,
                "telefone" to usuario.telefone,
                "tipoUsuario" to "Aluno",
                "autenticado" to usuario.autenticado,
                "dataCriacao" to usuario.dataCriacao,
                "sexo" to usuario.sexo,
                "etnia" to usuario.etnia,
                "escolaridade" to usuario.escolaridade,
                "descricao" to usuario.descricao,
                "dataNascimento" to usuario.dtNasc,
                "dataAtualizacao" to usuario.dataAtualizacao,
                "deletado" to usuario.deletado,
                "dataDeletado" to usuario.dataDeletado,
                "endereco" to usuario.endereco
            )
            is Recrutador -> mapOf(
                "id" to usuario.id,
                "nomeUsuario" to usuario.nomeUsuario,
                "cpf" to usuario.cpf,
                "primeiroNome" to usuario.primeiroNome,
                "sobrenome" to usuario.sobrenome,
                "email" to usuario.email,
                "telefone" to usuario.telefone,
                "tipoUsuario" to "Recrutador",
                "autenticado" to usuario.autenticado,
                "cargoUsuario" to usuario.cargoUsuario,
                "dataCriacao" to usuario.dataCriacao,
                "dataAtualizacao" to usuario.dataAtualizacao,
                "deletado" to usuario.deletado,
                "dataDeletado" to usuario.dataDeletado,
                "empresa" to mapOf(
                    "nome" to usuario.empresa.nomeEmpresa,
                    "cnpj" to usuario.empresa.cnpj,
                    "setorIndustria" to usuario.empresa.setorIndustria,
                    "telefoneContato" to usuario.empresa.telefoneContato,
                    "emailCorporativo" to usuario.empresa.emailCorporativo,
                    "endereco" to usuario.empresa.endereco,
                    "dataCriacao" to usuario.empresa.dataCriacao,
                    "recrutadores" to usuario.empresa.recrutadores.map { it.nomeUsuario }
                )
            )
            is Administrador -> mapOf(
                "id" to usuario.id,
                "nomeUsuario" to usuario.nomeUsuario,
                "cpf" to usuario.cpf,
                "primeiroNome" to usuario.primeiroNome,
                "sobrenome" to usuario.sobrenome,
                "email" to usuario.email,
                "telefone" to usuario.telefone,
                "tipoUsuario" to "Administrador",
                "autenticado" to usuario.autenticado,
                "cargo" to usuario.cargo,
                "descricao" to usuario.descricao,
                "nivelAcesso" to usuario.nivelAcesso,
                "ultimoAcesso" to usuario.ultimoAcesso,
                "dataCriacao" to usuario.dataCriacao,
                "dataAtualizacao" to usuario.dataAtualizacao,
                "deletado" to usuario.deletado,
                "dataDeletado" to usuario.dataDeletado
            )
            else -> throw IllegalStateException("Tipo de usuário desconhecido")
        }
    }

}
