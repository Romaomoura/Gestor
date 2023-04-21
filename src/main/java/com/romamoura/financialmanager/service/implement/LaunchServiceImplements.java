package com.romamoura.financialmanager.service.implement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.romamoura.financialmanager.domain.entities.Launch;
import com.romamoura.financialmanager.exception.RuleBusinessException;
import com.romamoura.financialmanager.domain.enums.StatusLaunch;
import com.romamoura.financialmanager.domain.enums.TypeLaunch;
import com.romamoura.financialmanager.domain.repository.LaunchRepository;
import com.romamoura.financialmanager.service.LaunchService;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaunchServiceImplements implements LaunchService {

    private LaunchRepository lancamentoRep;

    public LaunchServiceImplements(LaunchRepository lancamentoRep) {
        this.lancamentoRep = lancamentoRep;
    }

    @Override
    @Transactional
    public Launch salvar(Launch launch) {
        validar(launch);
        launch.setStatus(StatusLaunch.PENDENTE);
        return lancamentoRep.save(launch);
    }

    @Override
    @Transactional
    public Launch atualizar(Launch launch) {
        Objects.requireNonNull(launch.getId());
        validar(launch);
        return lancamentoRep.save(launch);
    }

    @Override
    @Transactional
    public void deletar(Launch launch) {
        Objects.requireNonNull(launch.getId());
        lancamentoRep.delete(launch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Launch> buscar(Launch lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching()
                                     .withIgnoreCase()
                                     .withStringMatcher(StringMatcher.CONTAINING));
        return lancamentoRep.findAll(example);
    }

    @Override
    public void atualizarStatus(Launch launch, StatusLaunch status) {
        launch.setStatus(status);
        atualizar(launch);
        
    }

    @Override
    public void validar(Launch launch) {
        
        if(launch.getDescricao() == null || launch.getDescricao().trim().equals("")){
            throw new RuleBusinessException("Informe uma Descrição válida.");
        }

        if(launch.getMes() == null || launch.getMes() < 1 || launch.getMes() > 12){
            throw new RuleBusinessException("Informe um Mês válido.");
        }

        if(launch.getAno() == null || launch.getAno().toString().length() != 4){
            throw new RuleBusinessException("Informe um Ano válido.");
        }
        
        if(launch.getUser() == null || launch.getUser().getId() == null){
            throw new RuleBusinessException("Informe um Usuário.");
        }

        if(launch.getValor() == null || launch.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RuleBusinessException("Informe um Valor válido.");
        }

        if(launch.getTipo() == null){
            throw new RuleBusinessException("Informe um Tipo de launch.");
        }
        

    }

    @Override
    public Optional<Launch> obterPorId(Long id) {
        return  lancamentoRep.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorTipoLancamentoReceita(Long id) {
        
        BigDecimal receitas = lancamentoRep.obterSaldoPorTipoLancamentoUsuarioEStatus(id, TypeLaunch.RECEITA, StatusLaunch.EFETIVADO);
        
        if(receitas == null) {
            receitas = BigDecimal.ZERO;
        }


        return receitas;
    }


    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorTipoLancamentoDespesa(Long id) {
        
        BigDecimal despesas = lancamentoRep.obterSaldoPorTipoLancamentoUsuarioEStatus(id, TypeLaunch.DESPESA, StatusLaunch.EFETIVADO);

        if(despesas == null) {
            despesas = BigDecimal.ZERO;
        }

        return despesas;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorTipoLancamentoUsuario(Long id) {
        
        BigDecimal receitas = lancamentoRep.obterSaldoPorTipoLancamentoUsuarioEStatus(id, TypeLaunch.RECEITA, StatusLaunch.EFETIVADO);
        BigDecimal despesas = lancamentoRep.obterSaldoPorTipoLancamentoUsuarioEStatus(id, TypeLaunch.DESPESA, StatusLaunch.EFETIVADO);
        
        if(receitas == null) {
            receitas = BigDecimal.ZERO;
        }

        if(despesas == null) {
            despesas = BigDecimal.ZERO;
        }

        return receitas.subtract(despesas);
    }
    
}
