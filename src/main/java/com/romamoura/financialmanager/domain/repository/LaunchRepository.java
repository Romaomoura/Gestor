package com.romamoura.financialmanager.domain.repository;

import com.romamoura.financialmanager.domain.entities.Launch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import com.romamoura.financialmanager.domain.enums.StatusLaunch;
import com.romamoura.financialmanager.domain.enums.TypeLaunch;

@Repository
public interface LaunchRepository extends JpaRepository<Launch, Long>{

    @Query(value = "SELECT SUM(l.valor) FROM Launch l JOIN l.user u WHERE u.id = :idUsuario AND l.tipo = :tipo and l.status = :status GROUP BY u")
    BigDecimal obterSaldoPorTipoLancamentoUsuarioEStatus( 
                                    @Param("idUsuario") Long idUsuario,  
                                    @Param("tipo") TypeLaunch tipo,
                                    @Param("status") StatusLaunch status);

}
