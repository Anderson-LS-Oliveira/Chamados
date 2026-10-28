package br.com.sistemas.chamados.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import br.com.sistemas.chamados.entity.Cliente;

@DataJpaTest
class ClienteRepositoryTest {

    @Autowired
    private ClienteRepository repository;


    @Test
    void deveEncontrarEmailExistente() {
        repository.save(new Cliente("Maria Silva", "maria@email.com", "47999990000"));

        boolean existe = repository.existsByEmail("maria@email.com");

        assertTrue(existe);
    }

    @Test
    void naoDeveEncontrarEmailInexistente() {
        boolean existe = repository.existsByEmail("naoexiste@email.com");
        assertFalse(existe);
    }

    @Test
    void deveIgnorarOProprioClienteAoChecarEmail() {

        Cliente salvo = repository.save(new Cliente("Maria Silva", "maria@email.com", "47999990000"));
        boolean existe = repository.existsByEmailAndIdNot("maria@email.com", salvo.getId());

        assertFalse(existe);
    }
}