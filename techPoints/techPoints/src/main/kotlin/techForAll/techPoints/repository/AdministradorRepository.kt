package techForAll.techPoints.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import techForAll.techPoints.domain.Administrador
import java.util.*

@Repository
interface AdministradorRepository : JpaRepository<Administrador, Long>{

    fun findByEmail(email: String): Optional<Administrador>;

}

