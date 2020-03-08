package com.App.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.App.model.Support;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {

	@Query(value = "Select u from Support u where u.userLogin.id_userlogin=:puserLoginID and u.isClosed=:pisClosed ORDER BY u.dateCreated desc")
	public Collection<Support> findAllOpenedByUser(@Param("puserLoginID") long userLoginID,	@Param("pisClosed") boolean isClosed);
	
	@Query(value = "Select u from Support u where u.userLogin.id_userlogin=:puserLoginID and u.id_support=:pid_support")
	public Support findOneByUserAndSupportId(@Param("puserLoginID") long userLoginID, @Param("pid_support") long id_support);
	
	@Query(value = "Select u from Support u where u.answer=:panswer ORDER BY u.id_support")
	public Collection<Support> findAllOpened(@Param("panswer") String answer);

}
