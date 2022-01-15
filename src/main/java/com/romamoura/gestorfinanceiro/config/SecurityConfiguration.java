package com.romamoura.gestorfinanceiro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        String senhaHash = passwordEncoder().encode("Romao1807");

        auth.inMemoryAuthentication()
                    .withUser("gfinc")
                    .password(senhaHash)
                    .roles("USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
        .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll()
            .antMatchers(HttpMethod.POST, "/api/usuarios").permitAll() //Pode-se definir quem tem autorização para acessar a rota com roles (Perfis). hasRole/ hasAuthority entre outros tipos de permissões.
            .anyRequest().authenticated()
        .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Não deixar salvar sessão de usuário em coockies.
        .and().httpBasic();
    }
    
}
