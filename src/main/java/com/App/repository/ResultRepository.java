package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.GameMatch;
import com.App.model.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

	// Verifica se já existe um resultado para essa gamaMatch
	@Query(value = "Select u from Result u where u.gameMatch=:pgameMatch")
	public Result findByGameMatchId(@Param("pgameMatch") GameMatch gameMatch);

}
	