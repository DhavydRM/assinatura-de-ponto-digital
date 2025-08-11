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

        if (Objects.nonNull(data)) {
            List<RegistroDePonto> datas = registroDePontos.stream()
                    .map(registroDePonto -> {
                        final var entrada = registroDePonto.getEntrada().toLocalDate();
                        if (entrada.equals(data)) {
                            return registroDePonto;
                        }
                        return null;
                    }).filter(Objects::nonNull)
                    .toList();
            return datas;
        }

        return registroDePontos;
    }

    public RegistroDePonto buscarPorId(Long id) throws Exception {
        RegistroDePonto registro = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Recurso não encontrado!"));
        return registro;
    }

    public List<RegistroDePonto> buscarPorIdUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new RecursoNaoEncontrado("Usuário sem horas registradas!"));
        return usuario.getRegistroDePontos();
    }

    public RegistroDePonto buscarPorUsuarioIdAndData(Long usuarioId, LocalDateTime hoje) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("id"));
        RegistroDePonto registro = null;
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

        for (RegistroDePonto x : usuario.getRegistroDePontos()) {
            LocalDate ld = x.getEntrada().atZone(zoneId).toLocalDate();
            if (ld.equals(hoje)) {
                registro = new RegistroDePonto(x.getId(), x.getUsuario(), x.getEntrada(), x.getSaida(), x.getTurno());
                return registro;
            }   
        }
        return null;
    }


    public RegistroDePonto marcarEntrada(RegistroDePonto registro) {
        registro.setEntrada(LocalDateTime.now());
        LocalDateTime dataSistema = registro.getEntrada();
        LocalDateTime meioDia = LocalDateTime.of(LocalDate.now(), LocalTime.NOON);
        LocalDateTime noite = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 00, 00));

        if(dataSistema.isBefore(meioDia)) {
            registro.setTurno(Turnos.MANHA);

        } else if(dataSistema.isAfter(meioDia) && dataSistema.isBefore(noite)) {
            registro.setTurno(Turnos.TARDE);

        } else {
            registro.setTurno(Turnos.NOITE);
        }

        return repository.save(registro);
    }

    public RegistroDePonto marcarSaida(List<RegistroDePonto> registros) {
        RegistroDePonto ultimoRegistro = registros.get(registros.size() - 1);
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

    public boolean isPresent(Long usuarioId, LocalDateTime date) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado!"));
        for (RegistroDePonto x : usuario.getRegistroDePontos()) {
            if (date.equals(x.getEntrada())) {
                return true;
            }
        }
        return false;
    }
}
