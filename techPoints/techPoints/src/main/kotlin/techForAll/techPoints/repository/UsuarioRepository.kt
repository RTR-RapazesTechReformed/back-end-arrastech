package techForAll.techPoints.repository

import techForAll.techPoints.domain.Usuario
import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByEmail(email: String) : Usuario
    fun existsByEmail(email: String): Boolean
    fun findByEmailAndSenha(email: String, senha: String) : Usuario

}
