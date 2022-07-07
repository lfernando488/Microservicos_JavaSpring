package com.lfernando488.mscartoes.infra.repository;

import com.lfernando488.mscartoes.domain.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CartaoRepository extends JpaRepository <Cartao, Long> {
    List<Cartao> findByRendaLessThanEqual(BigDecimal rendaBigDecimal);
}
