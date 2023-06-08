package com.example.introduction2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.introduction2.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public Page<User> findByGenderId(Integer genderId, Pageable pageable);

    public Page<User> findByPrefectureId(Integer prefectureId, Pageable pageable);
}
