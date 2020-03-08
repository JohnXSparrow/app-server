package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.App.model.RankingPotCoin;

public interface RankingPotCoinRepository extends JpaRepository<RankingPotCoin, Long> {
	
	@Transactional	
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE ranking_pot_coin s SET s.amount_coin = 0")
	public void resetRankingPotCoin();

}
