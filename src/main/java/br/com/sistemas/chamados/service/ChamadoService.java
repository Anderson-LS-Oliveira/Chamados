package br.com.sistemas.chamados.service;


import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Categoria;
import br.com.sistemas.chamados.enums.Status;
import br.com.sistemas.chamados.enums.Prioridade;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service 
public class ChamadoService {
    
    private final ChamadoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TecnicoRepository tecnicoRepository;

    public ChamadoService(ChamadoRepository repository, ClienteRepository clienteRepository, CategoriaRepository categoriaRepository, TecnicoRepository tecnicoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Transactional
    public ChamadoResponse criar(ChamadoRequest dto) {
        if (repository.existsByTitulo(dto.titulo())) {
            throw new RegraNegocioException(
                "Já existe chamado com o título " + dto.titulo()
            );
        }

        Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(
            () -> new RegraNegocioException("Cliente não encontrado com o id " + dto.clienteId())
        );

        Categoria categoria = categoriaRepository.findById(dto.categoriaId()).orElseThrow(
            () -> new RegraNegocioException("Categoria não encontrada com o id " + dto.categoriaId())
        );

        Tecnico tecnico = null;
        if (dto.tecnicoId() != null) {
            tecnico = tecnicoRepository.findById(dto.tecnicoId()).orElseThrow(
                () -> new RecursoNaoEncontradoException("Técnico não encontrado com o id " + dto.tecnicoId())
            );
        }


        Chamado salvo = repository.save(new Chamado(
            dto.titulo(),
            dto.descricao(),
            Prioridade.valueOf(dto.prioridade()),
            cliente,
            tecnico,
            categoria
        ));
        return ChamadoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listar(Status status, Prioridade prioridade, Long clienteId, Pageable pageable) {
        return repository.filtrar(status, prioridade, clienteId, pageable).map(ChamadoResponse::de);
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntity(id));
    }

    @Transactional
    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado chamado = buscarEntity(id);
        if (repository.existsByTituloAndIdNot(dto.titulo(), id)) {
            throw new RegraNegocioException("Já existe chamado com o título " + dto.titulo());
        }
        chamado.setTitulo(dto.titulo());
        chamado.setDescricao(dto.descricao());
        chamado.setPrioridade(Prioridade.valueOf(dto.prioridade()));

        Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(
            () -> new RegraNegocioException("Cliente não encontrado com o id " + dto.clienteId())
        );
        chamado.setCliente(cliente);

        Categoria categoria = categoriaRepository.findById(dto.categoriaId()).orElseThrow(
            () -> new RegraNegocioException("Categoria não encontrada com o id " + dto.categoriaId())
        );
        chamado.setCategoria(categoria);

        return ChamadoResponse.de(chamado);
    }

    @Transactional
    public ChamadoResponse atualizarStatus(Long id, Status status) {
        Chamado chamado = buscarEntity(id);

        if (chamado.getStatus() == Status.FECHADO) {
            throw new RegraNegocioException("Não é possível alterar o status de um chamado fechado.");
        }

        if (status != Status.ABERTO && chamado.getTecnico() == null) {
                throw new RegraNegocioException("Chamado precisa de um técnico atribuido para mudar status");
        }

        chamado.setStatus(status);

        if (status == Status.FECHADO) {
            chamado.setDataFechamento(LocalDateTime.now());
        }
        return ChamadoResponse.de(chamado);

    }

    @Transactional
    public ChamadoResponse atribuirTecnico(Long id, Long tecnicoId) {
        Chamado chamado = buscarEntity(id);
        if (chamado.getStatus() == Status.FECHADO) {
            throw new RegraNegocioException("Não é possível atribuir técnico a um chamado fechado.");
        }
        Tecnico tecnico = tecnicoRepository.findById(tecnicoId).orElseThrow(
            () -> new RecursoNaoEncontradoException("Técnico não encontrado com o id " + tecnicoId));
        chamado.setTecnico(tecnico);
        if (chamado.getStatus() == Status.ABERTO){
            chamado.setStatus(Status.EM_ATENDIMENTO);
        }
        return ChamadoResponse.de(chamado);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntity(id));
    }

    private Chamado buscarEntity(Long id) {
        return repository.findById(id).orElseThrow(
            () -> new RecursoNaoEncontradoException("Chamado não encontrado com o id " + id)
        );
    }



}