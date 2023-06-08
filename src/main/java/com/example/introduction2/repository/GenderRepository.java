package com.example.introduction2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.introduction2.entity.Gender;

public interface GenderRepository extends JpaRepository<Gender, Integer> {

}
