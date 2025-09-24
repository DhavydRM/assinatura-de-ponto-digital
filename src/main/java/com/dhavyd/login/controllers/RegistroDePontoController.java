package com.dhavyd.login.controllers;


import com.dhavyd.login.entidades.RegistroDePonto;
import com.dhavyd.login.servico.RegistroDePontoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.*;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/registros")
public class RegistroDePontoController {

    @Autowired
    private RegistroDePontoService service;

    @GetMapping// GET: Retorna todos os registros de ponto
    public ResponseEntity<List<RegistroDePonto>> buscarTodos(@RequestParam(name = "dataInicial", defaultValue = "") LocalDate dataInicial,
                                                             @RequestParam(name = "dataFinal", defaultValue = "") LocalDate dataFinal) { // ResponseEntity é a classe responsavel por todas as requisições HTTP
        List<RegistroDePonto> registroDePontos = service.buscarTodos(dataInicial, dataFinal);

        return ResponseEntity.ok().body(registroDePontos); // Retorna um JSON listando os RegistroDePontos no Body
    }

    @GetMapping(value = "/{id}") // GET: Retorna um registro pelo id informado
        public ResponseEntity<RegistroDePonto> buscarPorId(@PathVariable Long id) throws Exception {
        return ResponseEntity.ok().body(service.buscarPorId(id));
    }

    @GetMapping(value = "/usuario/{usuarioId}") // GET: Busca os registros de um único usuário
    public ResponseEntity<List<RegistroDePonto>> buscarPorIdUsario(@PathVariable("usuarioId") Long id,
                                                                   @RequestParam(name = "carregarRegistrosToday", defaultValue = "false") boolean carregarRegitrosToday,
                                                                   @RequestParam(name = "dataInicial", defaultValue = "" ) LocalDate dataInicial,
                                                                   @RequestParam(name = "dataFinal", defaultValue = "") LocalDate dataFinal) {
        List<RegistroDePonto> registroDePonto = service.buscarPorIdUsuario(id, carregarRegitrosToday, dataInicial, dataFinal);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @PostMapping(value = "/entrada/{usuarioId}") // POST: Marca a entrada de um usuário
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

    @PostMapping(value = "/saida/{usuarioId}") // POST: Marca a saída de um usuário
    public ResponseEntity<RegistroDePonto> marcarSaida(@PathVariable Long usuarioId) {
        RegistroDePonto registroDePonto = service.marcarSaida(usuarioId);

        if (Objects.nonNull(registroDePonto)) { // Retorna 200 se o registro tiver sido marcado
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(registroDePonto.getId()).toUri();
            return ResponseEntity.created(uri).body(registroDePonto);
        } else { // Erro 400 ao marcar a saida
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping(value = "/{id}") // DELETE: Deleta um registro por id
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarRegistro(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}") // Atualiza um registro por id
    public ResponseEntity<RegistroDePonto> atualizar(@PathVariable Long id,
                                                     @RequestBody RegistroDePonto RegistroDePonto) {
        RegistroDePonto = service.atualizarResgistro(id, RegistroDePonto);
        return ResponseEntity.ok().body(RegistroDePonto);
    }
}
