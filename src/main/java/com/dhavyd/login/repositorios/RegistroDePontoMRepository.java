package com.dhavyd.login.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dhavyd.login.entidades.RegistroDePontoM;

@Repository
public interface RegistroDePontoMRepository extends JpaRepository<RegistroDePontoM, Long> {
    
}
