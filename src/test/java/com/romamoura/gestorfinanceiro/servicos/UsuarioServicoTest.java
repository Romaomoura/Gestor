package com.romamoura.gestorfinanceiro.servicos;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.romamoura.gestorfinanceiro.excessoes.ErroAutenticacao;
import com.romamoura.gestorfinanceiro.excessoes.RegraNegocioExcecao;
import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.modelos.repositorios.UsuarioRepositorio;
import com.romamoura.gestorfinanceiro.servicos.implementacoes.UsuarioServicosImplementacao;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServicoTest {
	
	@SpyBean
	UsuarioServicosImplementacao servicoUsuario;	
	
	@MockBean
	UsuarioRepositorio repositorioUsuario; 
	
	//@BeforeEach
	//public void setUp() {
		
	//	servicoUsuario = Mockito.spy(UsuarioServicosImplementacao.class);
	//  servicoUsuario = new UsuarioServicosImplementacao(repositorioUsuario);
	//}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		Assertions.assertDoesNotThrow(() -> {
			//Cenário
			String email = "emailqualquer@gmail.com";
			String senha = "senha";
			
			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
			Mockito.when(repositorioUsuario.findByEmail(email)).thenReturn(Optional.of(usuario));
			
			//Ação
			Usuario result =  servicoUsuario.autenticar(email, senha);
			
			//Verificação
			System.out.println(">>>>>>>>>>>>>>>" + result);
			assertNotNull(result);
	
		});
	}
	
	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertDoesNotThrow(() -> {
			//Cenário
			Mockito.doNothing().when(servicoUsuario).validarEmail(Mockito.anyString());
			Usuario usuario = Usuario.builder()
					  		  .id(1l)
					          .nome("romao")
					          .email("romao@gmail.com")
					          .senha("1234")
					          .build();
			Mockito.when(repositorioUsuario.save(Mockito.any(Usuario.class))).thenReturn(usuario);
						
			//Ação
			
			Usuario usuarioSalvo = servicoUsuario.salvarUsuario(new Usuario());
			
			//Verificação
			
			assertNotNull(usuarioSalvo.getId().equals(1l));
			assertNotNull(usuarioSalvo.getNome().equals("romao"));
			assertNotNull(usuarioSalvo.getEmail().equals("romao@gmail.com"));
			assertNotNull(usuarioSalvo.getSenha().equals("1234"));
			
		});
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioExcecao.class, () -> {
			//Cenário
			
			String email = "joao@gmail.com" ;
			
			Usuario usuario = Usuario.builder()
					  	      .email(email)
					          .build();
			Mockito.doThrow(RegraNegocioExcecao.class).when(servicoUsuario).validarEmail(email);
								
			//Ação
			
			servicoUsuario.salvarUsuario(usuario);
			
			//Verificação
			
			Mockito.verify(repositorioUsuario, Mockito.never()).save(usuario);
			
		});
	}
	
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioComOEmailInformado() {

		//Cenário
		Mockito.when(repositorioUsuario.findByEmail(anyString())).thenReturn(Optional.empty());
		
		//Ação
		 
		Throwable e = Assertions.assertThrows(ErroAutenticacao.class, () -> servicoUsuario.autenticar("email@email.com", "1234"));
		
		//Verificando
		Assertions.assertEquals("Usuário não encontrado para o email informado.", e.getMessage());
			
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioComASenhaInformada() {
		 
		//Cenário
		String email = "emailqualquer@gmail.com";
		String senha = "senha";
		Usuario usuario = Usuario.builder().email(email).senha(senha).build();
		Mockito.when(repositorioUsuario.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//Ação
		
		Throwable e = Assertions.assertThrows(ErroAutenticacao.class, () -> servicoUsuario.autenticar(email, "1234"));
		
		//Verificando
		Assertions.assertEquals("Senha inválida.", e.getMessage());
				
	}
	
	
	@Test()
	public void deveValidarSemErroEmail() {
		Assertions.assertDoesNotThrow(() -> {
			
			//Cenario
			when(repositorioUsuario.existsByEmail(Mockito.anyString())).thenReturn(false);
			
			//Ação
			 servicoUsuario.validarEmail("emailqualquer@gmail.com");
			
			//Verificação 
			
			
		});
	
	}
	
	@Test()
	public void deveLancarErroAoValidarEmail() {
		Assertions.assertThrows(RegraNegocioExcecao.class, () -> {
			
			//Cenario
			when(repositorioUsuario.existsByEmail(Mockito.anyString())).thenReturn(true);
			
			//Ação
			servicoUsuario.validarEmail("romaolindodemais@gmail.com");
			
			
		});
	
	}
	
}
