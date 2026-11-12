package br.com.sistemas.chamados.controller.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import br.com.sistemas.chamados.enums.Status;
import br.com.sistemas.chamados.service.CategoriaService;
import br.com.sistemas.chamados.service.ChamadoService;
import org.springframework.web.bind.annotation.GetMapping;
import br.com.sistemas.chamados.enums.Prioridade;
import br.com.sistemas.chamados.service.ClienteService;
import br.com.sistemas.chamados.service.TecnicoService;

@Controller
public class DashboardViewController {

    private final ClienteService clientes;
    private final CategoriaService categorias;
    private final TecnicoService tecnicos;
    private final ChamadoService chamados;


    public DashboardViewController(ClienteService clientes, CategoriaService categorias,
                TecnicoService tecnicos, ChamadoService chamados) {
        this.clientes = clientes;
        this.categorias = categorias;
        this.tecnicos = tecnicos;
        this.chamados = chamados;
    }

    @GetMapping("/")
    public String index(Model model) {
        
        model.addAttribute("totalClientes", clientes.listar().size());
        model.addAttribute("totalCategorias", categorias.listar().size());
        model.addAttribute("totalTecnicos", tecnicos.listar().size());

        model.addAttribute("totalChamados", contar(null, null));
        model.addAttribute("totalAbertos", contar(Status.ABERTO, null));
        model.addAttribute("totalEmAtendimento", contar(Status.EM_ATENDIMENTO, null));
        model.addAttribute("totalFechados", contar(Status.FECHADO, null));

        model.addAttribute("prioBaixa", contar(null, Prioridade.BAIXA));
        model.addAttribute("prioMedia", contar(null, Prioridade.MEDIA));
        model.addAttribute("prioAlta", contar(null, Prioridade.ALTA));
        model.addAttribute("prioCritica", contar(null, Prioridade.CRITICA));

        model.addAttribute("ultimos", chamados.listar(null, null, null,
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dataAbertura"))).getContent());
        
        return "dashboard/index";
        
    }
    private long contar(Status status, Prioridade prioridade) {
        return chamados.listar(status, prioridade, null, PageRequest.of(0, 1)).getTotalElements();
    }
}