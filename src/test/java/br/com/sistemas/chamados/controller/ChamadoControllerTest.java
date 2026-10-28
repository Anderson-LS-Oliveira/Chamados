package br.com.sistemas.chamados.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ComentarioService;

@WebMvcTest(ChamadoController.class)
class ChamadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChamadoService service;

    @MockitoBean
    private ComentarioService comentarioService;

    @Test
    void deveRetornar400AoCriarChamadoInvalido() throws Exception {
        mockMvc.perform(post("/chamados")

                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"titulo":"","descricao":"","prioridade":"","clienteId":null,"categoriaId":null}
                """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404AoBuscarChamadoInexistente() throws Exception {
        
        when(service.buscar(999L)).thenThrow(new RecursoNaoEncontradoException("Chamado não encontrado com o id 999"));

        mockMvc.perform(get("/chamados/999")).andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409AoAlterarStatusDeChamadoFechado() throws Exception {
        when(service.atualizarStatus(eq(1L), any()))
            .thenThrow(new RegraNegocioException("Não é possível alterar o status de um chamado fechado."));

        mockMvc.perform(patch("/chamados/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"status":"EM_ATENDIMENTO"}""")).andExpect(status().isConflict());
    }
}