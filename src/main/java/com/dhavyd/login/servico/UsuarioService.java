package com.dhavyd.login.servico;

import java.util.List;
import java.util.Objects;


import com.dhavyd.login.dto.LoginDTO;
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

    public Boolean autenticarUsuario(LoginDTO login) {
        Usuario usuario = repository.findByEmail(login.email());
        if (Objects.nonNull(usuario)) {
            if (usuario.getSenha().equals(login.senha())) {
                return true; // Email e senha batem, então libera o acesso do usuário

            } else { // Senha incorreta, não autentica o usuário
                return false;
            }
        } else { // Não encontrou nenhum usuario com o email informado, não autentica
            return false;
        }
    }

    private void atualizarUsuario(Usuario user, Usuario usuario) {
        user.setNome(usuario.getNome());
        user.setEmail(usuario.getEmail());
        user.setSenha(usuario.getSenha());
        user.setFuncao(usuario.getFuncao());
    }

    
}
