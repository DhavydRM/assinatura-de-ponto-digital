package com.dhavyd.login.dto;

import com.dhavyd.login.entidades.Registro;
import com.dhavyd.login.entidades.enums.Turnos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record RegistroDeUsuarioDTO(Long id, LocalDateTime entrada, LocalDateTime saida, Turnos turno) {

    public static RegistroDeUsuarioDTO registroUsuarioToDTO(Registro registro) {
        return new RegistroDeUsuarioDTO(registro.getId(),
                registro.getEntrada(), registro.getSaida(), Turnos.retornarTurno(registro.getEntrada()));
    }

    public static List<RegistroDeUsuarioDTO> listaRegistroUsuarioToDTO (List<Registro> registros) {
        return registros
                .stream()
                .map(RegistroDeUsuarioDTO::registroUsuarioToDTO)
                .toList();
    }

}
