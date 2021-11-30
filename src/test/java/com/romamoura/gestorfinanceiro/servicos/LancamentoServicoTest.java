package com.romamoura.gestorfinanceiro.servicos;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.romamoura.gestorfinanceiro.excessoes.RegraNegocioExcecao;
import com.romamoura.gestorfinanceiro.modelos.entidades.Lancamento;
import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.modelos.enums.StatusLancamento;
import com.romamoura.gestorfinanceiro.modelos.repositorios.LancamentoRepositorio;
import com.romamoura.gestorfinanceiro.modelos.repositorios.LancamentoRepositorioTest;
import com.romamoura.gestorfinanceiro.servicos.implementacoes.LancamentoServicoImplementacao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServicoTest {
    
    @SpyBean
    LancamentoServicoImplementacao lancamentoServico;

    @MockBean
    LancamentoRepositorio lancamentoRepositorio;

    @Test
    public void deveSalvarUmLancacameto() {
        Assertions.assertDoesNotThrow(() -> {
			//Cenário
			Lancamento lancamentoASalvar = LancamentoRepositorioTest.criarLancamento();
			Mockito.doNothing().when(lancamentoServico).validar(lancamentoASalvar);
            
			Lancamento lancamentoSalvo = LancamentoRepositorioTest.criarLancamento();
            lancamentoSalvo.setId(1l);
			Mockito.when(lancamentoRepositorio.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
			
            //Ação
			Lancamento lancamento = lancamentoServico.salvar(lancamentoASalvar);
			
			//Verificação
			
			assertNotNull(lancamento.getId().equals(lancamentoSalvo.getId()));
			assertNotNull(lancamento.getStatus().equals(StatusLancamento.PENDENTE));
			
		});

    }

    @Test
    public void naoDeveSalvarUmLancacametoQuandoHouverErroDeValidacao() {
        //Cenário
			Lancamento lancamentoASalvar = LancamentoRepositorioTest.criarLancamento();
            Mockito.doThrow( RegraNegocioExcecao.class ).when(lancamentoServico).validar(lancamentoASalvar);

        //Ação
        Throwable e = Assertions.assertThrows( RegraNegocioExcecao.class, () -> lancamentoServico.salvar(lancamentoASalvar));

        Assertions.assertNotNull(e);

        //Verificando
        Mockito.verify(lancamentoRepositorio, Mockito.never()).save(lancamentoASalvar);

    }

    @Test
    public void deveAtualizarUmLancacameto() {
        Assertions.assertDoesNotThrow(() -> {
			//Cenário
			Lancamento lancamentoSalvo = LancamentoRepositorioTest.criarLancamento();
            lancamentoSalvo.setId(1l);
            lancamentoSalvo.setStatus(StatusLancamento.CANCELADO);
			
			Mockito.doNothing().when(lancamentoServico).validar(lancamentoSalvo);
			Mockito.when(lancamentoRepositorio.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
            
            //Ação
			Lancamento lancamento = lancamentoServico.atualizar(lancamentoSalvo);
			
			//Verificação
			Mockito.verify(lancamentoRepositorio, Mockito.times(1)).save(lancamento);
		});

    }

    @Test
    public void deveLancarUmErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        //Cenário
		Lancamento lancamentoASalvar = LancamentoRepositorioTest.criarLancamento();

        //Ação
        Throwable e = Assertions.assertThrows( NullPointerException.class, () -> lancamentoServico.atualizar(lancamentoASalvar));

        Assertions.assertNotNull(e);

        //Verificando
        Mockito.verify(lancamentoRepositorio, Mockito.never()).save(lancamentoASalvar);

    }

    @Test
    public void deveDeletarUmLancacameto() {
        Assertions.assertDoesNotThrow(() -> {
			//Cenário
			Lancamento lancamento = LancamentoRepositorioTest.criarLancamento();
            lancamento.setId(1l);
	
            //Ação
			lancamentoServico.deletar(lancamento);
			
			//Verificação
			Mockito.verify( lancamentoRepositorio ).delete( lancamento );
		});
    }

    @Test
    public void deveLancarUmErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
        //Cenário
		Lancamento lancamento = LancamentoRepositorioTest.criarLancamento();

        //Ação
        Throwable e = Assertions.assertThrows( NullPointerException.class, () -> lancamentoServico.deletar(lancamento));

        //Verificando
        Assertions.assertNotNull(e);
        Mockito.verify(lancamentoRepositorio, Mockito.never()).delete(lancamento);

    }

    @Test
    public void deveBuscarUmLancamentos() {
        //cenario
        Lancamento lancamento = LancamentoRepositorioTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento); 
        Mockito.when( lancamentoRepositorio.findAll(Mockito.any(Example.class)) ).thenReturn(lista);

        //Ação
        List<Lancamento> buscar = lancamentoServico.buscar(lancamento);

        //Verificação
        assertTrue(buscar.size() == 1 && buscar.contains(lancamento));

    }

    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
        //cenario
        Lancamento lancamento = LancamentoRepositorioTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento statusNovo = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(lancamentoServico).atualizar(lancamento);
        
        //Acão
        lancamentoServico.atualizarStatus(lancamento, statusNovo);

        //Verificação
        assertTrue(lancamento.getStatus().equals(statusNovo));
        Mockito.verify(lancamentoServico).atualizar(lancamento);

    }

    @Test
    public void deveBuscarUmLancamentoPorId() {
        Long id = 1l;
        Lancamento lancamento = LancamentoRepositorioTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(lancamentoRepositorio.findById(id)).thenReturn(Optional.of(lancamento));
        
        Optional<Lancamento> resultado = lancamentoServico.obterPorId(id);

        assertTrue(resultado.isPresent());

    }

    @Test
    public void deveRetornarVazioQuandoLancamentoNaoExistir() {
        Long id = 1l;
        Lancamento lancamento = LancamentoRepositorioTest.criarLancamento();
        lancamento.setId(id);

        Mockito.when(lancamentoRepositorio.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = lancamentoServico.obterPorId(id);

        assertFalse(resultado.isPresent());

    }

    @Test
    public void develancarErrosAoValidarlancamento() {
        Lancamento lancamento= new Lancamento();

        //Ação
        Throwable e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe uma Descrição válida.", e.getMessage());

        lancamento.setDescricao("");

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe uma Descrição válida.", e.getMessage());

        lancamento.setDescricao("Salário");

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Mês válido.", e.getMessage());

        lancamento.setMes(0);

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Mês válido.", e.getMessage());

        lancamento.setMes(13);

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Mês válido.", e.getMessage());

        lancamento.setMes(1);

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Ano válido.", e.getMessage());

        lancamento.setAno(20212);

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Ano válido.", e.getMessage());

        lancamento.setAno(2021);

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Usuário.", e.getMessage());

        lancamento.setUsuario(new Usuario());

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Usuário.", e.getMessage());

        lancamento.setUsuario(new Usuario());
        lancamento.getUsuario().setId(1l);

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Valor válido.", e.getMessage());

        lancamento.setValor(BigDecimal.ZERO);

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Valor válido.", e.getMessage());

        lancamento.setValor(BigDecimal.valueOf(10));

        e = Assertions.assertThrows(RegraNegocioExcecao.class, () -> lancamentoServico.validar(lancamento));
		Assertions.assertEquals("Informe um Tipo de lancamento.", e.getMessage());
        
    }

}
