package com.dhavyd.login.servico;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhavyd.login.entidades.RegistroDePontoT;
import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.repositorios.RegistroDePontoTRepository;
import com.dhavyd.login.repositorios.UsuarioRepository;
import com.dhavyd.login.servico.execoes.RecursoNaoEncontrado;

@Service
public class RegistroDePontoTService {
    
    @Autowired
    private RegistroDePontoTRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<RegistroDePontoT> buscarTodos() {
        return repository.findAll();
    }

    public RegistroDePontoT buscarPorId(Long id) throws Exception {
        RegistroDePontoT registro = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontrado("Recurso não encontrado!"));
        return registro;
    }

    public List<RegistroDePontoT> buscarPorIdUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(() -> new RecursoNaoEncontrado("Usuário sem horas registradas!"));
        return usuario.getRegistroDePontosT();
    }

    public RegistroDePontoT buscarPorUsuarioIdAndData(Long usuarioId, LocalDate hoje) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("id"));
        RegistroDePontoT registro = null;
        ZoneId zoneId = ZoneId.systemDefault();

        for (RegistroDePontoT x : usuario.getRegistroDePontosT()) {
            LocalDate ld = x.getEntrada().atZone(zoneId).toLocalDate();
            if (ld.equals(hoje)) {
                registro = new RegistroDePontoT(x.getId(), x.getUsuario(), x.getEntrada(), x.getSaida());
                return registro;
            }   
        }
        return null;
    }

    public RegistroDePontoT marcarEntrada(RegistroDePontoT registro) {
        registro.setEntrada(Instant.now());
        return repository.save(registro);
    }

    public RegistroDePontoT marcarSaida(List<RegistroDePontoT> registros) {
        RegistroDePontoT ultimoRegistro = registros.get(registros.size() - 1);
        ultimoRegistro.setSaida(Instant.now());
        return repository.save(ultimoRegistro);
    }

    public void deletarRegistro(Long id) {
        repository.deleteById(id);
    }

    public RegistroDePontoT atualizarResgistro(Long id, RegistroDePontoT novosDados) {
        RegistroDePontoT registro = repository.getReferenceById(id);
        atualizarRegistroDePonto(registro, novosDados);
        return repository.save(registro);
    }

    private void atualizarRegistroDePonto(RegistroDePontoT registro, RegistroDePontoT novosDados) {
        registro.setEntrada(novosDados.getEntrada());
        registro.setSaida(novosDados.getSaida());
    }

    public boolean isPresent(Long usuarioId, Instant date) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontrado("Usuário não encontrado!"));
        for (RegistroDePontoT x : usuario.getRegistroDePontosT()) {
            if (date.equals(x.getEntrada())) {
                return true;
            }
        }
        return false;
    }
}
