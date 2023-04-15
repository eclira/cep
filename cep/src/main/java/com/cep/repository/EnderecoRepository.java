package com.cep.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cep.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

}
