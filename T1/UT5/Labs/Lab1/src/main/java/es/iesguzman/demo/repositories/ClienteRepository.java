package es.iesguzman.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesguzman.demo.models.Cliente;


public interface ClienteRepository extends JpaRepository<Cliente, Long> { }