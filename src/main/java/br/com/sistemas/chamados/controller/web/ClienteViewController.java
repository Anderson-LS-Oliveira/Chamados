package br.com.sistemas.chamados.controller.web;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.sistemas.chamados.dto.ClienteRequest;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.service.ClienteService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/clientes")
public class ClienteViewController {

    private final ClienteService service;

    public ClienteViewController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listar());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(model, "Novo cliente", "/web/clientes", null, null);
    }

    @PostMapping
    public String criar(@Valid ClienteRequest form, BindingResult result,
                        Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulario(model, "Novo cliente", "/web/clientes", form, primeiroErro(result));
        }
        try {
            service.criar(form);
        } catch (RegraNegocioException e) {
            return formulario(model, "Novo cliente", "/web/clientes", form, e.getMessage());
        }
        redirect.addFlashAttribute("sucesso", "Cliente salvo.");
        return "redirect:/web/clientes";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            return formulario(model, "Editar cliente", "/web/clientes/" + id + "/editar", service.buscar(id), null);
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/web/clientes";
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid ClienteRequest form, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        String acao = "/web/clientes/" + id + "/editar";
        if (result.hasErrors()) {
            return formulario(model, "Editar cliente", acao, form, primeiroErro(result));
        }
        try {
            service.atualizar(id, form);
        } catch (RegraNegocioException e) {
            return formulario(model, "Editar cliente", acao, form, e.getMessage());
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/web/clientes";
        }
        redirect.addFlashAttribute("sucesso", "Cliente atualizado.");
        return "redirect:/web/clientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Cliente excluído.");
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro", "Não é possível excluir: existem registros vinculados.");
        }
        return "redirect:/web/clientes";
    }


    private String formulario(Model model, String titulo, String acao, Object form, String erro) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("acao", acao);
        model.addAttribute("form", form);
        if (erro != null) {
            model.addAttribute("erros", List.of(erro));
        }
        return "clientes/form";
    }

    private String primeiroErro(BindingResult result) {
        return result.getAllErrors().get(0).getDefaultMessage();
    }
}