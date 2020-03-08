package com.App.repository;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.ExchangeCoin;
import com.App.model.UserLogin;

@Repository
public interface ExchangeCoinRepository extends JpaRepository<ExchangeCoin, Long> {

	@Query(value = "Select u from ExchangeCoin u where u.userLogin=:puserLogin and u.dateExchange BETWEEN :stDate AND :edDate ORDER BY u.dateExchange desc")
	public Collection<ExchangeCoin> findAllExchangeCoinByUserAndTime(
			@Param("puserLogin") UserLogin userLogin, 
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);

}
