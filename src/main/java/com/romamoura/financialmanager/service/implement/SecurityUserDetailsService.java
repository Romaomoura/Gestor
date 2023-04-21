package com.romamoura.financialmanager.service.implement;

import com.romamoura.financialmanager.domain.entities.User;
import com.romamoura.financialmanager.domain.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private UserRepository usuarioRep;

    public SecurityUserDetailsService(UserRepository usuarioRep) {
        this.usuarioRep = usuarioRep;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User usuarioEncontrato = usuarioRep
                        .findByEmail(email)
                        .orElseThrow( () -> new UsernameNotFoundException("Email não cadastrado."));

        return  org.springframework.security.core.userdetails.User.builder()
                            .username(usuarioEncontrato.getEmail())
                            .password(usuarioEncontrato.getSenha())
                            .roles("USER")
                            .build();
    }
    
}
