package com.example.introduction2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.introduction2.entity.Prefecture;

public interface PrefectureRepository extends JpaRepository<Prefecture, String> {

}
