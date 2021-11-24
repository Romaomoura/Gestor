package com.romamoura.gestorfinanceiro.servicos.implementacoes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.romamoura.gestorfinanceiro.excessoes.RegraNegocioExcecao;
import com.romamoura.gestorfinanceiro.modelos.entidades.Lancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.StatusLancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.TipoLancamento;
import com.romamoura.gestorfinanceiro.modelos.repositorios.LancamentoRepositorio;
import com.romamoura.gestorfinanceiro.servicos.LancamentoServico;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LancamentoServicoImplementacao implements LancamentoServico {

    private LancamentoRepositorio lancamentoRep;

    public LancamentoServicoImplementacao (LancamentoRepositorio lancamentoRep) {
        this.lancamentoRep = lancamentoRep;
    }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return lancamentoRep.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return lancamentoRep.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        lancamentoRep.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
                                     .withIgnoreCase()
                                     .withStringMatcher(StringMatcher.CONTAINING));
        return lancamentoRep.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
        lancamento.setStatus(status);
        atualizar(lancamento);
        
    }

    @Override
    public void validar(Lancamento lancamento) {
        
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioExcecao("Informe uma Descrição válida.");
        }

        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12){
            throw new RegraNegocioExcecao("Informe um Mês válido.");
        }

        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4){
            throw new RegraNegocioExcecao("Informe um Ano válido.");
        }
        
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraNegocioExcecao("Informe um Usuário");
        }

        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RegraNegocioExcecao("Informe um Valor válido.");
        }

        if(lancamento.getTipo() == null){
            throw new RegraNegocioExcecao("Informe um Tipo de lancamento.");
        }
        

    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return  lancamentoRep.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorTipoLancamentoUsuario(Long id) {
        
        BigDecimal receitas = lancamentoRep.obterSaldoPorTipoLancamentoUsuario(id, TipoLancamento.RECEITA);
        BigDecimal despesas = lancamentoRep.obterSaldoPorTipoLancamentoUsuario(id, TipoLancamento.DESPESA);
        
        if(receitas == null) {
            receitas = BigDecimal.ZERO;
        }

        if(despesas == null) {
            despesas = BigDecimal.ZERO;
        }

        return receitas.subtract(despesas);
    }
    
}
