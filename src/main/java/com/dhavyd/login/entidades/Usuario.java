package com.dhavyd.login.entidades;

import java.io.Serializable;
import java.util.List;

import com.dhavyd.login.entidades.enums.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Usuario implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    @Enumerated(EnumType.STRING)
    private Roles funcao;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Registro> registroDePontos;


    public Usuario(){
    }

    public Usuario(Long id, String nome, String email, String senha, List<Registro> registro, Roles funcao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.funcao = funcao;
        this.registroDePontos = registroDePontos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Registro> getRegistroDePontos() {
        return registroDePontos;
    }

    public Roles getFuncao() {
        return funcao;
    }

    public void setFuncao(Roles funcao) {
        this.funcao = funcao;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Usuario other = (Usuario) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
