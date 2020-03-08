package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.App.model.AppConfig;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, Long>{

}
