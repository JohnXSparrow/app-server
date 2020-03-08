package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
	
	@Query(value = "Select u from Team u where u.nameTeam=:pnameTeam")
	public Team findByName(@Param("pnameTeam") String nameTeam);
		
	@Query(value = "Select u from Team u where u.nameTeam LIKE CONCAT(:pnameTeam,'%')")
	public Collection<Team> findAllByName(@Param("pnameTeam") String nameTeam);

}
