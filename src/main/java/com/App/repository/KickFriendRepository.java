package com.App.repository;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.App.enumeration.KickFriendEnum;
import com.App.model.KickFriend;

public interface KickFriendRepository extends JpaRepository<KickFriend, Long> {
	
	@Query(value = "Select u from KickFriend u where u.challenged.id_userlogin=:pidChallenged and u.gameMatch.isClosed IS FALSE and u.gameMatch.isKickDisabled IS FALSE and u.wasProccessed=:pwasProccessed and u.status=:pstatus ORDER BY u.dateKick desc")
	public Collection<KickFriend> findAllToAccept(
			@Param("pidChallenged") long idChallenged, 
			@Param("pwasProccessed") boolean wasProccessed,
			@Param("pstatus") KickFriendEnum status);
	
	@Query(value = "Select u from KickFriend u where u.id_kickfriend=:pidKickFriend and u.challenged.id_userlogin=:pidChallenged")
	public KickFriend findKickFriendToAccept(
			@Param("pidKickFriend") long idKickFriend, 
			@Param("pidChallenged") long idChallenged);
	
	@Query(value = "Select u from KickFriend u where (u.challenger.id_userlogin=:pidChallenge or u.challenged.id_userlogin=:pidChallenge) and u.wasProccessed=:pwasProccessed and u.status=:pstatus and u.dateKick BETWEEN :stDate AND :edDate ORDER BY u.dateKick desc")
	public Collection<KickFriend> findAllKickFriendChallenge(
			@Param("pidChallenge") long idChallenge, 
			@Param("pwasProccessed") boolean wasProccessed,
			@Param("pstatus") KickFriendEnum status,
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);
	
	@Query(value = "Select u from KickFriend u where u.challenger.id_userlogin=:pidChallenge and u.wasProccessed=:pwasProccessed and u.status=:pstatus and u.dateKick BETWEEN :stDate AND :edDate ORDER BY u.dateKick desc")
	public Collection<KickFriend> findAllKickFriendChallengeWaiting(
			@Param("pidChallenge") long idChallenge, 
			@Param("pwasProccessed") boolean wasProccessed,
			@Param("pstatus") KickFriendEnum status,
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);
	
	@Query(value = "Select u from KickFriend u where u.challenger.id_userlogin=:pidChallenge and u.status=:pstatus and u.dateKick BETWEEN :stDate AND :edDate ORDER BY u.dateKick desc")
	public Collection<KickFriend> findAllKickFriendChallengeRefused(
			@Param("pidChallenge") long idChallenge,
			@Param("pstatus") KickFriendEnum status,
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);
	
	@Query(value = "Select u from KickFriend u where u.gameMatch.id_gamematch=:pgameMatch and u.wasProccessed=:pwasProccessed")
	public Collection<KickFriend> findAllByGameMatch(
			@Param("pgameMatch") long gameMatch,
			@Param("pwasProccessed") boolean wasProccessed);

	
}
