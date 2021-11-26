package com.romamoura.gestorfinanceiro.servicos;

import static org.junit.Assert.assertNotNull;

import com.romamoura.gestorfinanceiro.modelos.entidades.Lancamento;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServicoTest {
    
    @SpyBean
    LancamentoServicoImplementacao lancamentoServico;

    @MockBean
    LancamentoRepositorio lancaementoRep;

    @Test
    public void deveSalvarUmLancacameto() {
        Assertions.assertDoesNotThrow(() -> {
			//Cenário
			Lancamento lancamentoASalvar = LancamentoRepositorioTest.criarLancamento();
			Mockito.doNothing().when(lancamentoServico).validar(lancamentoASalvar);
            
			Lancamento lancamentoSalvo = LancamentoRepositorioTest.criarLancamento();
            lancamentoSalvo.setId(1l);
			Mockito.when(lancaementoRep.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
			
            //Ação
			Lancamento lancamento = lancamentoServico.salvar(lancamentoASalvar);
			
			//Verificação
			
			assertNotNull(lancamento.getId().equals(lancamentoSalvo.getId()));
			assertNotNull(lancamento.getStatus().equals(StatusLancamento.PENDENTE));
			
		});

    }

    @Test
    public void naoDeveSalvarUmLancacametoQuandoHouverErroDeValidacao() {

    }
}
