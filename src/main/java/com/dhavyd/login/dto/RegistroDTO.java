package com.dhavyd.login.dto;

import com.dhavyd.login.entidades.Registro;

import java.time.LocalDateTime;
import java.util.List;

public record RegistroDTO(Long id, String emailUsuario, LocalDateTime entrada, LocalDateTime saida) {

    public static RegistroDTO registroToDTO(Registro registro) {
        return new RegistroDTO(registro.getId(),
                registro.getUsuario().getEmail(), registro.getEntrada(), registro.getSaida());
    }

    public static List<RegistroDTO> listaResgistroToDTO(List<Registro> registros) {
        return registros
                .stream()
                .map(RegistroDTO::registroToDTO)
                .toList();
    }
}
