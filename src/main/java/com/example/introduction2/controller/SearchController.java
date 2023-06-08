package com.example.introduction2.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.introduction2.Service.GenderService;
import com.example.introduction2.Service.PrefectureService;
import com.example.introduction2.Service.UserHobbyService;
import com.example.introduction2.Service.UserService;
import com.example.introduction2.component.AppProperty;
import com.example.introduction2.dto.SearchRequest;
import com.example.introduction2.entity.Gender;
import com.example.introduction2.entity.Prefecture;
import com.example.introduction2.util.ImageUtil;

@Controller
@RequestMapping("/search")
@SessionAttributes("searchRequest")
public class SearchController {

    @Autowired
    private AppProperty appProperty;

    @Autowired
    private UserService userService;

    @Autowired
    private GenderService genderService;

    @Autowired
    private PrefectureService prefectureService;

    @Autowired
    private UserHobbyService userHobbyService;

    private Map<Integer, String> searchPatterns = new HashMap<Integer, String>() {
        {
            put(1, "or");
            put(2, "and");
        }
    };

    @ModelAttribute("searchRequest")
    private SearchRequest searchRequest() {
        return new SearchRequest();
    }

    /**
     * 検索結果画面表示
     * 
     * @param pageable
     * @param model
     * @return 検索結果画面
     */

    @GetMapping("")
    public String search(@PageableDefault(size = 2) Pageable pageable, @ModelAttribute SearchRequest searchRequest,
            Model model) {

        var userPages = userService.search(searchRequest, pageable);
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

        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("users", users);
        model.addAttribute("page", userPages);
        model.addAttribute("prefectures", prefectures);
        model.addAttribute("genders", genders);
        model.addAttribute("searchPatterns", searchPatterns);
        return "list";
    }
}
