package com.dhavyd.login.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dhavyd.login.entidades.RegistroDePonto;

@Repository
public interface RegistroDePontoRepository extends JpaRepository<RegistroDePonto, Long> {
    
}
