package com.romamoura.gestorfinanceiro.api.recursos;

import java.math.BigDecimal;
import java.util.Optional;

import com.romamoura.gestorfinanceiro.api.dto.TokenDTO;
import com.romamoura.gestorfinanceiro.api.dto.UsuarioDTO;
import com.romamoura.gestorfinanceiro.excessoes.ErroAutenticacao;
import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.servicos.JwtService;
import com.romamoura.gestorfinanceiro.servicos.LancamentoServico;
import com.romamoura.gestorfinanceiro.servicos.UsuarioServico;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioRecurso {
	 
    private final UsuarioServico usuarioServico;
	private final LancamentoServico lancamentoServico;
	private final JwtService jwtService;

	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticarUsuario( @RequestBody UsuarioDTO dto){
		
		try {
			Usuario usuarioAutenticado = usuarioServico.autenticar(dto.getEmail(), dto.getSenha());
			String tokenUsuario = jwtService.gerarToken(usuarioAutenticado);

			TokenDTO tokenDto = new TokenDTO(usuarioAutenticado.getNome(), tokenUsuario);
			return ResponseEntity.ok(tokenDto);
		} catch (ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity salvarUsuario( @RequestBody UsuarioDTO dto) {
		
		Usuario usuario =  Usuario.builder()
										.nome(dto.getNome())
										.email(dto.getEmail())
										.senha(dto.getSenha()).build();
		try {
			Usuario usuarioSalvo = usuarioServico.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("{id}/receitas")
	public ResponseEntity obterSaldoReceitas(@PathVariable("id") Long id){
		Optional<Usuario> usuario = usuarioServico.obterPorId(id);

		if(!usuario.isPresent()){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		BigDecimal saldo = lancamentoServico.obterSaldoPorTipoLancamentoReceita(id);
		return ResponseEntity.ok(saldo);

	}

	@GetMapping("{id}/despesas")
	public ResponseEntity obterSaldoDespesas(@PathVariable("id") Long id){
		Optional<Usuario> usuario = usuarioServico.obterPorId(id);

		if(!usuario.isPresent()){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		BigDecimal saldo = lancamentoServico.obterSaldoPorTipoLancamentoDespesa(id);
		return ResponseEntity.ok(saldo);

	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id){
		Optional<Usuario> usuario = usuarioServico.obterPorId(id);

		if(!usuario.isPresent()){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		BigDecimal saldo = lancamentoServico.obterSaldoPorTipoLancamentoUsuario(id);
		return ResponseEntity.ok(saldo);

	}

}
