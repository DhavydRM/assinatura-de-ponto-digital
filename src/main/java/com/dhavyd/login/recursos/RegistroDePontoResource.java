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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "/registros")
public class RegistroDePontoResource {

    @Autowired
    private RegistroDePontoService service;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<RegistroDePonto>> buscarTodos(@RequestParam(name = "date", defaultValue = "") String data) { // ResponseEntity é a classe responsavel por todas as requisições HTTP
        List<RegistroDePonto> registroDePontos = service.buscarTodos();

        if (!data.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dataBuscada = LocalDate.parse(data, formatter);
            List<RegistroDePonto> datas = registroDePontos.stream().filter(RegistroDePonto -> RegistroDePonto.equals(dataBuscada)).toList();

            return ResponseEntity.ok().body(datas);
        }

        return ResponseEntity.ok().body(registroDePontos); // Retorna um JSON listando os RegistroDePontos no Body
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RegistroDePonto> buscarPorId(@PathVariable Long id) throws Exception {
        RegistroDePonto registroDePonto = service.buscarPorId(id);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @GetMapping(value = "usuario/{usuarioId}")
    public ResponseEntity<List<RegistroDePonto>> buscarPorIdUsario(@PathVariable Long id) throws Exception {
        List<RegistroDePonto> registroDePonto = service.buscarPorIdUsuario(id);
        return ResponseEntity.ok().body(registroDePonto);
    }

    @GetMapping("/hoje/{usuarioId}")
    public ResponseEntity<RegistroDePonto> buscarPontoDeHoje(@PathVariable Long usuarioId) {
        LocalDateTime hoje = LocalDateTime.now();
        RegistroDePonto registroDoDia = service.buscarPorUsuarioIdAndData(usuarioId, hoje);

        return ResponseEntity.ok().body(registroDoDia);
    }

    @PostMapping(value = "/entrada/{usuarioId}")
    public ResponseEntity<RegistroDePonto> marcarEntrada(@PathVariable Long usuarioId) {
        Usuario user = usuarioService.buscarPorId(usuarioId);

        RegistroDePonto registroExistente = service.buscarPorUsuarioIdAndData(usuarioId, LocalDateTime.now());

        if (registroExistente != null) {
            if (service.isPresent(usuarioId, registroExistente.getEntrada()) && registroExistente.getEntrada() != null) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        RegistroDePonto registroDePonto = service.marcarEntrada(new RegistroDePonto(null, user, null, null));
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(registroDePonto.getId()).toUri();
        return ResponseEntity.created(uri).body(registroDePonto);
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
    public ResponseEntity<RegistroDePonto> atualizar(@PathVariable Long id, @RequestBody RegistroDePonto RegistroDePonto) {
        RegistroDePonto = service.atualizarResgistro(id, RegistroDePonto);
        return ResponseEntity.ok().body(RegistroDePonto);
    }
}
