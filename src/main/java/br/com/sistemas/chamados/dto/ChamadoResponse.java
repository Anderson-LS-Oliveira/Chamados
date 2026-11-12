
package br.com.sistemas.chamados.dto;

import java.time.LocalDateTime;
import br.com.sistemas.chamados.entity.Chamado;

public record ChamadoResponse(
    Long id,
    String titulo,
    String descricao,
    String prioridade,
    String status,
    LocalDateTime dataAbertura,
    LocalDateTime dataFechamento,
    Long clienteId,
    String clienteNome,
    Long tecnicoId,
    String tecnicoNome,
    Long categoriaId,
    String categoriaNome
) {
    public static ChamadoResponse de(Chamado c) {
        return new ChamadoResponse(
            c.getId(),
            c.getTitulo(),
            c.getDescricao(),
            c.getPrioridade().name(),
            c.getStatus().name(),
            c.getDataAbertura(),
            c.getDataFechamento(),
            c.getCliente().getId(),
            c.getCliente().getNome(),
            c.getTecnico() != null ? c.getTecnico().getId() : null,
            c.getTecnico() != null ? c.getTecnico().getNome() : null,
            c.getCategoria().getId(),
            c.getCategoria().getNome()
        );
    }
}
