package br.com.sistemas.chamados.dto;

public record ChamadoRequest(
    String titulo,
    String descricao,
    String prioridade,
    Long clienteId,
    Long tecnicoId,
    Long categoriaId
) {}
