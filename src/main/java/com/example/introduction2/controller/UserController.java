package com.example.introduction2.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.introduction2.Service.GenderService;
import com.example.introduction2.Service.HobbyService;
import com.example.introduction2.Service.PrefectureService;
import com.example.introduction2.Service.UserHobbyService;
import com.example.introduction2.Service.UserService;
import com.example.introduction2.component.AppProperty;
import com.example.introduction2.dto.SearchRequest;
import com.example.introduction2.dto.UserRequest;
import com.example.introduction2.entity.Gender;
import com.example.introduction2.entity.Prefecture;
import com.example.introduction2.util.ImageUtil;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/users", "/users/" })
@SessionAttributes("userRequest")
public class UserController {

	@Autowired
	private AppProperty appProperty;

	@Autowired
	private GenderService genderService;

	@Autowired
	private PrefectureService prefectureService;

	@Autowired
	private HobbyService hobbyService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserHobbyService userHobbyService;

	// Logger
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private Map<Integer, String> searchPatterns = new HashMap<Integer, String>() {
		{
			put(1, "or");
			put(2, "and");
		}
	};

	@ModelAttribute("userRequest")
	private UserRequest userRequest() {
		return new UserRequest();
	}

	/**
	 * 一覧画面表示
	 * 
	 * @param pageable
	 * @param model
	 * @return 一覧画面
	 */
	@GetMapping("")
	public String list(@PageableDefault(size = 2) Pageable pageable, @ModelAttribute SearchRequest searchRequest,
			Model model) {

		var userPages = userService.getUserPages(pageable);
		var userContents = userPages.getContent();
		var users = new ArrayList<LinkedHashMap<String, String>>();
		for (var user : userContents) {
			var showUsers = new LinkedHashMap<String, String>();
			showUsers.put("id", user.getId().toString());
			showUsers.put("name", user.getName());
			showUsers.put("age", user.getAge().toString());
			showUsers.put("gender", genderService.getName(user.getGenderId()));
			showUsers.put("prefecture", prefectureService.getName(user.getPrefectureId().toString()));
			showUsers.put("address", user.getAddress());
			showUsers.put("hobby", userHobbyService.joinHobbies(user.getId()));
			var picture = ImageUtil.convertPictureToBase64(appProperty.getUploadImagePath(), user.getPicture());
			if (picture.isEmpty()) {
				picture = ImageUtil.convertPictureToBase64(appProperty.getDefaultImagePath(),
						appProperty.getDefaultImageFileName());
			}
			showUsers.put("picture", picture);
			showUsers.put("jobCareerId", user.getJobCareer().getId().toString());
			users.add(showUsers);
		}

		var genders = genderService.findAll();
		var prefectures = prefectureService.getPrefectures();
		genders.add(0, new Gender(0, "---"));
		prefectures.add(0, new Prefecture(0, "---"));

		searchRequest.setSearchPattern(1);

		model.addAttribute("searchRequest", searchRequest);
		model.addAttribute("users", users);
		model.addAttribute("page", userPages);
		model.addAttribute("prefectures", prefectures);
		model.addAttribute("genders", genders);
		model.addAttribute("searchPatterns", searchPatterns);

		return "list";
	}

	/**
	 * 閲覧画面
	 * 
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("{id}")
	public String show(@PathVariable Integer id, Model model) {

		var showUser = new HashMap<String, String>();
		var user = userService.getUser(id);
		var hobbyList = user.getUserHobbies().stream().map(hobby -> hobby.getHobbyId().toString())
				.toArray(String[]::new);
		var picture = "";
		if (user.getPicture() != null && !user.getPicture().isEmpty()) {
			picture = ImageUtil.convertPictureToBase64(appProperty.getUploadImagePath(), user.getPicture());
		}
		showUser.put("name", user.getName());
		showUser.put("age", user.getAge().toString());
		showUser.put("gender", genderService.getName(user.getGenderId()));
		showUser.put("fullAddress", prefectureService.getName(user.getPrefectureId().toString()) + user.getAddress());
		showUser.put("hobbies", convertToNameAndJoinHobbies(hobbyList));
		showUser.put("introduction", user.getIntroduction());
		showUser.put("picture", picture);

		model.addAttribute("user", showUser);
		return "show";
	}

	/**
	 * 新規登録画面
	 * 
	 * @param userRequest
	 * @param model
	 * @return
	 */
	@GetMapping("new")
	public String newForm(HttpSession session, Model model) {

		userService.setFormContent(model);

		// リクエスト情報初期化
		model.asMap().remove("userRequest");
		model.addAttribute("userRequest", new UserRequest());

		return "new";
	}

	/**
	 * 確認画面(新規登録)
	 * 
	 * @param userRequest
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping("new")
	public String checkNewForm(@Validated @ModelAttribute UserRequest userRequest, BindingResult result, Model model) {

		if (result.hasErrors()) {
			for (var error : result.getAllErrors()) {
				log.error(error.getDefaultMessage());
			}
			userService.setFormContent(model);
			return "new";
		}

		var hobbies = userRequest.getHobby() != null ? convertToNameAndJoinHobbies(userRequest.getHobby()) : "";
		var picture = "";
		if (userRequest.getPicture() != null && !userRequest.getPicture().isEmpty()) {
			var saveFileName = saveUploadFile(userRequest.getPicture());
			picture = ImageUtil.convertPictureToBase64(appProperty.getUploadImageTmpPath(), saveFileName);
			userRequest.setPictureFileName(saveFileName);
		}

		var showUser = new HashMap<String, String>();
		showUser.put("name", userRequest.getName());
		showUser.put("age", userRequest.getAge().toString());
		showUser.put("gender", genderService.getName(Integer.parseInt(userRequest.getGender())));
		showUser.put("fullAddress", prefectureService.getName(userRequest.getPrefecture()) + userRequest.getAddress());
		showUser.put("hobbies", hobbies);
		showUser.put("introduction", userRequest.getIntroduction());
		showUser.put("picture", picture);

		model.addAttribute("user", showUser);
		model.addAttribute("formPath", "/users");
		model.addAttribute("formMethod", "post");
		return "confirm";
	}

	/**
	 * 編集画面
	 * 
	 * @param id
	 * @param userRequest
	 * @param model
	 * @return
	 */
	@GetMapping("{id}/edit")
	public String editForm(@PathVariable("id") String id, @ModelAttribute UserRequest userRequest,
			SessionStatus sessionStatus, Model model) {

		userService.setFormContent(model);

		var user = userService.getUser(Integer.parseInt(id));
		userRequest.setName(user.getName());
		userRequest.setAge(user.getAge());
		userRequest.setGender(user.getGenderId().toString());
		userRequest.setPrefecture(user.getPrefectureId().toString());
		userRequest.setAddress(user.getAddress());
		var hobbies = user.getUserHobbies().stream()
				.map((hobby) -> hobby.getHobbyId().toString())
				.toArray(String[]::new);
		userRequest.setHobby(hobbies);
		userRequest.setIntroduction(user.getIntroduction());
		var pictureFileName = user.getPicture();
		var base64Picture = (pictureFileName != null && pictureFileName.isEmpty())
				? ImageUtil.convertPictureToBase64(appProperty.getDefaultImagePath(),
						appProperty.getDefaultImageFileName())
				: ImageUtil.convertPictureToBase64(appProperty.getUploadImagePath(), pictureFileName);

		model.addAttribute("pictureFileName", pictureFileName);
		model.addAttribute("base64Picture", base64Picture);
		model.addAttribute("userRequset", userRequest);
		return "edit";
	}

	/**
	 * 確認画面(編集)
	 * 
	 * @param id
	 * @param userRequest
	 * @param result
	 * @param model
	 * @return
	 */
	@PostMapping("{id}/edit")
	public String checkEditForm(@PathVariable("id") Integer id, @Validated @ModelAttribute UserRequest userRequest,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			for (var error : result.getAllErrors()) {
				log.error(error.getDefaultMessage());
			}
			userService.setFormContent(model);
			return "edit";
		}

		var hobbies = userRequest.getHobby() != null ? convertToNameAndJoinHobbies(userRequest.getHobby()) : "";
		var picture = "";
		var user = userService.getUser(id);
		if (userRequest.getPicture() != null && !userRequest.getPicture().isEmpty()) {
			var saveFileName = saveUploadFile(userRequest.getPicture());
			picture = ImageUtil.convertPictureToBase64(appProperty.getUploadImageTmpPath(), saveFileName);
			userRequest.setPictureFileName(saveFileName);
		} else if (user.getPicture() != null) {
			// 保存はしていないが過去に登録済み(= 変更しなかった)の場合は過去の画像を画面上に表示だけは行う
			picture = ImageUtil.convertPictureToBase64(appProperty.getUploadImagePath(), user.getPicture());
		}

		var showUser = new HashMap<String, String>();
		showUser.put("name", userRequest.getName());
		showUser.put("age", userRequest.getAge().toString());
		showUser.put("gender", genderService.getName(Integer.parseInt(userRequest.getGender())));
		showUser.put("fullAddress", prefectureService.getName(userRequest.getPrefecture()) + userRequest.getAddress());
		showUser.put("hobbies", hobbies);
		showUser.put("introduction", userRequest.getIntroduction());
		showUser.put("picture", picture);

		model.addAttribute("user", showUser);
		model.addAttribute("formPath", String.format("/users/%s/edit", id));
		model.addAttribute("formMethod", "put");
		return "confirm";
	}

	/**
	 * 登録処理
	 * 
	 * @param userRequest
	 * @param sessionStatus
	 * @param model
	 * @return
	 */
	@PostMapping("")
	public String create(@ModelAttribute UserRequest userRequest, SessionStatus sessionStatus,
			RedirectAttributes redirectAttributes, Model model) {

		// ユーザー情報をまとめて登録する
		userService.createUser(userRequest);

		// 保存済みの一時ファイルを本番用のディレクトリへコピー
		// コピー後は一時ファイルを削除
		if (userRequest.getPictureFileName() != null && !userRequest.getPictureFileName().isEmpty()) {
			var oldPath = Paths.get(appProperty.getUploadImageTmpPath() + userRequest.getPictureFileName());
			var newPath = Paths.get(appProperty.getUploadImagePath() + userRequest.getPictureFileName());
			try (var inputStream = Files.newInputStream(oldPath)) {
				FileCopyUtils.copy(inputStream, Files.newOutputStream(newPath));
				if (oldPath.toFile().exists()) {
					Files.delete(oldPath);
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}

		// セッションを破棄する
		sessionStatus.setComplete();

		redirectAttributes.addFlashAttribute("flashMessage", "新規登録が完了しました");
		return "redirect:/users";
	}

	/**
	 * 更新処理
	 * 
	 * @param id
	 * @param userRequest
	 * @param sessionStatus
	 * @param model
	 * @return
	 */
	@PutMapping("{id}/edit")
	public String update(@PathVariable("id") Integer id, @ModelAttribute UserRequest userRequest,
			SessionStatus sessionStatus, RedirectAttributes redirectAttributes, Model model) {

		if (userRequest.getHobby() != null) {
			for (var hobby : userRequest.getHobby()) {
				System.out.println(hobby);
			}
		}

		// 更新前に現在の画像ファイル名を取得しておく
		var oldPictureFileName = userService.getUser(id).getPicture();

		// データを更新する
		var user = userService.update(id, userRequest);

		// 趣味は一旦削除後、設定されていた場合のみ保存する
		userHobbyService.deleteAll(id);
		if (userRequest.getHobby() != null) {
			for (var hobbyId : userRequest.getHobby()) {
				userHobbyService.create(user, Integer.parseInt(hobbyId));
			}
		}

		// 保存済みの一時ファイルを本番用のディレクトリへコピー
		// コピー後は一時ファイルを削除
		if (userRequest.getPictureFileName() != null && !userRequest.getPictureFileName().isEmpty()) {
			var tmpPath = Paths.get(appProperty.getUploadImageTmpPath() + userRequest.getPictureFileName());
			var newPath = Paths.get(appProperty.getUploadImagePath() + userRequest.getPictureFileName());
			try (var inputStream = Files.newInputStream(tmpPath)) {
				FileCopyUtils.copy(inputStream, Files.newOutputStream(newPath));
				if (tmpPath.toFile().exists()) {
					Files.delete(tmpPath);
				}

				// 前回登録した画像ファイルを削除する
				var oldPath = Paths.get(appProperty.getUploadImagePath() + oldPictureFileName);
				if (oldPath.toFile().exists()) {
					Files.delete(oldPath);
				}

			} catch (IOException e) {
				System.out.println(e);
			}
		}

		// セッションを破棄する
		sessionStatus.setComplete();

		redirectAttributes.addFlashAttribute("flashMessage", "更新が完了しました");
		return "redirect:/users";
	}

	/**
	 * 削除処理
	 * 
	 * @param id
	 * @param userRequest
	 * @param sessionStatus
	 * @param model
	 * @return
	 */
	@DeleteMapping("{id}")
	public String delete(@PathVariable("id") Integer id, @ModelAttribute UserRequest userRequest,
			SessionStatus sessionStatus, RedirectAttributes redirectAttributes, Model model) {

		// 削除前に画像ファイル情報を取得する
		var fileName = userService.getUser(id).getPicture();

		// DBのデータ削除
		userService.delete(id);

		// 保存画像ファイル削除
		deletePicture(fileName);

		redirectAttributes.addFlashAttribute("flashMessage", "削除が完了しました");
		return "redirect:/users";
	}

	private void deletePicture(String fileName) {
		if (fileName != null && !fileName.isEmpty()) {
			var file = new File(appProperty.getUploadImagePath() + fileName);
			file.delete();
		}
	}

	private String convertToNameAndJoinHobbies(String[] hobbies) {
		var hobbyList = Arrays.stream(hobbies).map((hobby) -> hobbyService.getName(hobby)).toArray(String[]::new);
		return String.join(" ", hobbyList);
	}

	private String saveUploadFile(MultipartFile file) {
		createUploadDirectory();

		var fileName = getFileName(file) + "_"
				+ DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now())
				+ getExtension(file);
		var uploadPath = Paths.get(appProperty.getUploadImageTmpPath() + fileName);
		try (OutputStream os = Files.newOutputStream(uploadPath, StandardOpenOption.CREATE)) {
			byte[] bytes = file.getBytes();
			os.write(bytes);
		} catch (IOException e) {
			System.out.println(e);
			return "";
		}

		return fileName;
	}

	private String getExtension(MultipartFile file) {
		var extension = "";
		var fullFileName = file.getOriginalFilename();
		if (fullFileName == null) {
			return extension;
		}
		var index = fullFileName.lastIndexOf(".");
		if (index >= 0) {
			extension = fullFileName.substring(index, fullFileName.length());
		}
		return extension;
	}

	private String getFileName(MultipartFile file) {
		var fileName = "";
		var fullFileName = file.getOriginalFilename();
		if (fullFileName == null) {
			return fileName;
		}
		var index = fullFileName.lastIndexOf(".");
		if (index >= 0) {
			fileName = fullFileName.substring(0, index);
		}
		return fileName;
	}

	private void createUploadDirectory() {
		var path = Paths.get(appProperty.getUploadImagePath());
		if (!Files.exists(path)) {
			try {
				Files.createDirectory(path);
				var tmpPath = Paths.get(appProperty.getUploadImageTmpPath());
				if (!Files.exists(path)) {
					try {
						Files.createDirectory(tmpPath);
					} catch (IOException e) {
						System.out.println(e);
					}
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}
