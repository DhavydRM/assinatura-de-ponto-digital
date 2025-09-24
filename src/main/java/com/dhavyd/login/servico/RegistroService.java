package com.dhavyd.login.servico;

import com.dhavyd.login.dto.RegistroDTO;
import com.dhavyd.login.dto.RegistroDeUsuarioDTO;
import com.dhavyd.login.entidades.Registro;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.entidades.enums.Turnos;
import com.dhavyd.login.repositorios.RegistroRepository;
import com.dhavyd.login.repositorios.UsuarioRepository;
import com.dhavyd.login.servico.execoes.RecursoNaoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Objects;

@Service
public class RegistroService {

    @Autowired
    private RegistroRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RegistroDTO> buscarTodos(LocalDate dataInicial, LocalDate dataFinal) {
        List<Registro> registroDePontos = repository.findAll();

        if (Objects.nonNull(dataInicial) && Objects.nonNull(dataFinal)) {
            return RegistroDTO.listaResgistroToDTO(registrosPorPeriodo(registroDePontos, dataInicial, dataFinal));
        }

        if (Objects.nonNull(dataInicial)) {
            return RegistroDTO.listaResgistroToDTO(registrosPorPeriodo(registroDePontos, dataInicial, dataInicial));
        }

        return RegistroDTO.listaResgistroToDTO(registroDePontos);
    }

    public RegistroDTO buscarPorId(Long id) {
         return RegistroDTO.registroToDTO(repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Recurso não encontrado!")));
    }

    public List<RegistroDeUsuarioDTO> buscarPorIdUsuario(Long idUsuario,
                                                         boolean carregarRegistrosToday,
                                                         LocalDate dataInicial,
                                                         LocalDate dataFinal) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado!"));

        if (carregarRegistrosToday) {
            return RegistroDeUsuarioDTO.listaRegistroUsuarioToDTO(usuario.getRegistroDePontos()
                    .stream()
                    .filter(registroDePonto -> registroDePonto.getEntrada().toLocalDate().equals(LocalDate.now()))
                    .toList());

        }

        if (Objects.nonNull(dataInicial) && Objects.nonNull(dataFinal)) {
            return RegistroDeUsuarioDTO.listaRegistroUsuarioToDTO(registrosPorPeriodo(usuario.getRegistroDePontos(), dataInicial, dataFinal));
        } else if (Objects.nonNull(dataInicial)){
            return RegistroDeUsuarioDTO.listaRegistroUsuarioToDTO(registrosPorPeriodo(usuario.getRegistroDePontos(), dataInicial, dataInicial));
        }

        return RegistroDeUsuarioDTO.listaRegistroUsuarioToDTO(usuario.getRegistroDePontos());
    }

    public Registro marcarEntrada(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Registro registro = new Registro(LocalDateTime.now(), usuario, null, null);

        registro.setTurno(retornarTurno(registro.getEntrada()));

        List<Registro> registroExistente = usuario.getRegistroDePontos()
                .stream()
                .filter(registroDePonto ->
                        registroDePonto.getEntrada()
                                .toLocalDate()
                                .equals(LocalDate.now()))
                .toList();

        if (!registroExistente.isEmpty()) {
            Registro ultimoRegistro = registroExistente.getLast();
            if (ultimoRegistro.getTurno().equals(Turnos.MANHA) || ultimoRegistro.getTurno().equals(Turnos.TARDE)) {
                if (!ultimoRegistro.getTurno().equals(registro.getTurno())) {
                    return repository.save(registro);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }

        return repository.save(registro);
    }

    private Turnos retornarTurno(LocalDateTime entrada) {

        LocalDateTime meioDia = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
        LocalDateTime noite = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0, 0));

        if (entrada.isBefore(meioDia)) {
            return Turnos.MANHA;

        } else if (entrada.isAfter(meioDia) && entrada.isBefore(noite)) {
            return Turnos.TARDE;

        } else {
            return Turnos.NOITE;
        }
    }

    public Registro marcarSaida(Long usuarioId) {
        Usuario user = usuarioRepository.findById(usuarioId).orElse(null);
        assert user != null;
        Registro ultimoRegistro = user.getRegistroDePontos().getLast();

        if (!Objects.nonNull(ultimoRegistro.getSaida())) { // Retorna verdadeiro se a saída do usuário estiver vazia
            ultimoRegistro.setSaida(LocalDateTime.now());
            return repository.save(ultimoRegistro);
        }

        return null;
    }

    public void deletarRegistro(Long id) {
        repository.deleteById(id);
    }

    public Registro atualizarResgistro(Long id, Registro novosDados) {
        Registro registro = repository.getReferenceById(id);
        atualizarRegistroDePonto(registro, novosDados);
        return repository.save(registro);
    }

    private void atualizarRegistroDePonto(Registro registro, Registro novosDados) {
        registro.setEntrada(novosDados.getEntrada());
        registro.setSaida(novosDados.getSaida());
        registro.setTurno(retornarTurno(registro.getEntrada()));
    }

    private List<Registro> registrosPorPeriodo(List<Registro> registroDePontos, LocalDate dataInicial, LocalDate dataFinal) {

        return registroDePontos.stream()
                .map(registroDePonto -> {
                    final var entrada = registroDePonto.getEntrada().toLocalDate();
                    if (entrada.isAfter(dataInicial) && entrada.isBefore(dataFinal) ||
                            entrada.isEqual(dataInicial) ||
                            entrada.isEqual(dataFinal)) {

                        return registroDePonto;
                    }
                    return null;
                }).filter(Objects::nonNull)
                .toList();
    }
}
