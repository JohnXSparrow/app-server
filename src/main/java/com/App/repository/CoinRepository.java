package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.App.model.Coin;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {

}
