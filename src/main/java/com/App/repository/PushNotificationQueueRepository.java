package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.PushNotificationQueue;

@Repository
public interface PushNotificationQueueRepository extends JpaRepository<PushNotificationQueue, Long>{

	@Query(value = "Select u from PushNotificationQueue u where u.userLogin=:puserlogin and u.gameMatch=:pgamematch")
	public Collection<PushNotificationQueue> findByUserLoginAndGameMatch(@Param("puserlogin") long userlogin, @Param("pgamematch") long gamematch);
	
}
