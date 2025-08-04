package com.dhavyd.login.recursos;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dhavyd.login.entidades.RegistroDePontoT;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.servico.RegistroDePontoTService;
import com.dhavyd.login.servico.UsuarioService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController // Identifica que aqui é o controlador da API(Faz a comunicação direta com o front)
@RequestMapping(value = "/registros/tarde") // Adiciona o endpoint pela qual os metodos serão chamados
public class RegistroDePontoTResource {
    
    @Autowired
    private RegistroDePontoTService service;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<RegistroDePontoT>> buscarTodos() { // ResponseEntity é a classe responsavel por todas as requisições HTTP
        List<RegistroDePontoT> registroDePontos = service.buscarTodos();
        return ResponseEntity.ok().body(registroDePontos); // Retorna um JSON listando os RegistroDePontos no Body
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RegistroDePontoT> buscarPorId(@PathVariable Long id) throws Exception {
        RegistroDePontoT registroDePonto = service.buscarPorId(id);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @GetMapping(value = "usuario/{usuarioId}")
    public ResponseEntity<List<RegistroDePontoT>> buscarPorIdUsario(@PathVariable Long id) throws Exception {
        List<RegistroDePontoT> registroDePonto = service.buscarPorIdUsuario(id);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @GetMapping("/hoje/{usuarioId}")
    public ResponseEntity<RegistroDePontoT> buscarPontoDeHoje(@PathVariable Long usuarioId) {
        LocalDate hoje = LocalDate.now();
        RegistroDePontoT registroDoDia = service.buscarPorUsuarioIdAndData(usuarioId, hoje);
    
        return ResponseEntity.ok().body(registroDoDia);
}

    @PostMapping(value = "/entrada/{usuarioId}")
    public ResponseEntity<RegistroDePontoT> marcarEntrada(@PathVariable Long usuarioId) {
        Usuario user = usuarioService.buscarPorId(usuarioId);

        RegistroDePontoT registroExistente = service.buscarPorUsuarioIdAndData(usuarioId, LocalDate.now());

        if (registroExistente != null) {
            if (service.isPresent(usuarioId, registroExistente.getEntrada()) && registroExistente.getEntrada() != null) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        RegistroDePontoT registroDePonto = service.marcarEntrada(new RegistroDePontoT(null, user, null));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registroDePonto.getId()).toUri();
        return ResponseEntity.created(uri).body(registroDePonto);
    }

    @PostMapping(value = "/saida/{usuarioId}")
    public ResponseEntity<RegistroDePontoT> marcarSaida(@PathVariable Long usuarioId) {
        Usuario user = usuarioService.buscarPorId(usuarioId);
        RegistroDePontoT registroDePonto = service.marcarSaida(user.getRegistroDePontosT());
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
    public ResponseEntity<RegistroDePontoT> atualizar(@PathVariable Long id, @RequestBody RegistroDePontoT RegistroDePonto) {
        RegistroDePonto = service.atualizarResgistro(id, RegistroDePonto);
        return ResponseEntity.ok().body(RegistroDePonto);
    }
}
