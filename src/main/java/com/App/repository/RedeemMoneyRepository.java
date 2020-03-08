package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.RedeemMoney;
import com.App.model.UserLogin;

@Repository
public interface RedeemMoneyRepository extends JpaRepository<RedeemMoney, Long>{
	
	@Query(value = "Select u from RedeemMoney u where u.userLogin=:puserLogin ORDER BY u.dateRequest desc")
	public Collection<RedeemMoney> findAllByUser(@Param("puserLogin") UserLogin userLogin);

}
