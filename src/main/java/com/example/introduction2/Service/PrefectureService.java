package com.example.introduction2.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.introduction2.entity.Prefecture;
import com.example.introduction2.repository.PrefectureRepository;

@Service
public class PrefectureService {

	@Autowired
	private PrefectureRepository prefectureRepository;

	public List<Prefecture> getPrefectures() {
		return prefectureRepository.findAll();
	}

	public String getName(String id) {
		return prefectureRepository.findById(id).orElseThrow().getName();
	}
}
