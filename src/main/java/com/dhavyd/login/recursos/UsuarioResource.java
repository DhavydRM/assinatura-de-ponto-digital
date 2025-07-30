package com.dhavyd.login.recursos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.servico.UsuarioService;

@RestController // Identifica que aqui é o controlador da API(Faz a comunicação direta com o front)
@RequestMapping(value = "/usuarios") // Adiciona o endpoint pela qual os metodos serão chamados
public class UsuarioResource {
    
    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() { // ResponseEntity é a classe responsavel por todas as requisições HTTP
        List<Usuario> usuarios = service.buscarTodos();
        return ResponseEntity.ok().body(usuarios); // Retorna um JSON listando os usuarios no Body
    }
}
