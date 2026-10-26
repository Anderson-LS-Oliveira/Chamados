package br.com.sistemas.chamados.repository;

import br.com.sistemas.chamados.entity.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.sistemas.chamados.enums.Prioridade;
import br.com.sistemas.chamados.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {
    boolean existsByTitulo(String titulo);
    boolean existsByTituloAndIdNot(String titulo, Long id);

    @Query("""
        SELECT c FROM Chamado c
        WHERE (:status IS NULL OR c.status = :status)
          AND (:prioridade IS NULL OR c.prioridade = :prioridade)
          AND (:clienteId IS NULL OR c.cliente.id = :clienteId)
        """)
    Page<Chamado> filtrar(@Param("status") Status status,
                          @Param("prioridade") Prioridade prioridade,
                          @Param("clienteId") Long clienteId,
                          Pageable pageable);
    
}
