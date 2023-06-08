package com.example.introduction2.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.introduction2.dto.JobCareerRequest;
import com.example.introduction2.dto.SearchRequest;
import com.example.introduction2.dto.UserRequest;
import com.example.introduction2.entity.Career;
import com.example.introduction2.entity.JobCareer;
import com.example.introduction2.entity.User;
import com.example.introduction2.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserHobbyService userHobbyService;

	@Autowired
	private JobCareerService jobCareerService;

	@Autowired
	private GenderService genderService;

	@Autowired
	private PrefectureService prefectureService;

	@Autowired
	private HobbyService hobbyService;

	@PersistenceContext
	private EntityManager entityManager;

	// Logger
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	public Page<User> getUserPages(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	public User getUser(Integer id) {
		return userRepository.findById(id).orElseThrow();
	}

	/**
	 * 絞り込み検索
	 * 
	 * @param searchRequest
	 * @param pageable
	 * @return
	 */
	public Page<User> search(SearchRequest searchRequest, Pageable pageable) {

		log.info(searchRequest.toString());

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);

		// from
		Root<User> user = cq.from(User.class);

		// join
		Join<User, JobCareer> jobCareer = user.join("jobCareer", JoinType.LEFT);
		Join<JobCareer, Career> career = jobCareer.join("careers", JoinType.LEFT);

		// 検索条件判定
		List<Predicate> predicates = new ArrayList<>();
		if (searchRequest.getGender() != 0) {
			predicates.add(cb.equal(user.get("genderId"), searchRequest.getGender()));
		}
		if (searchRequest.getPrefecture() != 0) {
			predicates.add(cb.equal(user.get("prefectureId"), searchRequest.getPrefecture()));
		}
		if (!searchRequest.getUseLanguage().isEmpty()) {
			predicates.add(cb.like(career.get("useLanguage"), "%" + searchRequest.getUseLanguage() + "%"));
		}
		if (!searchRequest.getOther().isEmpty()) {
			predicates.add(cb.like(career.get("other"), "%" + searchRequest.getOther() + "%"));
		}

		// 検索条件設定(or / and)
		Predicate finalPredicate;
		if (searchRequest.getSearchPattern() == 1) {
			finalPredicate = cb.or(predicates.toArray(new Predicate[0]));
		} else {
			finalPredicate = cb.and(predicates.toArray(new Predicate[0]));
		}

		// 検索条件セット
		cq.where(finalPredicate);

		// クエリ実行
		TypedQuery<User> query = entityManager.createQuery(cq);

		// ページネーション設定
		int totalRows = query.getResultList().size();
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		return new PageImpl<>(query.getResultList(), pageable, totalRows);
	}

	/**
	 * ユーザー情報を登録する
	 * 
	 * @param userRequest
	 * @return
	 */
	public User create(UserRequest userRequest) {
		var user = new User();
		user.setName(userRequest.getName());
		user.setAge(userRequest.getAge());
		user.setGenderId(Integer.parseInt(userRequest.getGender()));
		user.setPrefectureId(Integer.parseInt(userRequest.getPrefecture()));
		user.setAddress(userRequest.getAddress());
		user.setIntroduction(userRequest.getIntroduction());
		user.setPicture(userRequest.getPictureFileName());
		userRepository.save(user);

		return user;
	}

	/**
	 * ユーザー情報登録
	 * 
	 * users, user_hobbies, job_careers へ登録する
	 * 
	 * @param userRequest
	 */
	@Transactional
	public void createUser(UserRequest userRequest) {
		var user = create(userRequest);
		if (userRequest.getHobby() != null) {
			Arrays.stream(userRequest.getHobby())
					.map(Integer::parseInt)
					.forEach(hobbyId -> userHobbyService.create(user, hobbyId));
		}
		jobCareerService.createJobCareer(user);
	}

	public User update(Integer id, UserRequest userRequest) {
		var user = getUser(id);
		user.setName(userRequest.getName());
		user.setAge(userRequest.getAge());
		user.setGenderId(Integer.parseInt(userRequest.getGender()));
		user.setPrefectureId(Integer.parseInt(userRequest.getPrefecture()));
		user.setAddress(userRequest.getAddress());
		user.setIntroduction(userRequest.getIntroduction());
		// 画像はセットされていた場合のみ更新を行う
		if (userRequest.getPictureFileName() != null && !userRequest.getPictureFileName().isEmpty()) {
			user.setPicture(userRequest.getPictureFileName());
		}
		userRepository.save(user);

		return user;
	}

	/**
	 * 職務経歴書編集画面の入力情報による更新
	 * 
	 * @param id
	 * @param jobCareerRequest
	 * @return
	 */
	public User jobCareerUpdate(Integer id, JobCareerRequest jobCareerRequest) {
		var user = getUser(id);
		user.setName(jobCareerRequest.getName());
		user.setAge(jobCareerRequest.getAge());
		user.setGenderId(Integer.parseInt(jobCareerRequest.getGender()));
		userRepository.save(user);
		return user;
	}

	public void delete(Integer id) {
		var user = getUser(id);
		userRepository.delete(user);
	}

	public void setFormContent(Model model) {
		var genders = genderService.findAll();
		var prefectures = prefectureService.getPrefectures();
		var hobbies = hobbyService.findAll();
		model.addAttribute("genders", genders);
		model.addAttribute("prefectures", prefectures);
		model.addAttribute("hobbies", hobbies);
	}
}
