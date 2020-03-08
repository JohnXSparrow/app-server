package com.App.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.App.job.LockJob;

@Repository
public interface LockJobRepository  extends JpaRepository<LockJob, Long> {

}
