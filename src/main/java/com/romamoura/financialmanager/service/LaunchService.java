package com.romamoura.financialmanager.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.romamoura.financialmanager.domain.entities.Launch;
import com.romamoura.financialmanager.domain.enums.StatusLaunch;

public interface LaunchService {
    
    Launch salvar(Launch launch);
    Launch atualizar(Launch launch);
    void deletar(Launch launch);
    List<Launch> buscar(Launch lancamentoFiltro);
    void atualizarStatus(Launch launch, StatusLaunch status);
    void validar(Launch launch);
    Optional<Launch> obterPorId(Long id);
    BigDecimal obterSaldoPorTipoLancamentoUsuario(Long id);
    BigDecimal obterSaldoPorTipoLancamentoReceita(Long id);
    BigDecimal obterSaldoPorTipoLancamentoDespesa(Long id);
    

}
