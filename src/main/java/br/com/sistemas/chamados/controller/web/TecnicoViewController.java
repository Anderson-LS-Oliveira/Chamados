package br.com.sistemas.chamados.controller.web;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.TecnicoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/tecnicos")
public class TecnicoViewController {

    private final TecnicoService service;


    public TecnicoViewController(TecnicoService service) {
        this.service = service;
    }

    
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tecnicos", service.listar());
        return "tecnicos/lista";

    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(model, "Novo técnico", "/web/tecnicos", null, null);
    }

    @PostMapping
    public String criar(@Valid TecnicoRequest form, BindingResult result,
                        Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulario(model, "Novo técnico", "/web/tecnicos", form, primeiroErro(result));
        }
        try {
            service.criar(form);
        } catch (RegraNegocioException e) {
            return formulario(model, "Novo técnico", "/web/tecnicos", form, e.getMessage());
        }
        
        redirect.addFlashAttribute("sucesso", "Técnico salvo.");
        return "redirect:/web/tecnicos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        
        try {
            return formulario(model, "Editar técnico", "/web/tecnicos/" + id + "/editar", service.buscar(id), null);
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/web/tecnicos";
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid TecnicoRequest form, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        String acao = "/web/tecnicos/" + id + "/editar";
        if (result.hasErrors()) {
            return formulario(model, "Editar técnico", acao, form, primeiroErro(result));
        }
        try {
            service.atualizar(id, form);
        } catch (RegraNegocioException e) {
            return formulario(model, "Editar técnico", acao, form, e.getMessage());
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/web/tecnicos";
        }
        redirect.addFlashAttribute("sucesso", "Técnico atualizado.");
        return "redirect:/web/tecnicos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Técnico excluído.");
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro", "Não é possível excluir: existem registros vinculados.");
        }
        return "redirect:/web/tecnicos";
    }


    private String formulario(Model model, String titulo, String acao, Object form, String erro) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("acao", acao);
        model.addAttribute("form", form);
        if (erro != null) {
            model.addAttribute("erros", List.of(erro));
        }
        return "tecnicos/form";
    }

    private String primeiroErro(BindingResult result) {
        return result.getAllErrors().get(0).getDefaultMessage();
    }
}