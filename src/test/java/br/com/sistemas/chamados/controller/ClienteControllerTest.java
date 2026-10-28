package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sistemas.chamados.dto.ClienteResponse;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService service;

    @MockitoBean
    private ChamadoService chamadoService;

    @Test
    void deveRetornar201AoCriarCliente() throws Exception {
        Cliente cliente = new Cliente("Maria Silva", "maria@email.com", "47999990000");
        when(service.criar(any())).thenReturn(ClienteResponse.de(cliente));

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"Maria Silva","email":"maria@email.com","telefone":"47999990000"}
                    """))
            .andExpect(status().isCreated());
    }

    @Test
    void deveRetornar400AoCriarClienteInvalido() throws Exception {
        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"","email":"email-invalido","telefone":"47999990000"} """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409AoCriarClienteComEmailDuplicado() throws Exception {
        when(service.criar(any())).thenThrow(new RegraNegocioException("Já existe cliente com este e-mail"));

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"Maria Silva","email":"maria@email.com","telefone":"47999990000"}
                    """))
            .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar404AoBuscarClienteInexistente() throws Exception {
        when(service.buscar(999L)).thenThrow(new RecursoNaoEncontradoException("Cliente 999 não encontrado"));

        mockMvc.perform(get("/clientes/999")).andExpect(status().isNotFound());
    }
}