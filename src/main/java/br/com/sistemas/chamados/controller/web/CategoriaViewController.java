package br.com.sistemas.chamados.controller.web;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.dto.CategoriaRequest;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/web/categorias")
public class CategoriaViewController {

    private final CategoriaService service;

    public CategoriaViewController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", service.listar());
        return "categorias/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(model, "Nova categoria", "/web/categorias", null, null);
    }

    @PostMapping
    public String criar(@Valid CategoriaRequest form, BindingResult result,
                        Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulario(model, "Nova categoria", "/web/categorias", form, primeiroErro(result));
        }
        try {
            service.criar(form);
        } catch (RegraNegocioException e) {
            return formulario(model, "Nova categoria", "/web/categorias", form, e.getMessage());
        }
        redirect.addFlashAttribute("sucesso", "Categoria salva.");
        return "redirect:/web/categorias";
    }
    

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            return formulario(model, "Editar categoria", "/web/categorias/" + id + "/editar", service.buscar(id), null);
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/web/categorias";
        }
    }

    @PostMapping("/{id}/editar")
    public String atualizar(@PathVariable Long id, @Valid CategoriaRequest form, BindingResult result,
                            Model model, RedirectAttributes redirect) {
        String acao = "/web/categorias/" + id + "/editar";
        if (result.hasErrors()) {
            return formulario(model, "Editar categoria", acao, form, primeiroErro(result));
        }
        try {
            service.atualizar(id,form);
        } catch (RegraNegocioException e) {
            return formulario(model, "Editar categoria", acao, form, e.getMessage());
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/web/categorias";
        }
        redirect.addFlashAttribute("sucesso", "Categoria atualizada.");
        return "redirect:/web/categorias";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
            redirect.addFlashAttribute("sucesso", "Categoria excluída.");
        } catch (RecursoNaoEncontradoException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro", "Não é possível excluir: existem registros vinculados.");
        }
        return "redirect:/web/categorias";
    }


    private String formulario(Model model, String titulo, String acao, Object form, String erro) {
        model.addAttribute("titulo", titulo);
        model.addAttribute("acao", acao);
        model.addAttribute("form", form);
        if (erro != null) {model.addAttribute("erros", List.of(erro));
        }return "categorias/form";
    }

    private String primeiroErro(BindingResult result) {
        return result.getAllErrors().get(0).getDefaultMessage();
    }
}