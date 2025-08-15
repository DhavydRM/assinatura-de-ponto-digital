package com.dhavyd.login.recursos;


import com.dhavyd.login.entidades.RegistroDePonto;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.servico.RegistroDePontoService;
import com.dhavyd.login.servico.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/registros")
public class RegistroDePontoResource {

    @Autowired
    private RegistroDePontoService service;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<RegistroDePonto>> buscarTodos(@RequestParam(name = "date", defaultValue = "") LocalDate data) { // ResponseEntity é a classe responsavel por todas as requisições HTTP
        List<RegistroDePonto> registroDePontos = service.buscarTodos(data);

        return ResponseEntity.ok().body(registroDePontos); // Retorna um JSON listando os RegistroDePontos no Body
    }

    @GetMapping(value = "/{id}")
        public ResponseEntity<RegistroDePonto> buscarPorId(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok().body(service.buscarPorId(id));
    }

    @GetMapping(value = "/usuario/{usuarioId}")
    public ResponseEntity<List<RegistroDePonto>> buscarPorIdUsario(@PathVariable("usuarioId") Long id,
                                                                   @RequestParam(name = "carregarRegistros", defaultValue = "false") boolean carregarRegitros) {
        List<RegistroDePonto> registroDePonto = service.buscarPorIdUsuario(id, carregarRegitros);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @PostMapping(value = "/entrada/{usuarioId}")
    public ResponseEntity<RegistroDePonto> marcarEntrada(@PathVariable Long usuarioId) {
        RegistroDePonto registroDePonto = service.marcarEntrada(usuarioId);

        if (Objects.nonNull(registroDePonto)) {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(registroDePonto.getId()).toUri();
            return ResponseEntity.created(uri).body(registroDePonto);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value = "/saida/{usuarioId}")
    public ResponseEntity<RegistroDePonto> marcarSaida(@PathVariable Long usuarioId) {
        Usuario user = usuarioService.buscarPorId(usuarioId);
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
    public ResponseEntity<RegistroDePonto> atualizar(@PathVariable Long id,
                                                     @RequestBody RegistroDePonto RegistroDePonto) {
        RegistroDePonto = service.atualizarResgistro(id, RegistroDePonto);
        return ResponseEntity.ok().body(RegistroDePonto);
    }
}
