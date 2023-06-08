package com.example.introduction2.Service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.introduction2.entity.User;
import com.example.introduction2.entity.UserHobby;
import com.example.introduction2.repository.HobbyRepository;
import com.example.introduction2.repository.UserHobbyRepository;

import jakarta.transaction.Transactional;

@Service
public class UserHobbyService {

	@Autowired
	private HobbyRepository hobbyRepository;

	@Autowired
	private UserHobbyRepository userHobbyRepository;

	public Integer create(User user, Integer hobbyId) {
		var userHobby = new UserHobby();
		userHobby.setUser(user);
		userHobby.setHobbyId(hobbyId);
		userHobbyRepository.save(userHobby);
		return userHobby.getId();
	}

	@Transactional
	public void deleteAll(Integer userId) {
		userHobbyRepository.deleteByUserId(userId);
	}

	public String joinHobbies(Integer userId) {
		var userHobbies = userHobbyRepository.findByUserId(userId);

		var joinHobbies = "";
		if (userHobbies.size() > 0) {
			var hobbyIds = new ArrayList<String>();
			for (var userHobby : userHobbies) {
				hobbyIds.add(hobbyRepository.findById(userHobby.getHobbyId().toString()).orElseThrow().getName());
			}
			joinHobbies = String.join(" ", hobbyIds);
		}

		return joinHobbies;
	}

}
