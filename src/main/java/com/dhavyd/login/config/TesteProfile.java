package com.dhavyd.login.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.dhavyd.login.entidades.Usuario;
import com.dhavyd.login.repositorios.UsuarioRepository;

@Configuration
@Profile("test")
public class TesteProfile implements CommandLineRunner{

    @Autowired
    UsuarioRepository repository;

    @Override
    public void run(String... args) throws Exception {
        
        Usuario us1 = new Usuario(null, "Dhavyd", "dhavyd@gmail.com", "Dhavyd009");
        Usuario us2 = new Usuario(null, "Ingrid", "ingrid@gmail.com", "Ingrid12@009");
        Usuario us3 = new Usuario(null, "Felipe", "felipe@gmail.com", "Coelho029@");

        repository.saveAll(Arrays.asList(us1, us2, us3));
    }
    
}
