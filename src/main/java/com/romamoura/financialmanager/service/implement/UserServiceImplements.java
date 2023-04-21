package com.romamoura.financialmanager.service.implement;

import java.util.Optional;

import com.romamoura.financialmanager.domain.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romamoura.financialmanager.exception.AuthenticationError;
import com.romamoura.financialmanager.exception.RuleBusinessException;
import com.romamoura.financialmanager.domain.repository.UserRepository;
import com.romamoura.financialmanager.service.UserService;

@Service // @Transactional(readOnly = false)
public class UserServiceImplements implements UserService {
	
	private UserRepository repository;
	private PasswordEncoder encoder;
	
	public UserServiceImplements(UserRepository repository, PasswordEncoder encoder) {
		super();
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public User autenticar(String email, String senha) {

		Optional<User> user = repository.findByEmail(email);
		
		if(!user.isPresent()) {
			throw new AuthenticationError("Usuário não encontrado para o email informado.");
		}

		boolean verificarSenha = encoder.matches(senha, user.get().getSenha());
		
		if(!verificarSenha) {
			throw new AuthenticationError("Senha inválida.");
		}
		
		return user.get();
	}

	@Override
	@Transactional
	public User salvarUsuario(User user) {
		validarEmail(user.getEmail());
		criptografarSenha(user);
		return repository.save(user);
	}

	private void criptografarSenha(User user) {
		String senha = user.getSenha();
		String senhaHash = encoder.encode(senha);
		user.setSenha(senhaHash);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RuleBusinessException("Já Existe um usuário cadastrado com esse email.");
		}
	}

	@Override
	public Optional<User> obterPorId(Long id) {
		return repository.findById(id);
	}

	
}
