package com.dhavyd.login.servico;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhavyd.login.entidades.RegistroDePonto;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.repositorios.RegistroDePontoRepository;
import com.dhavyd.login.repositorios.UsuarioRepository;
import com.dhavyd.login.servico.execoes.RecursoNaoEncontrado;

@Service
public class RegistroDePontoService {
    
    @Autowired
    private RegistroDePontoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RegistroDePonto> buscarTodos() {
        return repository.findAll();
    }

    public RegistroDePonto buscarPorId(Long id) throws Exception {
        RegistroDePonto registro = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Recurso não encontrado!"));
        return registro;
    }

    public List<RegistroDePonto> buscarPorIdUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new RecursoNaoEncontrado("Usuário sem horas registradas!"));
        return usuario.getRegistroDePontos();
    }

    public RegistroDePonto buscarPorUsuarioIdAndData(Long usuarioId, LocalDate hoje) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("id"));
        RegistroDePonto registro = null;
        ZoneId zoneId = ZoneId.systemDefault();

        for (RegistroDePonto x : usuario.getRegistroDePontos()) {
            LocalDate ld = x.getEntrada().atZone(zoneId).toLocalDate();
            if (ld.equals(hoje)) {
                registro = new RegistroDePonto(x.getId(), x.getUsuario(), x.getEntrada(), x.getSaida());
                return registro;
            }   
        }
        return null;
    }

    public RegistroDePonto marcarEntrada(RegistroDePonto registro) {
        registro.setEntrada(Instant.now());
        return repository.save(registro);
    }

    public RegistroDePonto marcarSaida(List<RegistroDePonto> registros) {
        RegistroDePonto ultimoRegistro = registros.get(registros.size() - 1);
        ultimoRegistro.setSaida(Instant.now());
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

    public boolean isPresent(Long usuarioId, Instant date) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado!"));
        for (RegistroDePonto x : usuario.getRegistroDePontos()) {
            if (date.equals(x.getEntrada())) {
                return true;
            }
        }
        return false;
    }
}
