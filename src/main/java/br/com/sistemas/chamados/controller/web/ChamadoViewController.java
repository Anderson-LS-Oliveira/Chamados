package br.com.sistemas.chamados.controller.web;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.enums.Prioridade;
import br.com.sistemas.chamados.enums.Status;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.ComentarioService;
import br.com.sistemas.chamados.service.TecnicoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/chamados")
public class ChamadoViewController {

    private final ChamadoService service;
    private final ComentarioService comentarioService;
    private final ClienteService clientes;
    private final CategoriaService categorias;
    private final TecnicoService tecnicos;

    public ChamadoViewController(ChamadoService service, ComentarioService comentarioService,
                                 ClienteService clientes, CategoriaService categorias,
                                 TecnicoService tecnicos) {
        this.service = service;
        this.comentarioService = comentarioService;
        this.clientes = clientes;
        this.categorias = categorias;
        this.tecnicos = tecnicos;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Status status,
                         @RequestParam(required = false) Prioridade prioridade,
                         @RequestParam(required = false) Long clienteId,
                         @PageableDefault(size = 10, sort = "dataAbertura", direction = Sort.Direction.DESC) Pageable pageable,
                         Model model) {
        model.addAttribute("chamados", service.listar(status, prioridade, clienteId, pageable));
        model.addAttribute("status", status);
        model.addAttribute("prioridade", prioridade);
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("statusOpcoes", Status.values());
        model.addAttribute("prioridadeOpcoes", Prioridade.values());
        model.addAttribute("clientes", clientes.listar());
        return "chamados/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        carregarOpcoes(model);
        return "chamados/form";
    }

    @PostMapping
    public String criar(@Valid ChamadoRequest form, BindingResult result,
                        Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return voltarAoFormulario(model, form, result.getAllErrors().get(0).getDefaultMessage());
        }
        try {
            service.criar(form);
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            return voltarAoFormulario(model, form, e.getMessage());
        }
        redirect.addFlashAttribute("sucesso", "Chamado aberto.");
        return "redirect:/web/chamados";
    }


    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("c", service.buscar(id));
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/web/chamados";
        }
        model.addAttribute("comentarios", comentarioService.listar(id));
        model.addAttribute("tecnicos", tecnicos.listar());
        model.addAttribute("statusOpcoes", Status.values());
        return "chamados/detalhe";
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable Long id, @RequestParam Status status,
                                RedirectAttributes redirect) {
        try {
            service.atualizarStatus(id, status);
            redirect.addFlashAttribute("sucesso", "Status atualizado.");
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/web/chamados/" + id;
    }

    @PostMapping("/{id}/atribuir")
    public String atribuir(@PathVariable Long id, @RequestParam Long tecnicoId,
                           RedirectAttributes redirect) {
        try {
            service.atribuirTecnico(id, tecnicoId);
            redirect.addFlashAttribute("sucesso", "Técnico atribuído.");
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/web/chamados/" + id;
    }

    @PostMapping("/{id}/comentarios")
    public String comentar(@PathVariable Long id, @Valid ComentarioRequest form,
                           BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("erro", result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/web/chamados/" + id;
        }
        try {
            comentarioService.criar(id, form);
            redirect.addFlashAttribute("sucesso", "Comentário adicionado.");
        } catch (RegraNegocioException | RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/web/chamados/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Chamado excluído.");
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro", "Não é possível excluir: existem registros vinculados.");
        }
        return "redirect:/web/chamados";
    }


    private void carregarOpcoes(Model model) {
        model.addAttribute("clientes", clientes.listar());
        model.addAttribute("categorias", categorias.listar());
        model.addAttribute("tecnicos", tecnicos.listar());
    }

    private String voltarAoFormulario(Model model, ChamadoRequest form, String erro) {
        model.addAttribute("form", form);
        model.addAttribute("erros", List.of(erro));
        carregarOpcoes(model);
        return "chamados/form";
    }
}