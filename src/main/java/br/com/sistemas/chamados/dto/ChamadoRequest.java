package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChamadoRequest(
    @NotBlank @Size(max = 120) String titulo,
    @NotBlank @Size(max = 500) String descricao,
    @NotBlank String prioridade,
    @NotNull Long clienteId,
    Long tecnicoId,
    @NotNull Long categoriaId
) {}
