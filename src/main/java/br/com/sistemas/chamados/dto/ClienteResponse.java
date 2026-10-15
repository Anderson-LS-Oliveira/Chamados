package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Cliente;

public record ClienteResponse(Long is, String nome, String email, String telefone) {
    
    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(
            c.getId(),
            c.getNome(),
            c.getEmail(),
            c.getTelefone()
        );
    }
}
