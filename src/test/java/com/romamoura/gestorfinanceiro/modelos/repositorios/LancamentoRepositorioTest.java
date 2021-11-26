package com.romamoura.gestorfinanceiro.modelos.repositorios;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

import com.romamoura.gestorfinanceiro.modelos.entidades.Lancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.StatusLancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.TipoLancamento;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LancamentoRepositorioTest {

    @Autowired
    LancamentoRepositorio lancamentoRep;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamento = criarEPersistirUmLancamento();

        //Verificação
		assertNotNull(lancamento.getId());
    }

    @Test
    public void deveDeletarUmlancamento(){
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        lancamentoRep.delete(lancamento);

        Lancamento lancamentoDeletado = entityManager.find(Lancamento.class, lancamento.getId());

        //Verificação
        assertNull(lancamentoDeletado);
    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento.setAno(2020);
        lancamento.setDescricao("Atualizando o lancamento");
        lancamento.setStatus(StatusLancamento.EFETIVADO);

        lancamentoRep.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());


        assertNotNull(lancamentoAtualizado.getAno().equals(2020));
        assertNotNull(lancamentoAtualizado.getDescricao().equals("Atualizando o lancamento"));
        assertNotNull(lancamentoAtualizado.getStatus().equals(StatusLancamento.EFETIVADO));

    }

    @Test
    public void deveBuscarUmLancamentoPorId(){
        Lancamento lancamento = criarEPersistirUmLancamento();

        Optional<Lancamento> lancamentoEncontrado = lancamentoRep.findById(lancamento.getId());

        assertTrue(lancamentoEncontrado.isPresent());

    }

    public static Lancamento criarLancamento() {
		return  Lancamento.builder()
                                    .ano(2019)
                                    .mes(1)
                                    .descricao("Lancamento qualquer ")
                                    .valor(BigDecimal.valueOf(20))
                                    .tipo(TipoLancamento.RECEITA)
                                    .status(StatusLancamento.PENDENTE)
                                    .dataCadastro(Calendar.getInstance())
                                    .build();
    
    }

    public Lancamento criarEPersistirUmLancamento() {
        Lancamento lancamento = criarLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }

}