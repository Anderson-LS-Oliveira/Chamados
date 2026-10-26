package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
    @NotBlank (message = "Nome é obrigatório")
    @Size (max = 120, message = "Nome deve ter no máximo 120 caracteres")
    String nome,
    
    @NotBlank (message = "Descrição é obrigatória")
    @Size (max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    String descricao
) {}