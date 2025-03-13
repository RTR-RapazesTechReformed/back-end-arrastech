package techForAll.techPoints.repository

import techForAll.techPoints.domain.Empresa
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DadosEmpresaRepository : JpaRepository<Empresa, Long> {
    fun findByCnpj(cnpj: String): Optional<Empresa>
}