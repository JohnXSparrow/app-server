package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.City;
import com.App.model.State;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
	
	@Query(value = "Select u from City u where u.nameCity=:pnameCity")
	public City findByName(@Param("pnameCity") String nameCity);
	
	@Query(value = "Select u from City u where u.state=:pstate")
	public Collection<City> findAllByState(@Param("pstate") State state);

}
	