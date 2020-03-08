package com.App.repository;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.AppAccess;

@Repository
public interface AppAccessRepository extends JpaRepository<AppAccess, Long> {
	
	@Query(value = "Select u from AppAccess u where u.idUserLogin=:pidUserLogin")
	public AppAccess findByUserId(@Param("pidUserLogin") long idUserLogin);
	
	@Query(value = "Select u from AppAccess u where u.lastAccess BETWEEN :stDate AND :edDate ORDER BY u.qttAccess desc")
	public Collection<AppAccess> findByLastAccess(@Param("stDate") Calendar stDate, @Param("edDate") Calendar edDate);

}
