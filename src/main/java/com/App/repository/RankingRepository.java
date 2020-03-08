package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.App.model.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
	
	@Query(value = "Select u from Ranking u where u.date=:pdate")
	public Ranking findOneByDate(@Param("pdate") String date);

}
