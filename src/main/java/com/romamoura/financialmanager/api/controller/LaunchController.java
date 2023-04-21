package com.romamoura.financialmanager.api.controller;

import java.util.List;
import java.util.Optional;

import com.romamoura.financialmanager.api.dto.UpdateStatusLaunchDTO;
import com.romamoura.financialmanager.api.dto.LaunchDTO;
import com.romamoura.financialmanager.domain.entities.Launch;
import com.romamoura.financialmanager.domain.entities.User;
import com.romamoura.financialmanager.exception.RuleBusinessException;
import com.romamoura.financialmanager.domain.enums.StatusLaunch;
import com.romamoura.financialmanager.domain.enums.TypeLaunch;
import com.romamoura.financialmanager.service.LaunchService;
import com.romamoura.financialmanager.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lancamentos")
public class LaunchController {
    
    private final LaunchService lancamentoServico;
    private final UserService usuarioServico;

    @PostMapping
    public ResponseEntity salvarLancamento( @RequestBody LaunchDTO launchDto) {
        try {
            Launch entidade = converter(launchDto);
            entidade = lancamentoServico.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RuleBusinessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }  
    }

    @GetMapping("{id}")
    public ResponseEntity BuscarLancamentoPorId(@PathVariable("id") Long id){
      return   lancamentoServico.obterPorId(id).map(launch -> new ResponseEntity(converterDTO(launch), HttpStatus.OK)
                            ).orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND) );
    }

    @PutMapping("{id}")
    public ResponseEntity atualizarLancamento( @PathVariable("id") Long id, @RequestBody LaunchDTO launchDto) {
        
        return lancamentoServico.obterPorId(id).map(entidade -> {
            try {
                Launch launch = converter(launchDto);
                launch.setId(entidade.getId());
                lancamentoServico.atualizar(launch);
                return ResponseEntity.ok(launch);
            }  catch (RuleBusinessException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } 
        }).orElseGet( () -> new ResponseEntity("Launch não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizarStatusLancamento(@PathVariable("id") Long id, @RequestBody UpdateStatusLaunchDTO atualizaStatusDTO ) {
        
        return lancamentoServico.obterPorId(id).map(entidade -> {
            StatusLaunch statusSelecionado = StatusLaunch.valueOf(atualizaStatusDTO.getStatus());
                
            if (statusSelecionado == null) {
                    return ResponseEntity.badRequest().body("Não foi posivel atualizar o status do lancamento, envie um status válido.");
            }
            try {
                         
            entidade.setStatus(statusSelecionado);
            lancamentoServico.atualizar(entidade);
            return ResponseEntity.ok(entidade);
               
            } catch (RuleBusinessException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
           }
        }).orElseGet( () -> new ResponseEntity("Launch não encontrado na base de dados.", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletarLancamento(@PathVariable("id") Long id) {
        return lancamentoServico.obterPorId(id).map( entidade -> {
            lancamentoServico.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet( () -> new ResponseEntity("Launch não encontrado na base de dados.", HttpStatus.BAD_REQUEST) );

    }

    @GetMapping
    public ResponseEntity buscarLancamento(
        @RequestParam(value = "descricao", required = false) String descricao,
        @RequestParam(value = "mes", required = false) Integer mes,
        @RequestParam(value = "ano", required = false) Integer ano,
        @RequestParam(value = "user", required = true) Long idUsuario
    ){
        Launch lancamentoFiltro = new Launch();
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<User> usuario = usuarioServico.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado.");
        }else{
            lancamentoFiltro.setUser(usuario.get());
        }

        List<Launch> launches = lancamentoServico.buscar(lancamentoFiltro);
        return ResponseEntity.ok(launches);

    }

    private LaunchDTO converterDTO(Launch launch) {
        return LaunchDTO.builder()
                          .id(launch.getId())
                          .descricao(launch.getDescricao())
                          .valor(launch.getValor())
                          .mes(launch.getMes())
                          .ano(launch.getAno())
                          .status(launch.getStatus().name())
                          .tipo(launch.getTipo().name())
                          .usuario(launch.getUser().getId())
                          .build();

    }

    private Launch converter(LaunchDTO launchDto) {
        Launch launch = new Launch();

        launch.setId(launchDto.getId());
        launch.setDescricao(launchDto.getDescricao());
        launch.setAno(launchDto.getAno());
        launch.setMes(launchDto.getMes());
        launch.setValor(launchDto.getValor());

       User user = usuarioServico.obterPorId(
            launchDto.getUsuario()).orElseThrow(() -> new RuleBusinessException("User não encontrado para o id informado"));

        launch.setUser(user);
        if (launchDto.getTipo() != null) {
        launch.setTipo(TypeLaunch.valueOf(launchDto.getTipo()));
        }
        if (launchDto.getStatus() != null) {
            launch.setStatus(StatusLaunch.valueOf(launchDto.getStatus()));
        }

        return launch;

    }

}
