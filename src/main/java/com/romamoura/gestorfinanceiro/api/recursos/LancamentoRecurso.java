package com.romamoura.gestorfinanceiro.api.recursos;

import java.util.List;
import java.util.Optional;

import com.romamoura.gestorfinanceiro.api.dto.AtualizaStatusLancamentoDTO;
import com.romamoura.gestorfinanceiro.api.dto.LancamentoDTO;
import com.romamoura.gestorfinanceiro.excessoes.RegraNegocioExcecao;
import com.romamoura.gestorfinanceiro.modelos.entidades.Lancamento;
import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.modelos.enums.StatusLancamento;
import com.romamoura.gestorfinanceiro.modelos.enums.TipoLancamento;
import com.romamoura.gestorfinanceiro.servicos.LancamentoServico;
import com.romamoura.gestorfinanceiro.servicos.UsuarioServico;

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
public class LancamentoRecurso {
    
    private final LancamentoServico lancamentoServico;
    private final UsuarioServico usuarioServico;

   /*  public LancamentoRecurso(LancamentoServico lancamentoServico, UsuarioServico usuarioServico){
        this.lancamentoServico = lancamentoServico;
        this.usuarioServico = usuarioServico;
    } */

    @PostMapping
    public ResponseEntity salvarLancamento( @RequestBody LancamentoDTO lancamentoDTO) {
        try {
            Lancamento entidade = converter(lancamentoDTO);
            entidade = lancamentoServico.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);
        } catch (RegraNegocioExcecao e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }  
    }

    @PutMapping("{id}")
    public ResponseEntity atualizarLancamento( @PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO ) {
        
        return lancamentoServico.obterPorId(id).map(entidade -> {
            try {
                Lancamento lancamento = converter(lancamentoDTO);
                lancamento.setId(entidade.getId());
                lancamentoServico.atualizar(lancamento);
                return ResponseEntity.ok(lancamento);
            }  catch (RegraNegocioExcecao e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            } 
        }).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST)); 
    }

    @PutMapping("{id}/atualizar-status")
    public ResponseEntity atualizarStatusLancamento(@PathVariable("id") Long id, @RequestBody AtualizaStatusLancamentoDTO atualizaStatusDTO ) {
        
        return lancamentoServico.obterPorId(id).map(entidade -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(atualizaStatusDTO.getStatus());
                
            if (statusSelecionado == null) {
                    return ResponseEntity.badRequest().body("Não foi posivel atualizar o status do lancamento, envie um status válido.");
            }
            try {
                         
            entidade.setStatus(statusSelecionado);
            lancamentoServico.atualizar(entidade);
            return ResponseEntity.ok(entidade);
               
            } catch (RegraNegocioExcecao e) {
                return ResponseEntity.badRequest().body(e.getMessage());
           }
        }).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST)); 
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletarLancamento(@PathVariable("id") Long id) {
        return lancamentoServico.obterPorId(id).map( entidade -> {
            lancamentoServico.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet( () -> new ResponseEntity("Lancamento não encontrado na base de dados.", HttpStatus.BAD_REQUEST) ); 

    }

    @GetMapping
    public ResponseEntity buscarLancamento(
        @RequestParam(value = "descricao", required = false) String descricao,
        @RequestParam(value = "mes", required = false) Integer mes,
        @RequestParam(value = "ano", required = false) Integer ano,
        @RequestParam(value = "usuario", required = true) Long idUsuario
    ){
        Lancamento lancamentoFiltro = new Lancamento(); 
        lancamentoFiltro.setDescricao(descricao);
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioServico.obterPorId(idUsuario);
        if (!usuario.isPresent()) {
            return ResponseEntity.badRequest().body("Não foi possível realizar a consulta. Usuário não encontrado.");
        }else{
            lancamentoFiltro.setUsuario(usuario.get());
        }

        List<Lancamento> lancamentos = lancamentoServico.buscar(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);

    }

    private Lancamento converter(LancamentoDTO lancamentoDTO) {
        Lancamento lancamento = new Lancamento();

        lancamento.setId(lancamentoDTO.getId());
        lancamento.setDescricao(lancamentoDTO.getDescricao());
        lancamento.setAno(lancamentoDTO.getAno());
        lancamento.setMes(lancamentoDTO.getMes());
        lancamento.setValor(lancamentoDTO.getValor());

       Usuario usuario = usuarioServico.obterPorId(
            lancamentoDTO.getUsuario()).orElseThrow(() -> new RegraNegocioExcecao("Usuario não encontrado para o id informado"));

        lancamento.setUsuario(usuario);
        if (lancamentoDTO.getTipo() != null) {
        lancamento.setTipo(TipoLancamento.valueOf(lancamentoDTO.getTipo()));
        }
        if (lancamentoDTO.getStatus() != null) {
            lancamento.setStatus(StatusLancamento.valueOf(lancamentoDTO.getStatus()));
        }

        return lancamento;

    }

}
