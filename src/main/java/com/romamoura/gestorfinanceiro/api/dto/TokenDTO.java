package com.romamoura.gestorfinanceiro.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TokenDTO {

    private String nome;
    private String Token;
    
}
