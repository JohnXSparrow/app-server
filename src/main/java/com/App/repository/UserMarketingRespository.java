package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.App.model.UserMarketing;

public interface UserMarketingRespository extends JpaRepository<UserMarketing, Long> {

	@Query(nativeQuery = true, value = "SELECT * FROM user_marketing u where u.was_sent=:pwasSent and u.is_unsubscribe=:pisUnsubscribe and u.email NOT LIKE CONCAT('%',:pemail) LIMIT 500")
	public Collection<UserMarketing> findAllNotSentAndIsUnsubscribe(
			@Param("pwasSent") String wasSent, 
			@Param("pisUnsubscribe") String isUnsubscribe, 
			@Param("pemail") String email);
	
	@Query(value = "Select u from UserMarketing u where u.id_usermarketing=:pIdUserMarketing and u.tokenToUnsubscribe=:ptoken")
	public UserMarketing getUserMarketing(@Param("pIdUserMarketing") Long IdUserMarketing, @Param("ptoken") String token);
	
}
