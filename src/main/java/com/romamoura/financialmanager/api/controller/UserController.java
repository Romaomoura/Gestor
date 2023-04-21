package com.romamoura.financialmanager.api.controller;

import java.math.BigDecimal;
import java.util.Optional;

import com.romamoura.financialmanager.api.dto.TokenDTO;
import com.romamoura.financialmanager.api.dto.UserDTO;
import com.romamoura.financialmanager.domain.entities.User;
import com.romamoura.financialmanager.exception.AuthenticationError;
import com.romamoura.financialmanager.service.JwtService;
import com.romamoura.financialmanager.service.LaunchService;
import com.romamoura.financialmanager.service.UserService;

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
public class UserController {
	 
    private final UserService userService;
	private final LaunchService launchService;
	private final JwtService jwtService;

	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticarUsuario( @RequestBody UserDTO dto){
		
		try {
			User usuarioAutenticado = userService.autenticar(dto.getEmail(), dto.getSenha());
			String tokenUsuario = jwtService.gerarToken(usuarioAutenticado);

			TokenDTO tokenDto = new TokenDTO(usuarioAutenticado.getNome(), tokenUsuario);
			return ResponseEntity.ok(tokenDto);
		} catch (AuthenticationError e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity salvarUsuario( @RequestBody UserDTO dto) {
		
		User user =  User.builder()
										.nome(dto.getNome())
										.email(dto.getEmail())
										.senha(dto.getSenha()).build();
		try {
			User usuarioSalvo = userService.salvarUsuario(user);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@GetMapping("{id}/receitas")
	public ResponseEntity obterSaldoReceitas(@PathVariable("id") Long id){
		Optional<User> usuario = userService.obterPorId(id);

		if(!usuario.isPresent()){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		BigDecimal saldo = launchService.obterSaldoPorTipoLancamentoReceita(id);
		return ResponseEntity.ok(saldo);

	}

	@GetMapping("{id}/despesas")
	public ResponseEntity obterSaldoDespesas(@PathVariable("id") Long id){
		Optional<User> usuario = userService.obterPorId(id);

		if(!usuario.isPresent()){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		BigDecimal saldo = launchService.obterSaldoPorTipoLancamentoDespesa(id);
		return ResponseEntity.ok(saldo);

	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id){
		Optional<User> usuario = userService.obterPorId(id);

		if(!usuario.isPresent()){
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		BigDecimal saldo = launchService.obterSaldoPorTipoLancamentoUsuario(id);
		return ResponseEntity.ok(saldo);

	}

}
