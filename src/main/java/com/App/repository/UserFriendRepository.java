package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.App.model.UserFriend;

@Repository
public interface UserFriendRepository extends JpaRepository<UserFriend, Long>{

}
