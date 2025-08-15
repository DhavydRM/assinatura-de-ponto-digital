package com.dhavyd.login.servico;

import com.dhavyd.login.entidades.RegistroDePonto;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.entidades.enums.Turnos;
import com.dhavyd.login.repositorios.RegistroDePontoRepository;
import com.dhavyd.login.repositorios.UsuarioRepository;
import com.dhavyd.login.servico.execoes.RecursoNaoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Objects;

@Service
public class RegistroDePontoService {

    @Autowired
    private RegistroDePontoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RegistroDePonto> buscarTodos(LocalDate data) {
        List<RegistroDePonto> registroDePontos = repository.findAll();
        registroDePontos.getLast();
        if (Objects.nonNull(data)) {
            return registroDePontos.stream()
                    .map(registroDePonto -> {
                        final var entrada = registroDePonto.getEntrada().toLocalDate();
                        if (entrada.equals(data)) {
                            return registroDePonto;
                        }
                        return null;
                    }).filter(Objects::nonNull)
                    .toList();
        }

        return registroDePontos;
    }

    public RegistroDePonto buscarPorId(Long id) {
         return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Recurso não encontrado!"));
    }

    public List<RegistroDePonto> buscarPorIdUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado!"));
        return usuario.getRegistroDePontos();
    }

    public RegistroDePonto marcarEntrada(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        RegistroDePonto registro = new RegistroDePonto(LocalDateTime.now(), usuario, null, null);
        LocalDateTime dataSistema = registro.getEntrada();
        LocalDateTime meioDia = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
        LocalDateTime noite = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0, 0));

        if (dataSistema.isBefore(meioDia)) {
            registro.setTurno(Turnos.MANHA);

        } else if (dataSistema.isAfter(meioDia) && dataSistema.isBefore(noite)) {
            registro.setTurno(Turnos.TARDE);

        } else {
            registro.setTurno(Turnos.NOITE);
        }

        RegistroDePonto registroExistente = usuario.getRegistroDePontos()
                .stream()
                .filter(registroDePonto ->
                        registroDePonto.getEntrada()
                                .toLocalDate()
                                .equals(LocalDate.now()))
                .toList().getLast();


        if (Objects.nonNull(registroExistente)) {
            if (registroExistente.getTurno().equals(Turnos.MANHA) || registroExistente.getTurno().equals(Turnos.TARDE)) {
                if (!registroExistente.getTurno().equals(registro.getTurno())) {
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


    public RegistroDePonto marcarSaida(List<RegistroDePonto> registros) {
        RegistroDePonto ultimoRegistro = registros.getLast();
        ultimoRegistro.setSaida(LocalDateTime.now());
        return repository.save(ultimoRegistro);
    }

    public void deletarRegistro(Long id) {
        repository.deleteById(id);
    }

    public RegistroDePonto atualizarResgistro(Long id, RegistroDePonto novosDados) {
        RegistroDePonto registro = repository.getReferenceById(id);
        atualizarRegistroDePonto(registro, novosDados);
        return repository.save(registro);
    }

    private void atualizarRegistroDePonto(RegistroDePonto registro, RegistroDePonto novosDados) {
        registro.setEntrada(novosDados.getEntrada());
        registro.setSaida(novosDados.getSaida());
    }
}
