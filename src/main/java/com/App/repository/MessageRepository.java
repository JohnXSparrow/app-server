package com.App.repository;

import java.util.Calendar;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.App.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
	
	@Query(value = "Select u from Message u where u.date BETWEEN :stDate AND :edDate ORDER BY u.date desc")
	public Collection<Message> listMessages(
			@Param("stDate") Calendar stDate, 
			@Param("edDate") Calendar edDate);

}
