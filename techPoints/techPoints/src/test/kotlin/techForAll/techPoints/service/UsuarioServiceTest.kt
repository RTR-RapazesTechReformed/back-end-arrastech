package techForAll.techPoints.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import techForAll.techPoints.domain.*
import techForAll.techPoints.dtos.UsuarioInput
import techForAll.techPoints.repository.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class UsuarioServiceTest {

    private val alunoRepository: AlunoRepository = mock()
    private val recrutadorRepository: RecrutadorRepository = mock()
    private val usuarioRepository: UsuarioRepository = mock()
    private val dadosEmpresaRepository: DadosEmpresaRepository = mock()
    private val enderecoRepository: EnderecoRepository = mock()
    private val administradorRepository: AdministradorRepository = mock()

    private val usuarioService = UsuarioService(
        alunoRepository,
        recrutadorRepository,
        usuarioRepository,
        dadosEmpresaRepository,
        enderecoRepository,
        administradorRepository
    )
    val endereco = Endereco(
        id = 1L,
        cidade = "São Paulo",
        estado = "SP",
        rua = "Rua Teste",
        numero = "123",
        cep = "03360330",
        dataCriacao =  LocalDateTime.now(),
    )
    val empresa = Empresa(
        id = 1,
        nomeEmpresa = "Tech Company",
        cnpj = "12345678000123",
        setorIndustria = "Tecnologia",
        telefoneContato = "1122334455",
        emailCorporativo = "contato@techcompany.com",
        endereco = endereco
    )
    val usuario = Aluno(
        nomeUsuario = "aluno01",
        cpf = "12345678900",
        senha = "senha123",
        primeiroNome = "João",
        sobrenome = "Silva",
        email = "joao.silva@email.com",
        telefone = "123456789",
        escolaridade = "Ensino Médio",
        sexo = "Masculino",
        etnia = "Branco",
        autenticado = false,
        endereco = endereco,
        descricao = null,
        dtNasc = LocalDate.of(2000, 1, 1),
        imagemPerfil = null
    )
    val recrutador = Recrutador(
        favoritos = emptyList(),
        interessados = emptyList(),
        processoSeletivo = emptyList(),
        contratados = emptyList(),
        cancelados = emptyList(),
        empresa = empresa,
        cargoUsuario = "Recrutador",
        nomeUsuario = "recrutador01",
        cpf = "12345678901",
        senha = "senha123",
        primeiroNome = "Carlos",
        sobrenome = "Oliveira",
        email = "carlos.oliveira@email.com",
        telefone = "987654321",
        imagemPerfil = null,
        autenticado = true
    )
    val administrador = Administrador(
        nomeUsuario = "admin01",
        cpf = "98765432100",
        senha = "admin123",
        primeiroNome = "Ana",
        sobrenome = "Pereira",
        email = "ana.pereira@email.com",
        telefone = "1122334455",
        imagemPerfil = null,
        autenticado = true,
        cargo = "Administrador",
        nivelAcesso = 1
    )

    @Test
    fun `deve cadastrar um aluno com sucesso`() {

        val request = UsuarioInput(
            tipoUsuario = 1,
            nomeUsuario = "aluno01",
            cpf = "12345678900",
            senha = "senha123",
            primeiroNome = "João",
            sobrenome = "Silva",
            email = "joao.silva@email.com",
            telefone = "123456789",
            enderecoId = 1,
            escolaridade = "Ensino Médio",
            sexo = "Masculino",
            etnia = "Branco",
            dtNasc = LocalDate.of(2000, 1, 1),
            autenticado = false,
            imagemPerfil = null,
            cnpj = null,
            cargoUsuario = null,
            descricao = null,
            nivelAcesso = null
        )

        val aluno = Aluno(
            escolaridade = "Ensino Médio",
            sexo = "Masculino",
            etnia = "Branco",
            endereco = Endereco(
                cidade = "São Paulo",
                estado = "SP",
                rua = "Rua Teste",
                numero = "123",
                cep = "03360330",
            ),
            dtNasc = LocalDate.of(2000, 1, 1),
            nomeUsuario = "aluno01",
            cpf = "12345678900",
            senha = "senha123",
            primeiroNome = "João",
            sobrenome = "Silva",
            email = "joao.silva@email.com",
            telefone = "123456789",
            imagemPerfil = null,
            autenticado = true
        )

        `when`(enderecoRepository.findById(1)).thenReturn(Optional.of(endereco))
        `when`(usuarioRepository.findByEmail("joao.silva@email.com")).thenReturn(aluno)

        val resultado = usuarioService.cadastrarUsuario(request) as Map<String, Any?>

        verify(alunoRepository).save(argThat { aluno ->
            assertEquals("aluno01", aluno.nomeUsuario)
            assertEquals("12345678900", aluno.cpf)
            assertEquals("João", aluno.primeiroNome)
            assertEquals("Ensino Médio", aluno.escolaridade)
            assertEquals("São Paulo", aluno.endereco.cidade)
            true
        })

        assertEquals("aluno01", resultado["nomeUsuario"])
        assertEquals("João", resultado["primeiroNome"])
        assertEquals("Ensino Médio", resultado["escolaridade"])
    }

    @Test
    fun `deve lancar excecao ao cadastrar aluno com endereco inexistente`() {
        val request = UsuarioInput(
            tipoUsuario = 1,
            nomeUsuario = "aluno01",
            cpf = "12345678900",
            senha = "senha123",
            primeiroNome = "João",
            sobrenome = "Silva",
            email = "joao.silva@email.com",
            telefone = "123456789",
            enderecoId = 1L,
            escolaridade = "Ensino Médio",
            sexo = "Masculino",
            etnia = "Branco",
            dtNasc = LocalDate.of(2000, 1, 1),
            autenticado = false,
            imagemPerfil = null,
            cnpj = null,
            cargoUsuario = null,
            descricao = null,
            nivelAcesso = null
        )

        `when`(enderecoRepository.findById(1L)).thenReturn(Optional.empty())

        val exception = assertThrows<IllegalArgumentException> {
            usuarioService.cadastrarUsuario(request)
        }

        assertEquals("Endereço não encontrado", exception.message)
    }

    @Test
    fun `cadastrarUsuario deve lançar IllegalArgumentException para tipo de usuário inválido`() {

        val request = UsuarioInput(
            tipoUsuario = 99,
            nomeUsuario = "aluno01",
            cpf = "12345678900",
            senha = "senha123",
            primeiroNome = "João",
            sobrenome = "Silva",
            email = "joao.silva@email.com",
            telefone = "123456789",
            enderecoId = 1L,
            escolaridade = "Ensino Médio",
            sexo = "Masculino",
            etnia = "Branco",
            dtNasc = LocalDate.of(2000, 1, 1),
            autenticado = false,
            imagemPerfil = null,
            cnpj = null,
            cargoUsuario = null,
            descricao = null,
            nivelAcesso = null
        )

        val exception = assertThrows<IllegalArgumentException> {
            usuarioService.cadastrarUsuario(request)
        }
        assertEquals("Tipo de usuário inválido", exception.message)

    }

    @Test
    fun `deve cadastrar um recrutador com sucesso`() {

        val request = UsuarioInput(
            tipoUsuario = 2,
            nomeUsuario = "recrutador01",
            cpf = "12345678901",
            senha = "senha123",
            primeiroNome = "Carlos",
            sobrenome = "Oliveira",
            email = "carlos.oliveira@email.com",
            telefone = "987654321",
            enderecoId = null,
            escolaridade = null,
            sexo = null,
            etnia = null,
            dtNasc = null,
            autenticado = false,
            imagemPerfil = null,
            cnpj = "12345678000123",
            cargoUsuario = "Recrutador",
            descricao = null,
            nivelAcesso = null
        )

        `when`(enderecoRepository.findById(1)).thenReturn(Optional.of(endereco))
        `when`(dadosEmpresaRepository.findByCnpj("12345678000123")).thenReturn(Optional.of(empresa))
        `when`(usuarioRepository.findByEmail("carlos.oliveira@email.com")).thenReturn(recrutador)

        val resultado = usuarioService.cadastrarUsuario(request) as Map<String, Any?>

        verify(recrutadorRepository).save(argThat { recrutador ->
            assertEquals("recrutador01", recrutador.nomeUsuario)
            assertEquals("12345678901", recrutador.cpf)
            assertEquals("Carlos", recrutador.primeiroNome)
            assertEquals("Recrutador", recrutador.cargoUsuario)
            assertEquals("Tech Company", recrutador.empresa.nomeEmpresa)
            true
        })

        assertEquals("recrutador01", resultado["nomeUsuario"])
        assertEquals("Carlos", resultado["primeiroNome"])
        assertEquals("Recrutador", resultado["cargoUsuario"])
    }

    @Test
    fun `deve cadastrar um administrador com sucesso`() {

        val request = UsuarioInput(
            tipoUsuario = 3,
            nomeUsuario = "admin01",
            cpf = "98765432100",
            senha = "admin123",
            primeiroNome = "Ana",
            sobrenome = "Pereira",
            email = "ana.pereira@email.com",
            telefone = "1122334455",
            enderecoId = null,
            escolaridade = null,
            sexo = null,
            etnia = null,
            dtNasc = null,
            autenticado = false,
            imagemPerfil = null,
            cnpj = null,
            cargoUsuario = "Administrador",
            descricao = "Administrador do sistema",
            nivelAcesso = 1
        )

        `when`(enderecoRepository.findById(1)).thenReturn(Optional.of(endereco))
        `when`(usuarioRepository.findByEmail("ana.pereira@email.com")).thenReturn(administrador)

        val resultado = usuarioService.cadastrarUsuario(request) as Map<String, Any?>

        verify(administradorRepository).save(argThat { administrador ->
            assertEquals("admin01", administrador.nomeUsuario)
            assertEquals("98765432100", administrador.cpf)
            assertEquals("Ana", administrador.primeiroNome)
            assertEquals("Administrador", administrador.cargo)
            assertEquals(1, administrador.nivelAcesso)
            true
        })

        assertEquals("admin01", resultado["nomeUsuario"])
        assertEquals("Ana", resultado["primeiroNome"])
        assertEquals("Administrador", resultado["cargo"])
        assertEquals(1, resultado["nivelAcesso"])
    }


    @Test
    fun `deve realizar soft delete de usuario com sucesso`() {

        `when`(usuarioRepository.findByEmailAndSenha("joao.silva@email.com", "senha123")).thenReturn(usuario)

        usuarioService.softDeleteUsuario("joao.silva@email.com", "senha123")

        assertTrue(usuario.deletado)
        assertNotNull(usuario.dataDeletado)
        verify(usuarioRepository).save(usuario)
    }

    @Test
    fun `deve lancar excecao ao realizar soft delete de usuario inexistente`() {
        `when`(usuarioRepository.findByEmailAndSenha("email@email.com", "senha123")).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            usuarioService.softDeleteUsuario("email@email.com", "senha123")
        }

        assertEquals("Credenciais inválidas", exception.message)
    }

    @Test
    fun `deve deletar o usuário corretamente`() {

        val email = "joao.silva@email.com"
        val senha = "senha123"

        `when`(usuarioRepository.findByEmailAndSenha(email, senha)).thenReturn(usuario)

        usuarioService.hardDeleteUsuario(email, senha)

        verify(usuarioRepository, times(1)).delete(usuario)
    }
    @Test
    fun `deve lançar exceção se usuário não encontrado para hardDeleteUsuario`() {

        val email = "usuario@teste.com"
        val senha = "senha123"

        `when`(usuarioRepository.findByEmailAndSenha(email, senha)).thenReturn(null)

        assertThrows<NoSuchElementException> {
            usuarioService.hardDeleteUsuario(email, senha)
        }
    }
    @Test
    fun `deve listar usuários do tipo aluno`() {

        val aluno1 = usuario

        val aluno2 = Aluno(
            nomeUsuario = "aluno03",
            cpf = "98765432100",
            senha = "senha456",
            primeiroNome = "Maria",
            sobrenome = "Oliveira",
            email = "maria.oliveira@email.com",
            telefone = "987654321",
            escolaridade = "Superior",
            sexo = "Feminino",
            etnia = "Negra",
            autenticado = false,
            endereco = Endereco(
                id = 2L,
                cidade = "Rio de Janeiro",
                estado = "RJ",
                rua = "Avenida Teste",
                numero = "456",
                cep = "22060100",
                dataCriacao = LocalDateTime.now()
            ),
            descricao = "Estudante de Engenharia de Software",
            dtNasc = LocalDate.of(1998, 5, 15),
            imagemPerfil = null
        )


        `when`(usuarioRepository.findAll()).thenReturn(listOf(aluno1, aluno2))

        val usuarios = usuarioService.listarUsuarios("aluno")

        assert(usuarios.size == 2)
    }

    @Test
    fun `deve listar todos os usuários`() {

        `when`(usuarioRepository.findAll()).thenReturn(listOf(usuario, recrutador, administrador))

        val usuarios = usuarioService.listarUsuarios("todos")

        assertEquals(3, usuarios.size)
        assertEquals("aluno01", usuarios[0]["nomeUsuario"])
        assertEquals("recrutador01", usuarios[1]["nomeUsuario"])
        assertEquals("admin01", usuarios[2]["nomeUsuario"])

        verify(usuarioRepository, times(1)).findAll()
        assert(usuarios.size == 3)
    }

    @Test
    fun `deve buscar usuário por ID`() {

        val usuarioId = 1L

        `when`(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario))

        val resultado = usuarioService.buscarUsuarioPorId(usuarioId)

        assert(resultado["email"] == "joao.silva@email.com")
    }

    @Test
    fun `deve lançar exceção se usuário não encontrado por ID`() {

        val usuarioId = 1L

        `when`(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            usuarioService.buscarUsuarioPorId(usuarioId)
        }
    }
    @Test

    fun `deve realizar login corretamente`() {

        `when`(usuarioRepository.findByEmail(administrador.email)).thenReturn(administrador)

        val resultado = usuarioService.loginUsuario(administrador.email, administrador.senha) as Map<String, Any?>

        assert(resultado["email"] == "ana.pereira@email.com")
    }

    @Test
    fun `deve lançar exceção se usuário não encontrado no login`() {

        val email = "usuario@teste.com"
        val senha = "senha123"

        `when`(usuarioRepository.findByEmail(email)).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            usuarioService.loginUsuario(email, senha)
        }
    }

    @Test
    fun `deve realizar logoff corretamente`() {
        val usuarioId = 1L

        val usuario = mock(Usuario::class.java)

        `when`(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario))

        usuarioService.logoffUsuario(usuarioId)

        verify(usuario, times(1)).logoff()

        verify(usuarioRepository, times(1)).save(usuario)
    }

    @Test
    fun `deve buscar usuário por email`() {
        val email = "joao.silva@email.com"

        `when`(usuarioRepository.findByEmail(email)).thenReturn(usuario)

        val resultado = usuarioService.buscarUsuarioPorEmail(email)

        assertNotNull(resultado)
        assertEquals(usuario.nomeUsuario, resultado["nomeUsuario"])
        verify(usuarioRepository, times(1)).findByEmail(email)
    }

    @Test
    fun `deve atualizar informações do aluno`() {
        val idUsuario = 1L

        val atualizacao = mapOf(
            "nomeUsuario" to "aluno02",
            "email" to "aluno02@teste.com"
        )

        `when`(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario))

        val resultado = usuarioService.atualizarUsuario(idUsuario, atualizacao) as Map<String, Any?>


        assertEquals("aluno02", resultado["nomeUsuario"])
        assertEquals("aluno02@teste.com", resultado["email"])
        verify(usuarioRepository, times(1)).save(usuario)
    }

    @Test
    fun `deve atualizar informações do recrutador`() {
        val idUsuario = 1L

        val atualizacao = mapOf(
            "cargoUsuario" to "Recrutador Chefe",
        )

        `when`(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(recrutador))

        val resultado = usuarioService.atualizarUsuario(idUsuario, atualizacao) as Map<String, Any?>


        assertEquals("recrutador01", resultado["nomeUsuario"])
        assertEquals("carlos.oliveira@email.com", resultado["email"])
        verify(usuarioRepository, times(1)).save(recrutador)
    }

    @Test
    fun `deve atualizar informações do adm`() {
        val idUsuario = 1L

        val atualizacao = mapOf(
            "cargo" to "superAdm",
            "descricap" to "todas as permissões",
            "nivelAcesso" to 2
        )

        `when`(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(administrador))

        val resultado = usuarioService.atualizarUsuario(idUsuario, atualizacao) as Map<String, Any?>

        assertEquals("admin01", resultado["nomeUsuario"])
        assertEquals("ana.pereira@email.com", resultado["email"])
        verify(usuarioRepository, times(1)).save(administrador)
    }

    @Test
    fun `deve atualizar a imagem de perfil do usuário`() {
        val idUsuario = 1L
        val novaFoto = byteArrayOf(1, 2, 3, 4)

        `when`(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario))

        usuarioService.atualizarImagemUsuario(idUsuario, novaFoto)

        assertTrue(usuario.imagemPerfil!!.contentEquals(novaFoto))
        verify(usuarioRepository, times(1)).save(usuario)
    }

    @Test
    fun `deve lançar exceção ao tentar atualizar imagem vazia`() {
        val idUsuario = 1L
        val novaFoto = byteArrayOf()

        assertThrows<IllegalArgumentException> {
            usuarioService.atualizarImagemUsuario(idUsuario, novaFoto)
        }
    }

    @Test
    fun `deve obter a imagem de perfil do usuário`() {
        val idUsuario = 1L
        val imagemPerfil = byteArrayOf(1, 2, 3, 4)

        usuario.imagemPerfil  = byteArrayOf(1, 2, 3, 4)

        `when`(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario))

        val resultado = usuarioService.obterImagemPerfil(idUsuario)

        assertTrue(resultado.contentEquals(imagemPerfil))
        verify(usuarioRepository, times(1)).findById(idUsuario)
    }

    @Test
    fun `deve lançar exceção quando imagem de perfil não encontrada`() {
        val idUsuario = 1L

        `when`(usuarioRepository.findById(idUsuario)).thenReturn(Optional.of(usuario))

        assertThrows<NoSuchElementException> {
            usuarioService.obterImagemPerfil(idUsuario)
        }
    }

    @Test
    fun `deve reativar usuário corretamente quando deletado`() {
        val email = "joao.silva@email.com"
        val senha = "senha123"

        val usuario: Usuario = mock(Aluno::class.java)

        usuario.deletado = true
        usuario.dataDeletado = LocalDateTime.now()

        `when`(usuarioRepository.findByEmailAndSenha(email, senha)).thenReturn(usuario)

        usuarioService.reativarUsuario(email, senha)

        verify(usuario, times(1)).reativar()

        verify(usuarioRepository, times(1)).save(usuario)
    }

    @Test
    fun `deve lançar IllegalArgumentException quando o usuário já está ativo`() {
        val email = "joao.silva@email.com"
        val senha = "senha123"

        usuario.deletado = false

        `when`(usuarioRepository.findByEmailAndSenha(email, senha)).thenReturn(usuario)

        assertThrows<IllegalArgumentException> {
            usuarioService.reativarUsuario(email, senha)
        }
    }

    @Test
    fun `deve lançar exceção quando tentar reativar usuário com credenciais incorretas`() {
        val email = "joao.silva@email.com"
        val senha = "senha123"

        `when`(usuarioRepository.findByEmailAndSenha(email, senha)).thenReturn(null)

        assertThrows<NoSuchElementException> {
            usuarioService.reativarUsuario(email, senha)
        }
    }

    @Test
    fun `deve lançar IllegalStateException quando tipo de usuário desconhecido for passado`() {
        val usuarioDesconhecido = Any()

        assertThrows<IllegalStateException> {
            usuarioService.mapearUsuario(usuarioDesconhecido)
        }
    }

}
