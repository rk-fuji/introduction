package com.example.introduction2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.introduction2.entity.JobCareer;

public interface JobCareerRepository extends JpaRepository<JobCareer, Integer> {

}
