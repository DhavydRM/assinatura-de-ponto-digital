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

    public List<RegistroDePonto> buscarTodos(LocalDate dataFiltrada, LocalDate dataInicial, LocalDate dataFinal) {
        List<RegistroDePonto> registroDePontos = repository.findAll();
        if (Objects.nonNull(dataInicial) && Objects.nonNull(dataFinal)) {
            var diaInicial = dataInicial.getDayOfMonth();
            var diaFinal = dataFinal.getDayOfMonth();
            return registroDePontos.stream()
                    .map(registroDePonto -> {
                        final var entrada = registroDePonto.getEntrada().toLocalDate();
                        if (entrada.getDayOfMonth() >= diaInicial && entrada.getDayOfMonth() <= diaFinal) {
                            return registroDePonto;
                        }
                        return null;
                    }).filter(Objects::nonNull)
                    .toList();
        }
        if (Objects.nonNull(dataFiltrada)) {
            return registroDePontos.stream()
                    .map(registroDePonto -> {
                        final var entrada = registroDePonto.getEntrada().toLocalDate();
                        if (entrada.equals(dataFiltrada)) {
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

    public List<RegistroDePonto> buscarPorIdUsuario(Long idUsuario, boolean carregarRegistros) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado!"));

        if (carregarRegistros) {
            return usuario.getRegistroDePontos()
                    .stream()
                    .filter(registroDePonto -> registroDePonto.getEntrada().toLocalDate().equals(LocalDate.now()))
                    .toList();

        }
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

        List<RegistroDePonto> registroExistente = usuario.getRegistroDePontos()
                .stream()
                .filter(registroDePonto ->
                        registroDePonto.getEntrada()
                                .toLocalDate()
                                .equals(LocalDate.now()))
                .toList();

        if (!registroExistente.isEmpty()) {
            RegistroDePonto ultimoRegistro = registroExistente.getLast();
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


    public RegistroDePonto marcarSaida(Long usuarioId) {
        Usuario user = usuarioRepository.findById(usuarioId).orElse(null);
        assert user != null;
        RegistroDePonto ultimoRegistro = user.getRegistroDePontos().getLast();

        if (!Objects.nonNull(ultimoRegistro.getSaida())) { // Retorna verdadeiro se a saída do usuário estiver vazia
            ultimoRegistro.setSaida(LocalDateTime.now());
            return repository.save(ultimoRegistro);
        }

        return null;
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
