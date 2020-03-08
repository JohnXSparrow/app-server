package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.UserCoin;

@Repository
public interface UserCoinRepository extends JpaRepository<UserCoin, Long> {
	
	@Query(value = "Select u from UserCoin u where (u.silverCoin<:psilverCoin)")
	public Collection<UserCoin> findUsersCoin(@Param("psilverCoin") long silverCoin);

}
