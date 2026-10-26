
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
    Long tecnicoId,
    Long categoriaId
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
            c.getTecnico().getId(),
            c.getCategoria().getId()
        );
    }
}
