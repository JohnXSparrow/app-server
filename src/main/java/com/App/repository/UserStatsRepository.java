package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.App.model.UserStats;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
	
	@Query(nativeQuery = true, value = "SELECT * FROM user_stats s ORDER BY s.win DESC, s.defeat, s.atie, s.kicks DESC, s.win_general DESC, s.defeat_general, s.atie_general, s.kicks_general DESC LIMIT 30")
	public Collection<UserStats> getUserStatsFilter();
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE user_stats u SET u.win = 0, u.defeat = 0, u.atie = 0, u.kicks = 0")
	public void resetUserStats();
	
	@Query(nativeQuery = true, value = "SELECT position FROM (SELECT t.*, @rownum \\:= @rownum + 1 AS position FROM user_stats t JOIN (SELECT @rownum \\:= 0) r ORDER BY t.win DESC, t.defeat, t.atie, t.kicks DESC, t.win_general DESC, t.defeat_general, t.atie_general, t.kicks_general DESC) x WHERE x.id_userstats =:pidUser")
	public int findUserPosition(@Param("pidUser") long idUser);
	
	@Query(nativeQuery = true, value = "SELECT * FROM user_stats s ORDER BY s.win_general DESC, s.defeat_general, s.atie_general, s.kicks_general DESC LIMIT 100")
	public Collection<UserStats> getUserStatsTop100();

}