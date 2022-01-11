package com.romamoura.gestorfinanceiro.servicos;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.romamoura.gestorfinanceiro.modelos.entidades.Lancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.StatusLancamento;

public interface LancamentoServico {
    
    Lancamento salvar(Lancamento lancamento);
    Lancamento atualizar(Lancamento lancamento);
    void deletar(Lancamento lancamento);
    List<Lancamento> buscar(Lancamento lancamentoFiltro);
    void atualizarStatus(Lancamento lancamento, StatusLancamento status);
    void validar(Lancamento lancamento);
    Optional<Lancamento> obterPorId(Long id);
    BigDecimal obterSaldoPorTipoLancamentoUsuario(Long id);
    BigDecimal obterSaldoPorTipoLancamentoReceita(Long id);
    BigDecimal obterSaldoPorTipoLancamentoDespesa(Long id);
    

}
