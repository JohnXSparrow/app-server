package com.App.repository;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.GameMatch;
import com.App.model.Team;

@Repository
public interface GameMatchRepository extends JpaRepository<GameMatch, Long> {

	@Query(value = "Select u from GameMatch u where (u.isClosed=:pisClosed) and (u.isKickDisabled=:pisKickDisabled) ORDER BY startTime")
	public Collection<GameMatch> findListGameMatch(
			@Param("pisClosed") boolean isClosed,
			@Param("pisKickDisabled") boolean isKickDisabled);
	
	@Query(value = "Select u from GameMatch u where (u.isSetResult=:pisSetResult) and (u.isClosed=:pisClosed) ORDER BY startTime")
	public Collection<GameMatch> findListGameMatchByIsSetResult(
			@Param("pisSetResult") boolean isSetResult, 
			@Param("pisClosed") boolean isClosed);

	@Query(value = "Select u from GameMatch u where " 
			+ "((u.teamA=:pteamA or u.teamA=:pteamB) "
			+ "or (u.teamB=:pteamB or u.teamB=:pteamA)) " 
			+ "and (u.dateTime=:pdateTime)")
	public Collection<GameMatch> findGameMatchExist(
			@Param("pteamA") Team teamA, 
			@Param("pteamB") Team teamB,
			@Param("pdateTime") Date dateTime);

	@Query(value = "Select u from GameMatch u where "
			+ "(u.dateTime=:pdateTime) and "
			+ "(u.isClosed=:pisClosedFalse and u.isKickDisabled=:pisKickDisabledFalse) or "
			+ "(u.isClosed=:pisClosedTrue and u.isKickDisabled=:pisKickDisabledFalse)")
	public Collection<GameMatch> findGameMatchJob(
			@Param("pdateTime") Date dateTime,
			@Param("pisClosedFalse") boolean isClosedFalse,	
			@Param("pisClosedTrue") boolean isClosedTrue, 
			@Param("pisKickDisabledFalse") boolean isKickDisabledFalse);

	@Query(value = "Select u from GameMatch u where u.isKickDisabled=:pisKickDisabled and u.isSetResult=:pisSetResult ORDER BY startTime")
	public Collection<GameMatch> findIsKickDisabled(
			@Param("pisKickDisabled") boolean isKickDisabled,
			@Param("pisSetResult") boolean isSetResult);
	
	@Query(value = "Select u from GameMatch u where (u.isClosed=:pisClosed) and (u.isKickDisabled=:pisKickDisabled) and (u.isWeek=:pisWeek) ORDER BY startTime")
	public Collection<GameMatch> findListGameMatchOneWeek(
			@Param("pisClosed") boolean isClosed,
			@Param("pisKickDisabled") boolean isKickDisabled,
			@Param("pisWeek") boolean isWeek);	
		
}
