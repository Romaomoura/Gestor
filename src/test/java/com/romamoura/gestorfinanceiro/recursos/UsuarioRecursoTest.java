package com.romamoura.gestorfinanceiro.recursos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.romamoura.gestorfinanceiro.api.dto.UsuarioDTO;
import com.romamoura.gestorfinanceiro.excessoes.ErroAutenticacao;
import com.romamoura.gestorfinanceiro.excessoes.RegraNegocioExcecao;
import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.servicos.LancamentoServico;
import com.romamoura.gestorfinanceiro.servicos.UsuarioServico;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class UsuarioRecursoTest {

    static final String API = "/api/usuarios";

    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioServico usuarioServico;

    @MockBean
    LancamentoServico lancamentoServico;

    @Test
    public void deveAutenticarUmUsuario() throws Exception{
        //Cenario
        String email = "romaomoura@gmail.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().email(email).senha(senha).build();

        Mockito.when(usuarioServico.autenticar(email, senha)).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        //Execução //Verificação

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                                    .post(API.concat("/autenticar"))
                                                                    .accept(JSON)
                                                                    .contentType(JSON).content(json);

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
            .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

        } 

    @Test
    public void deveObterUmBadRequestAoTentarSeAutenticar() throws Exception{
        //Cenario
        String email = "romaomoura@gmail.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

        Mockito.when(usuarioServico.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        //Execução //Verificação

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                                    .post(API.concat("/autenticar"))
                                                                    .accept(JSON)
                                                                    .contentType(JSON).content(json);

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        } 
        
    @Test
    public void deveCriarUmNovoUsuario() throws Exception{
        //Cenario
        String email = "romaomoura@gmail.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
        Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

        Mockito.when(usuarioServico.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        //Execução //Verificação

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                                    .post(API)
                                                                    .accept(JSON)
                                                                    .contentType(JSON).content(json);

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
            .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

        }

    @Test
    public void deveObterUmBadRequestAoCriarUmUsuarioInvalido() throws Exception{
        //Cenario
        String email = "romaomoura@gmail.com";
        String senha = "1234";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

        Mockito.when(usuarioServico.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioExcecao.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        //Execução //Verificação

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                                    .post(API)
                                                                    .accept(JSON)
                                                                    .contentType(JSON).content(json);

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        } 
    
}
