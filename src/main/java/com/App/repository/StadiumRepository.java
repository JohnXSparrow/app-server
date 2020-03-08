package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.City;
import com.App.model.Stadium;

@Repository
public interface StadiumRepository extends JpaRepository<Stadium, Long> {
	
	@Query(value = "Select u from Stadium u where u.nameStadium=:pnameStadium")
	public Stadium findByName(@Param("pnameStadium") String nameStadium);
	
	@Query(value = "Select u from Stadium u where u.city=:pcity")
	public Collection<Stadium> findAllByCity(@Param("pcity") City city);
	
	@Query(value = "Select u from Stadium u where u.nameStadium LIKE CONCAT(:pnameStadium,'%')")
	public Collection<Stadium> findAllByName(@Param("pnameStadium") String nameStadium);

}
