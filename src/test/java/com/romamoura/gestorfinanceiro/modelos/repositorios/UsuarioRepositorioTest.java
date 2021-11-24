package com.romamoura.gestorfinanceiro.modelos.repositorios;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositorioTest {
	
	@Autowired
	UsuarioRepositorio usuarioRepositorio;
	
	@Autowired
	TestEntityManager testManagerEntity;
	
	@Test
	public void verificaAExistenciaEmail() {
		
		//Cenário
		
		//Criando o usuário
		Usuario usuario =  Usuario
				.builder()
				.nome("Chico da Silva")
				.email("chico@gmail.com")
				.build();
		testManagerEntity.persist(usuario);
		
		//Ação
		
		boolean existe = usuarioRepositorio.existsByEmail("chico@gmail.com");
		
		//verificação
		
		assertTrue(existe);
	
		
	}
	
	@Test
	public void verificaANaoExistenciaEmail() {
		//cenário
		
		//Ação
		boolean existe = usuarioRepositorio.existsByEmail("usuario1@gmail.com");
		
		//verificação
		
		assertFalse(existe);
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		
		//Cenário
		Usuario usuario = criarUsuario();
		
		//Ação
		
		Usuario usuarioSalvo = usuarioRepositorio.save(usuario);
		
		//Verificação
		assertNotNull(usuarioSalvo.getId());
		
	}

	@Test
	public void deveBuscarUmUsuarioorEmail() {
		
		//Cenário
		Usuario usuario = criarUsuario();
		testManagerEntity.persist(usuario);
		
		//verificação
		Optional<Usuario> result = usuarioRepositorio.findByEmail("chico@gmail.com");
		
		assertTrue(result.isPresent());
		
	}
	
	@Test
	public void deveRetornarVazioUmUsuarioPorEmailQueNaoExisteNaBase() {
		
		//Cenário
		
		//verificação
		Optional<Usuario> result = usuarioRepositorio.findByEmail("chico@gmail.com");
		
		assertFalse(result.isPresent());
		
	}
	
	public static Usuario criarUsuario() {
		return Usuario
					.builder()
					.nome("Chico da Silva")
					.email("chico@gmail.com")
					.senha("1234")
					.build();
	}
}
