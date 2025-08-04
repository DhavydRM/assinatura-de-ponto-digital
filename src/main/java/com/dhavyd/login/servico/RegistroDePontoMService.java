package com.dhavyd.login.servico;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhavyd.login.entidades.RegistroDePontoM;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.repositorios.RegistroDePontoMRepository;
import com.dhavyd.login.repositorios.UsuarioRepository;
import com.dhavyd.login.servico.execoes.RecursoNaoEncontrado;

@Service
public class RegistroDePontoMService {
    
    @Autowired
    private RegistroDePontoMRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RegistroDePontoM> buscarTodos() {
        return repository.findAll();
    }

    public RegistroDePontoM buscarPorId(Long id) throws Exception {
        RegistroDePontoM registro = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Recurso não encontrado!"));
        return registro;
    }

    public List<RegistroDePontoM> buscarPorIdUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new RecursoNaoEncontrado("Usuário sem horas registradas!"));
        return usuario.getRegistroDePontosM();
    }

    public RegistroDePontoM buscarPorUsuarioIdAndData(Long usuarioId, LocalDate hoje) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("id"));
        RegistroDePontoM registro = null;
        ZoneId zoneId = ZoneId.systemDefault();

        for (RegistroDePontoM x : usuario.getRegistroDePontosM()) {
            LocalDate ld = x.getEntrada().atZone(zoneId).toLocalDate();
            if (ld.equals(hoje)) {
                registro = new RegistroDePontoM(x.getId(), x.getUsuario(), x.getEntrada(), x.getSaida());
                return registro;
            }   
        }
        return null;
    }

    public RegistroDePontoM marcarEntrada(RegistroDePontoM registro) {
        registro.setEntrada(Instant.now());
        return repository.save(registro);
    }

    public RegistroDePontoM marcarSaida(List<RegistroDePontoM> registros) {
        RegistroDePontoM ultimoRegistro = registros.get(registros.size() - 1);
        ultimoRegistro.setSaida(Instant.now());
        return repository.save(ultimoRegistro);
    }

    public void deletarRegistro(Long id) {
        repository.deleteById(id);
    }

    public RegistroDePontoM atualizarResgistro(Long id, RegistroDePontoM novosDados) {
        RegistroDePontoM registro = repository.getReferenceById(id);
        atualizarRegistroDePonto(registro, novosDados);
        return repository.save(registro);
    }

    private void atualizarRegistroDePonto(RegistroDePontoM registro, RegistroDePontoM novosDados) {
        registro.setEntrada(novosDados.getEntrada());
        registro.setSaida(novosDados.getSaida());
    }

    public boolean isPresent(Long usuarioId, Instant date) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado!"));
        for (RegistroDePontoM x : usuario.getRegistroDePontosM()) {
            if (date.equals(x.getEntrada())) {
                return true;
            }
        }
        return false;
    }
}
