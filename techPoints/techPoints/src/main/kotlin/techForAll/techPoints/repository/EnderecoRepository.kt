package techForAll.techPoints.repository

import techForAll.techPoints.domain.Endereco

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnderecoRepository : JpaRepository<Endereco, Long> {

    fun findByCepAndNumero(cep: String, numero: String): Endereco?

}