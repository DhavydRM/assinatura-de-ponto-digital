package com.dhavyd.login.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dhavyd.login.entidades.RegistroDePontoT;

@Repository
public interface RegistroDePontoTRepository extends JpaRepository<RegistroDePontoT, Long> {
    
}
