package com.dhavyd.login.auth;

import com.dhavyd.login.dto.LoginDTO;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
@Component
public class Token {

    private Boolean token;
    private static UsuarioRepository repository;

    public Token(){
    }

    public boolean getToken() {
        return token;
    }

    public void setToken(boolean token) {
        this.token = token;
    }

    @Autowired
    public void setRepository(UsuarioRepository repository) {
        Token.repository = repository;
    }

    public static Token gerarToken(LoginDTO login) {
        Usuario usuario = repository.findByEmail(login.email());
        Token token = new Token();
        token.setToken(false);
        if (Objects.nonNull(usuario)) {
            if (usuario.getSenha().equals(login.senha())) {
                token.setToken(true);
                return token; // Email e senha batem, então libera o acesso do usuário

            } else { // Senha incorreta, não autentica o usuário
                return token;
            }
        } else { // Não encontrou nenhum usuario com o email informado, não autentica
            return token;
        }
    }


}
