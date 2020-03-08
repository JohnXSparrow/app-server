package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.enumeration.UserStatusEnum;
import com.App.model.UserLogin;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, Long> {
	
	@Query(value = "Select u from UserLogin u where u.username=:pusername")
	public UserLogin findByUsername(@Param("pusername") String username);
	
	@Query(value = "Select u from UserLogin u where u.id_userlogin=:puserLogin and u.tokenCode=:ptokenCode")
	public UserLogin confirmAccount(@Param("puserLogin") Long userLogin, @Param("ptokenCode") String tokenCode);
	
	@Query(value = "Select count(*) from UserLogin")
	public long countUserTotal();
	
	@Query(value = "Select count(*) from OauthAccessToken")
	public long countLoggedUser();
	
	@Query(value = "Select count(*) from UserLogin u where u.userStatus=:puserStatus")
	public long countUserActive(@Param("puserStatus") UserStatusEnum status);
	
	@Query(value = "Select u from UserLogin u where u.userStatus=:puserStatus")
	public Collection<UserLogin> findUsersByStatus(@Param("puserStatus") UserStatusEnum status);
	
	@Query(value = "Select u from UserLogin u where u.user.email=:pemail")
	public UserLogin findOneByEmail(@Param("pemail") String email);
	
	

}
