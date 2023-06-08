package com.example.introduction2.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.introduction2.entity.Hobby;
import com.example.introduction2.repository.HobbyRepository;

@Service
public class HobbyService {
	@Autowired
	private HobbyRepository hobbyRepository;

	public List<Hobby> findAll() {
		return hobbyRepository.findAll();
	}

	public String getName(String id) {
		return hobbyRepository.findById(id).orElse(new Hobby()).getName();
	}
}
