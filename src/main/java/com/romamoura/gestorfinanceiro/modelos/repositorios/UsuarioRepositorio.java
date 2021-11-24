package com.romamoura.gestorfinanceiro.modelos.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long>{

	//Busca um email e o retorna
	Optional<Usuario> findByEmail(String email);
	
	//Verifica se o email existe na base de dados e retorna true ou false
	boolean existsByEmail(String email);
	
}
