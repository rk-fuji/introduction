package com.example.introduction2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.introduction2.entity.Hobby;

public interface HobbyRepository extends JpaRepository<Hobby, String> {

}
