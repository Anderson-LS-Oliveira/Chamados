package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(
    @NotBlank @Size(max = 500) String texto,
    @NotBlank @Size(max = 120) String autor
) {}