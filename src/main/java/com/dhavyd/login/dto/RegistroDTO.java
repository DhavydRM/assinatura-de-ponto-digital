package com.dhavyd.login.dto;

import com.dhavyd.login.entidades.Registro;
import com.dhavyd.login.entidades.enums.Turnos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record RegistroDTO(Long id, String emailUsuario, LocalDateTime entrada, LocalDateTime saida, Turnos turno) {

    public static RegistroDTO registroToDTO(Registro registro) {
        return new RegistroDTO(registro.getId(),
                registro.getUsuario().getEmail(), registro.getEntrada(), registro.getSaida(), Turnos.retornarTurno(registro.getEntrada()));
    }

    public static List<RegistroDTO> listaResgistroToDTO(List<Registro> registros) {
        return registros
                .stream()
                .map(RegistroDTO::registroToDTO)
                .toList();
    }

}
