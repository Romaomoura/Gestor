package com.romamoura.gestorfinanceiro.servicos.implementacoes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.romamoura.gestorfinanceiro.modelos.entidades.Usuario;
import com.romamoura.gestorfinanceiro.servicos.JwtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServicesImp implements JwtService {

    @Value("${jwt.expirations}")
    private String expiration;
    @Value("${jwt.key.assignment}")    
    private String keyAssignment;

    @Override
    public String gerarToken(Usuario usuario) {
        long expLong = Long.valueOf(expiration);
        LocalDateTime dataHoraExp = LocalDateTime.now().plusMinutes(expLong);
        Instant instant = dataHoraExp.atZone(ZoneId.systemDefault()).toInstant();
        Date data = Date.from(instant);

        String horaExpiracaoToken = dataHoraExp.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        String token = Jwts.builder()
                            .setExpiration(data)
                            .setSubject(usuario.getEmail())
                            .claim("nome", usuario.getNome())
                            .claim("horaExpiracao", horaExpiracaoToken)
                            .signWith(SignatureAlgorithm.HS512, keyAssignment)
                            .compact();

        return token;
    }

    @Override
    public Claims obterClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                    .setSigningKey(keyAssignment)
                    .parseClaimsJws(token)
                    .getBody();
    }

    @Override
    public boolean isTokenValido(String token) {
        try {
            Claims claims = obterClaims(token);
            Date dataExp = claims.getExpiration();
            LocalDateTime dataHrExpToken = dataExp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            return !LocalDateTime.now().isAfter(dataHrExpToken);
            
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public String obterLoginUsuario(String token) {
        Claims claims = obterClaims(token);
        return claims.getSubject();
    }
    
}
