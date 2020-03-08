package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.Country;
import com.App.model.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
	
	@Query(value = "Select u from State u where u.nameState=:pnameState")
	public State findByName(@Param("pnameState") String nameState);
	
	@Query(value = "Select u from State u where u.country=:pcountry")
	public Collection<State> findAllByCountry(@Param("pcountry") Country country);

}
