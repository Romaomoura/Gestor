package com.romamoura.financialmanager.config;

import java.util.Arrays;
import java.util.List;

import com.romamoura.financialmanager.api.JwtTokenFilter;
import com.romamoura.financialmanager.service.JwtService;
import com.romamoura.financialmanager.service.implement.SecurityUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }

    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter(jwtService, userDetailsService);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter>  corsFilter(){

        List<String> all = Arrays.asList("*");
        

        CorsConfiguration  config = new CorsConfiguration();
        config.setAllowedMethods(all);
        config.setAllowedOriginPatterns(all);
        config.setAllowedHeaders(all);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        CorsFilter corsFilter = new CorsFilter(source);

        FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<CorsFilter>(corsFilter);

        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return filter;
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
        .and()
            .addFilterBefore( jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class );
    }   
}
