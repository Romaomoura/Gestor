package com.romamoura.gestorfinanceiro.servicos.implementacoes;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.romamoura.gestorfinanceiro.excessoes.ErroAutenticacao;
import com.romamoura.gestorfinanceiro.excessoes.RegraNegocioExcecao;
import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.modelos.repositorios.UsuarioRepositorio;
import com.romamoura.gestorfinanceiro.servicos.UsuarioServico;

@Service // @Transactional(readOnly = false)
public class UsuarioServicosImplementacao implements UsuarioServico{
	
	private UsuarioRepositorio usuarioRep;
	private PasswordEncoder encoder;
	
	public UsuarioServicosImplementacao(UsuarioRepositorio usuarioRep, PasswordEncoder encoder) {
		super();
		this.usuarioRep = usuarioRep;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {

		Optional<Usuario> usuario = usuarioRep.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}

		boolean verificarSenha = encoder.matches(senha, usuario.get().getSenha());
		
		if(!verificarSenha) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		criptografarSenha(usuario);
		return usuarioRep.save(usuario);
	}

	private void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senhaHash = encoder.encode(senha);
		usuario.setSenha(senhaHash);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = usuarioRep.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioExcecao("Já Existe um usuário cadastrado com esse email.");
		}
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return usuarioRep.findById(id);
	}

	
}
