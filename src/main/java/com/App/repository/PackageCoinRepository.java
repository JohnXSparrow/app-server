package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.App.model.PackageCoin;

@Repository
public interface PackageCoinRepository extends JpaRepository<PackageCoin, Long> {

}
