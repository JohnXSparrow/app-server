package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.App.model.UserNotifications;

@Repository
public interface UserNotificationsRepository extends JpaRepository<UserNotifications, Long> {

}
