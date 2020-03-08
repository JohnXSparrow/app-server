package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.BuyCoin;

@Repository
public interface BuyCoinRepository extends JpaRepository<BuyCoin, Long> {
	
	@Query(value = "Select u from BuyCoin u where u.pay_id=:ppay_id and u.status=:pstatus")
	public BuyCoin findByPay_id(@Param("ppay_id") String id_pay, @Param("pstatus") Enum<?> status);
	
	@Query(value = "Select u from BuyCoin u where u.status=:pstatus")
	public Collection<BuyCoin> getAllByStatus(@Param("pstatus") Enum<?> status);
	
	@Query(value = "Select u from BuyCoin u where u.userLogin.id_userlogin=:puserLoginID ORDER BY u.id_buycoin desc")
	public Collection<BuyCoin> findAllByUserLoginID(@Param("puserLoginID") long userLoginID);

}
