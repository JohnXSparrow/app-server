package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "Select u from User u where u.email=:pemail")
	public User findByEmail(@Param("pemail") String email);
	
	@Query(value = "Select u from User u where u.cpf=:pcpf")
	public User findByCPF(@Param("pcpf") String cpf);	
	
}
