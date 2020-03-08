package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.security.service.OauthAccessToken;

@Repository
public interface OauthAccessTokenRepository extends JpaRepository<OauthAccessToken, Long> {

	@Query(value = "Select u from OauthAccessToken u where u.user_name=:pusername")
	public OauthAccessToken findByUsername(@Param("pusername") String username);
}
