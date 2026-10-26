package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.enums.Status;
import jakarta.validation.constraints.NotNull;


public record AtualizarStatusRequest(
    @NotNull Status status
) {}
