package com.romamoura.gestorfinanceiro.servicos.implementacoes;

import java.util.Optional;

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
	
	public UsuarioServicosImplementacao(UsuarioRepositorio usuarioRep) {
		super();
		this.usuarioRep = usuarioRep;
	}

	@Override
	public Usuario autenticar(String email, String senha) {

		Optional<Usuario> usuario = usuarioRep.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return usuarioRep.save(usuario);
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
