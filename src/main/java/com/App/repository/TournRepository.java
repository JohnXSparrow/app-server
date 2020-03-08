package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.Tourn;

@Repository
public interface TournRepository extends JpaRepository<Tourn, Long> {

	@Query(value = "Select u from Tourn u where u.nameTourn=:pnameTourn")
	public Tourn findByName(@Param("pnameTourn") String nameTourn);

}
