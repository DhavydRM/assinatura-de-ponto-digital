package com.dhavyd.login.recursos;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dhavyd.login.entidades.RegistroDePonto;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.servico.RegistroDePontoService;
import com.dhavyd.login.servico.UsuarioService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController // Identifica que aqui é o controlador da API(Faz a comunicação direta com o front)
@RequestMapping(value = "/registros") // Adiciona o endpoint pela qual os metodos serão chamados
public class RegistroDePontoResource {
    
    @Autowired
    private RegistroDePontoService service;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<RegistroDePonto>> buscarTodos() { // ResponseEntity é a classe responsavel por todas as requisições HTTP
        List<RegistroDePonto> registroDePontos = service.buscarTodos();
        return ResponseEntity.ok().body(registroDePontos); // Retorna um JSON listando os RegistroDePontos no Body
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RegistroDePonto> buscarPorId(@PathVariable Long id) throws Exception {
        RegistroDePonto registroDePonto = service.buscarPorId(id);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @GetMapping(value = "usuario/{id}")
    public ResponseEntity<List<RegistroDePonto>> buscarPorIdUsario(@PathVariable Long id) throws Exception {
        List<RegistroDePonto> registroDePonto = service.buscarPorIdUsuario(id);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @PostMapping(value = "/entrada/{id}")
    public ResponseEntity<RegistroDePonto> marcarEntrada(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        RegistroDePonto registroDePonto = service.marcarEntrada(new RegistroDePonto(null, user, null));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registroDePonto.getId()).toUri();
        return ResponseEntity.created(uri).body(registroDePonto);
    }

    @PostMapping(value = "/saida/{id}")
    public ResponseEntity<RegistroDePonto> marcarSaida(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        RegistroDePonto registroDePonto = service.marcarSaida(user.getRegistroDePontos());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registroDePonto.getId()).toUri();
        return ResponseEntity.created(uri).body(registroDePonto);
    }
    
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarRegistro(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<RegistroDePonto> atualizar(@PathVariable Long id, @RequestBody RegistroDePonto RegistroDePonto) {
        RegistroDePonto = service.atualizarResgistro(id, RegistroDePonto);
        return ResponseEntity.ok().body(RegistroDePonto);
    }
}
