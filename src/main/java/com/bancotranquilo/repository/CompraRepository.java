package com.bancotranquilo.repository;

import com.bancotranquilo.model.Compra;
import com.bancotranquilo.model.StatusCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByStatus(StatusCompra status);
    Optional<Compra> findByIdAndStatus(Long id, StatusCompra status);
}


