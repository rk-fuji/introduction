package com.example.introduction2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.introduction2.entity.UserHobby;

public interface UserHobbyRepository extends JpaRepository<UserHobby, Integer> {

	public List<UserHobby> findByUserId(Integer userId);
	public void deleteByUserId(Integer userId);
}
