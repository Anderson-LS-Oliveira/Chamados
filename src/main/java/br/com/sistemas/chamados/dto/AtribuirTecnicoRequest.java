package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotNull;

public record AtribuirTecnicoRequest(
    @NotNull Long tecnicoId
) {
    
}
