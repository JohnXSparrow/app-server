package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

	@Query(value = "Select u from Country u where u.nameCountry=:pnameCountry")
	public Country findByName(@Param("pnameCountry") String nameCountry);
	
	@Query(value = "Select u from Country u where u.initials=:pinitials")
	public Country findByInitials(@Param("pinitials") String initials);

}
