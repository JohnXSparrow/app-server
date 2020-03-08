package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.BuyPlanGP;

@Repository
public interface BuyPlanGPRepository extends JpaRepository<BuyPlanGP, Long>  {
	
	@Query(value = "Select u from BuyPlanGP u where u.orderId=:porderId")
	public BuyPlanGP findByOrderId(@Param("porderId") String orderId);

}
