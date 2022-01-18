package com.romamoura.gestorfinanceiro.servicos.implementacoes;

import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.modelos.repositorios.UsuarioRepositorio;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private UsuarioRepositorio usuarioRep;

    public SecurityUserDetailsService(UsuarioRepositorio usuarioRep) {
        this.usuarioRep = usuarioRep;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuarioEncontrato = usuarioRep
                        .findByEmail(email)
                        .orElseThrow( () -> new UsernameNotFoundException("Email não cadastrado."));

        return  User.builder()
                            .username(usuarioEncontrato.getEmail())
                            .password(usuarioEncontrato.getSenha())
                            .roles("USER")
                            .build();
    }
    
}
