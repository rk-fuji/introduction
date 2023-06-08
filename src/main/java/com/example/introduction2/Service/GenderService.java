package com.example.introduction2.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.introduction2.entity.Gender;
import com.example.introduction2.repository.GenderRepository;

@Service
public class GenderService {

	@Autowired
	private GenderRepository genderRepository;

	/**
	 * 全件検索
	 * 
	 * @return
	 */
	public List<Gender> findAll() {
		return genderRepository.findAll();
	}

	public String getName(Integer id) {
		return genderRepository.findById(id).orElseThrow().getName();
	}
}
