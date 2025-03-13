package techForAll.techPoints.service

import techForAll.techPoints.domain.Empresa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import techForAll.techPoints.dtos.EmpresaComEnderecoDto
import techForAll.techPoints.dtos.EmpresaComRecrutadoresDto
import techForAll.techPoints.dtos.EmpresaInput
import techForAll.techPoints.repository.DadosEmpresaRepository
import techForAll.techPoints.repository.EnderecoRepository
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
class DadosEmpresaService @Autowired constructor(
    private val empresaRepository: DadosEmpresaRepository,
    private val enderecoRepository: EnderecoRepository
) {
    fun cadastrarEmpresa(novaEmpresa: EmpresaInput): Empresa {
        val endereco = enderecoRepository.findById(novaEmpresa.enderecoId)
            .orElseThrow { IllegalArgumentException("Endereço não encontrado") }
        val empresa = Empresa(
            nomeEmpresa = novaEmpresa.nomeEmpresa,
            cnpj = novaEmpresa.cnpj,
            setorIndustria = novaEmpresa.setorIndustria,
            telefoneContato = novaEmpresa.telefoneContato,
            emailCorporativo = novaEmpresa.emailCorporativo,
            endereco = endereco
        )
        return empresaRepository.save(empresa)
    }

    fun listarEmpresas(): List<EmpresaComRecrutadoresDto> {
        val empresas = empresaRepository.findAll()
        if (empresas.isEmpty()) {
            throw NoSuchElementException("Nenhuma empresa encontrada")
        }
        return empresas.map { empresa ->
            EmpresaComRecrutadoresDto(
                id = empresa.id,
                nomeEmpresa = empresa.nomeEmpresa,
                cnpj = empresa.cnpj,
                setorIndustria = empresa.setorIndustria,
                telefoneContato = empresa.telefoneContato,
                emailCorporativo = empresa.emailCorporativo,
                endereco = empresa.endereco,
                dataCriacao = empresa.dataCriacao,
                recrutadores = empresa.recrutadores.map { it.nomeUsuario }
            )
        }
    }

    fun buscarEmpresaPorId(idEmpresa: Long): EmpresaComRecrutadoresDto {
        val dadosEmpresa = empresaRepository.findById(idEmpresa)
            .orElseThrow { NoSuchElementException("Empresa não encontrada com o ID: $idEmpresa") }

        val dadosEmpresaExibir = EmpresaComRecrutadoresDto(
            id = dadosEmpresa.id,
            nomeEmpresa = dadosEmpresa.nomeEmpresa,
            cnpj = dadosEmpresa.cnpj,
            setorIndustria = dadosEmpresa.setorIndustria,
            telefoneContato = dadosEmpresa.telefoneContato,
            emailCorporativo = dadosEmpresa.emailCorporativo,
            endereco = dadosEmpresa.endereco,
            dataCriacao = dadosEmpresa.dataCriacao,
            recrutadores = dadosEmpresa.recrutadores.map{ it.nomeUsuario }
        )

        return dadosEmpresaExibir
    }

    fun atualizarEmpresa(idEmpresa: Long, empresaAtualizada: Map<String, Any>): EmpresaComRecrutadoresDto {
        val empresaExistente = empresaRepository.findById(idEmpresa)
            .orElseThrow { NoSuchElementException("Empresa não encontrada com o ID: $idEmpresa") }

        empresaAtualizada["nomeEmpresa"]?.let { empresaExistente.nomeEmpresa = it as String }
        empresaAtualizada["setorIndustria"]?.let { empresaExistente.setorIndustria = it as String }
        empresaAtualizada["telefoneContato"]?.let { empresaExistente.telefoneContato = it as String }
        empresaAtualizada["emailCorporativo"]?.let { empresaExistente.emailCorporativo = it as String }
        empresaAtualizada["cnpj"]?.let { empresaExistente.cnpj = it as String }
        empresaExistente.dataAtualizacao = LocalDateTime.now()

        empresaRepository.save(empresaExistente)
        val dadosEmpresaExibir = EmpresaComRecrutadoresDto(
            id = empresaExistente.id,
            nomeEmpresa = empresaExistente.nomeEmpresa,
            cnpj = empresaExistente.cnpj,
            setorIndustria = empresaExistente.setorIndustria,
            telefoneContato = empresaExistente.telefoneContato,
            emailCorporativo = empresaExistente.emailCorporativo,
            endereco = empresaExistente.endereco,
            dataCriacao = empresaExistente.dataCriacao,
            recrutadores = empresaExistente.recrutadores.map{ it.nomeUsuario }
        )

        return dadosEmpresaExibir

    }

    fun deletarEmpresa(idEmpresa: Long) {
        val empresaExistente = empresaRepository.findById(idEmpresa)
            .orElseThrow { NoSuchElementException("Empresa não encontrada com o ID: $idEmpresa") }

        empresaRepository.delete(empresaExistente)
    }

    fun buscarEmpresaPorCnpj(cnpj: String): EmpresaComEnderecoDto {
        val empresa = empresaRepository.findByCnpj(cnpj)
            .orElseThrow { NoSuchElementException("Empresa não encontrada com o CNPJ: $cnpj") }

        return EmpresaComEnderecoDto(
            id = empresa.id,
            nomeEmpresa = empresa.nomeEmpresa,
            cnpj = empresa.cnpj,
            setorIndustria = empresa.setorIndustria,
            telefoneContato = empresa.telefoneContato,
            emailCorporativo = empresa.emailCorporativo,
            endereco = empresa.endereco
        )
    }

}


