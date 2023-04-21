package com.romamoura.financialmanager.service;

import java.util.Optional;

import com.romamoura.financialmanager.domain.entities.User;

public interface UserService {
	
	User autenticar(String email, String senha);
	
	User salvarUsuario(User user);
	
	void validarEmail(String email);
	
	Optional<User> obterPorId(Long id);

}
