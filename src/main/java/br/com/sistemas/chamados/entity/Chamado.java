package br.com.sistemas.chamados.entity;

import java.time.LocalDateTime;
import java.util.List;

import br.com.sistemas.chamados.enums.Prioridade;
import br.com.sistemas.chamados.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.CascadeType;


@Entity 
@Table(name = "chamados")
public class Chamado {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 120)
    private String titulo;

    @Column (nullable = false, length = 500)
    private String descricao;

    @Column (nullable = false)
    private LocalDateTime dataAbertura;

    @Column
    private LocalDateTime dataFechamento;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false, length = 20)
    private Prioridade prioridade;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false, length = 20)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "chamado", cascade = CascadeType.REMOVE)
    private List<Comentario> comentarios;

    public Chamado() {}

    @PrePersist
    public void prePersist() {
        if (this.dataAbertura == null) {
            this.dataAbertura = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = Status.ABERTO;
        }
    }


    public Chamado(
            String titulo,
            String descricao,
            Prioridade prioridade,
            Cliente cliente, 
            Tecnico tecnico, 
            Categoria categoria) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = Status.ABERTO;
        this.cliente = cliente;
        this.tecnico = tecnico;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }
    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }
    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }
    public Prioridade getPrioridade() {
        return prioridade;
    }
    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Tecnico getTecnico() {
        return tecnico;
    }
    public void setTecnico(Tecnico tecnico) {
        this.tecnico = tecnico;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    
}
