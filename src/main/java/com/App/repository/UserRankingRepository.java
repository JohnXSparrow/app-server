package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.App.model.UserRanking;

public interface UserRankingRepository extends JpaRepository<UserRanking, Long> {

	@Transactional
	@Modifying
	@Query(value = "Delete from UserRanking where ranking_id_ranking=:pidRanking")
	public void deleteUserRankingByIdRanking(@Param("pidRanking") long idRanking);
	
	@Query(value = "Select u from UserRanking u where u.ranking.id_ranking=:pidRanking")
	public Collection<UserRanking> findAllByIdRanking(@Param("pidRanking") long idRanking);	
	
	@Query(value = "Select u from UserRanking u where u.ranking.date=:pdate")
	public Collection<UserRanking> findAllByDate(@Param("pdate") String date);
	
}
