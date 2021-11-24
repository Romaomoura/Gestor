package com.romamoura.gestorfinanceiro.servicos;

import java.util.Optional;

import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;

public interface UsuarioServico {
	
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);

}
