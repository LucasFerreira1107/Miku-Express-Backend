package com.mikuexpress.mikuexpress.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikuexpress.mikuexpress.entity.UserGeneric;

public interface UserGenericRepository extends JpaRepository<UserGeneric, Long> {

	Optional<UserGeneric> findByEmail(String email);
	boolean existsByEmail(String email);
}
