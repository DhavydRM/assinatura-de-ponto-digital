package com.dhavyd.login.entidades;

import com.dhavyd.login.entidades.enums.Turnos;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class RegistroDePonto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    private LocalDateTime entrada;
    private LocalDateTime saida;

    @Enumerated(EnumType.STRING)
    private Turnos turno;

    public RegistroDePonto(LocalDateTime entrada, Usuario usuario, Long id, Turnos turno) {
        this.entrada = entrada;
        this.usuario = usuario;
        this.id = id;
        this.turno = turno;
    }

    public RegistroDePonto(Long id, Usuario usuario, LocalDateTime entrada, LocalDateTime saida, Turnos turno) {
        this.id = id;
        this.usuario = usuario;
        this.entrada = entrada;
        this.saida = saida;
        this.turno = turno;
    }

    public RegistroDePonto() {

    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public Turnos getTurno() {
        return turno;
    }

    public void setTurno(Turnos turno) {
        this.turno = turno;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RegistroDePonto that = (RegistroDePonto) o;
        return Objects.equals(entrada, that.entrada);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entrada);
    }
}
