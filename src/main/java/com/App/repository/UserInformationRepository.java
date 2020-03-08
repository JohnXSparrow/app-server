package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.App.model.UserInformation;

@Repository
public interface UserInformationRepository extends JpaRepository<UserInformation, Long> {
	
	
}
