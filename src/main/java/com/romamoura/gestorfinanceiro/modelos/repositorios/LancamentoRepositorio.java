package com.romamoura.gestorfinanceiro.modelos.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import com.romamoura.gestorfinanceiro.modelos.entidades.Lancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.StatusLancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.TipoLancamento;

@Repository
public interface LancamentoRepositorio extends JpaRepository<Lancamento, Long>{

    @Query(value = "SELECT SUM(l.valor) FROM Lancamento l JOIN l.usuario u WHERE u.id = :idUsuario AND l.tipo = :tipo and l.status = :status GROUP BY u")
    BigDecimal obterSaldoPorTipoLancamentoUsuarioEStatus( 
                                    @Param("idUsuario") Long idUsuario,  
                                    @Param("tipo") TipoLancamento tipo,
                                    @Param("status") StatusLancamento status);

}
