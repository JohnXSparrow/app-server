package com.App.repository;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.Coin;
import com.App.model.GameMatch;
import com.App.model.Kick;
import com.App.model.Team;
import com.App.model.UserLogin;

@Repository
public interface KickRepository extends JpaRepository<Kick, Long> {

	@Query(value = "Select u from Kick u where (u.gameMatch.id_gamematch=:pgameMatch) and (u.coin=:pcoin) and (u.userLogin=:puserLogin)")
	public Collection<Kick> findValidKick(@Param("pgameMatch") long gameMatch, @Param("pcoin") Coin coin,
			@Param("puserLogin") UserLogin userLogin);

	@Query(value = "Select u from Kick u where (u.gameMatch.id_gamematch=:pgameMatch) and (u.team=:pteam) and (u.coin=:pcoin) and (u.valueKick=:pvalueKick) and oponent IS NULL")
	public Collection<Kick> findOponent(@Param("pgameMatch") long gameMatch, @Param("pteam") Team team,
			@Param("pcoin") Coin coin, @Param("pvalueKick") long valueKick);
	
	@Query(value = "Select u from Kick u where u.gameMatch.id_gamematch=:pgameMatch and u.userLogin=:puserLogin")
	public Collection<Kick> findKicksRepet(@Param("pgameMatch") long gameMatch, @Param("puserLogin") UserLogin userLogin);
	
	@Query(value = "Select u from Kick u where (u.gameMatch.id_gamematch=:pgameMatch) and (u.wasProccessed=:pwasProccessed)")
	public Collection<Kick> findAllByGameMatch(@Param("pgameMatch") long gameMatch,
			@Param("pwasProccessed") boolean wasProccessed);
	
	@Query(value = "Select u from Kick u where u.userLogin=:puserLogin and u.wasProccessed=:pwasProccessed")
	public Collection<Kick> findKicksDoneGM(@Param("puserLogin") UserLogin userLogin, @Param("pwasProccessed") boolean wasProccessed);
	
	@Query(value = "Select u from Kick u where u.userLogin=:puserLogin and u.wasProccessed IS FALSE and u.dateKick BETWEEN :stDate AND :edDate ORDER BY u.dateKick desc")
	public Collection<Kick> findKicksDoneInProgress(
			@Param("puserLogin") UserLogin userLogin, 
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);
	
	@Query(value = "Select u from Kick u where u.userLogin=:puserLogin and u.wasProccessed IS TRUE and u.dateKick BETWEEN :stDate AND :edDate ORDER BY u.result.id_result desc")
	public Collection<Kick> findKicksDoneFinalized(
			@Param("puserLogin") UserLogin userLogin, 
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);
	
	@Query(value = "Select u from Kick u where u.userLogin=:puserLogin and u.wasProccessed IS FALSE and u.dateKick BETWEEN :stDate AND :edDate")
	public Collection<Kick> findKicksDoneAll(
			@Param("puserLogin") UserLogin userLogin, 
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);
	
	@Query(value = "Select u from Kick u where u.userLogin=:puserLogin and u.wasProccessed IS FALSE and u.gameMatch=:pgameMatch")
	public Collection<Kick> findKicksDoneByGameMatch(
			@Param("puserLogin") UserLogin userLogin, 
			@Param("pgameMatch") GameMatch gameMatch);
	
	@Query(value = "Select count(*) from Kick u where u.gameMatch.id_gamematch=:pid_gamematch and u.team.id_team=:pid_team and u.coin.id_coin=:pid_coin and u.valueKick=:pvalueKick")
	public int countAmountKick(
			@Param("pid_gamematch") long id_gameMatch,
			@Param("pid_team") long id_team,
			@Param("pid_coin") long id_coin,
			@Param("pvalueKick") long valueKick);

}
