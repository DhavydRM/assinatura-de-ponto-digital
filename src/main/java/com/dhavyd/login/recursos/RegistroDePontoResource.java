package com.dhavyd.login.recursos;


import com.dhavyd.login.entidades.RegistroDePonto;
import com.dhavyd.login.servico.RegistroDePontoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/registros")
public class RegistroDePontoResource {

    @Autowired
    private RegistroDePontoService service;

    @GetMapping
    public ResponseEntity<List<RegistroDePonto>> buscarTodos(@RequestParam(name = "data", defaultValue = "") LocalDate dataFiltro,
                                                             @RequestParam(name = "dataInicial", defaultValue = "") LocalDate dataInicial,
                                                             @RequestParam(name = "dataFinal", defaultValue = "") LocalDate dataFinal) { // ResponseEntity é a classe responsavel por todas as requisições HTTP
        List<RegistroDePonto> registroDePontos = service.buscarTodos(dataFiltro, dataInicial, dataFinal);

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
        RegistroDePonto registroDePonto = service.marcarSaida(usuarioId);

        if (Objects.nonNull(registroDePonto)) {
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(registroDePonto.getId()).toUri();
            return ResponseEntity.created(uri).body(registroDePonto);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
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
