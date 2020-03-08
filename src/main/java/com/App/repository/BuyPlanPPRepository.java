package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.BuyPlanPP;

@Repository
public interface BuyPlanPPRepository extends JpaRepository<BuyPlanPP, Long> {
		
	@Query(value = "Select u from BuyPlanPP u where u.pay_id=:ppay_id and u.status=:pstatus")
	public BuyPlanPP findByPay_id(@Param("ppay_id") String id_pay, @Param("pstatus") Enum<?> status);

}
