package com.romamoura.financialmanager.service;

import com.romamoura.financialmanager.domain.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface JwtService {
    
    String gerarToken(User user);

    Claims obterClaims(String token) throws ExpiredJwtException;

    boolean isTokenValido(String token);

    String obterLoginUsuario(String token);
}
