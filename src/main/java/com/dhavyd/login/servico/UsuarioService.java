package com.dhavyd.login.servico;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.repositorios.UsuarioRepository;
import com.dhavyd.login.servico.execoes.RecursoNaoEncontrado;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> buscarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("id"));
    }

    public Usuario inserir(Usuario usuario) {
        return repository.save(usuario);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Usuario atualizar(Long id, Usuario usuario) {
        Usuario user = repository.getReferenceById(id);
        atualizarUsuario(user, usuario);
        return repository.save(user);
    }

    private void atualizarUsuario(Usuario user, Usuario usuario) {
        user.setNome(usuario.getNome());
        user.setEmail(usuario.getEmail());
        user.setSenha(usuario.getSenha());
    }

    
}
