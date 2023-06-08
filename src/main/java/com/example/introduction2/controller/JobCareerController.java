package com.example.introduction2.controller;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.introduction2.Service.GenderService;
import com.example.introduction2.Service.JobCareerService;
import com.example.introduction2.Service.UserService;
import com.example.introduction2.dto.CareerRequest;
import com.example.introduction2.dto.JobCareerRequest;
import com.example.introduction2.entity.JobCareer;

@Controller
@RequestMapping({ "/users/{id}/job_career/{jobCareerId}", "/users/{id}/job_career/{jobCareerId}/" })
@SessionAttributes("jobCareerRequest")
public class JobCareerController {

    Map<Integer, String> SPOUSES = new HashMap<Integer, String>() {
        {
            put(1, "あり");
            put(2, "なし");
        }
    };

    @Autowired
    private UserService userService;

    @Autowired
    private GenderService genderService;

    @Autowired
    private JobCareerService jobCareerService;

    @ModelAttribute("jobCareerRequest")
    private JobCareerRequest jobCareerRequest() {
        return new JobCareerRequest();
    }

    /**
     * 閲覧画面
     * 
     * @param id
     * @param jobCareerId
     * @param model
     * @return
     */
    @GetMapping("")
    public String show(@PathVariable("id") Integer id, @PathVariable("jobCareerId") Integer jobCareerId, Model model) {
        var showJobCareer = new HashMap<String, String>();
        var user = userService.getUser(id);
        var jobCareer = jobCareerService.getJobCareer(jobCareerId);

        var operation = "";
        if (jobCareer.getOperation() != null) {
            var year = String.valueOf(jobCareer.getOperation().getYear());
            var month = String.valueOf(jobCareer.getOperation().getMonthValue());
            operation = String.format("%s年%s月", year, month);
        }
        var careerFullDate = "";
        if ((jobCareer.getCareerDate() != null && !jobCareer.getCareerDate().isEmpty())
                && (jobCareer.getCareerMonth() != null && !jobCareer.getCareerMonth().isEmpty())) {
            careerFullDate = String.format("%s 年 %s ヵ月", jobCareer.getCareerDate(),
                    jobCareer.getCareerMonth());
        }

        showJobCareer.put("nameKana", jobCareer.getNameKana());
        showJobCareer.put("affiliation", jobCareer.getAffiliation());
        showJobCareer.put("name", user.getName());
        showJobCareer.put("gender", genderService.getName(user.getGenderId()));
        showJobCareer.put("nearestStation", jobCareer.getNearestStation());
        showJobCareer.put("age", user.getAge().toString());
        showJobCareer.put("operation", operation);
        showJobCareer.put("spouse", getSpouseName(jobCareer.getSpouse()));
        showJobCareer.put("careerFullDate", careerFullDate);
        showJobCareer.put("specialty", jobCareer.getSpecialty());
        showJobCareer.put("favoriteTechnology", jobCareer.getFavoriteTechnology());
        showJobCareer.put("favoriteBusiness", jobCareer.getFavoriteBusiness());
        showJobCareer.put("selfPublicRelations", jobCareer.getSelfPublicRelations());

        var showCareers = getShowCareers(jobCareer);

        model.addAttribute("jobCareer", showJobCareer);
        model.addAttribute("careers", showCareers);
        return "job_career/show";
    }

    /**
     * 編集画面
     * 
     * @param id
     * @param jobCareerRequest
     * @param model
     * @return
     */
    @GetMapping("/edit")
    public String editForm(@PathVariable("id") Integer id, @PathVariable("jobCareerId") Integer jobCareerId,
            @ModelAttribute JobCareerRequest jobCareerRequest,
            @ModelAttribute CareerRequest careerRequest,
            SessionStatus sessionStatus,
            Model model) {

        // 前回の画面情報を引き継がないようにする
        sessionStatus.setComplete();

        // ユーザー情報を取得してフォーム用データにセット
        var user = userService.getUser(id);
        jobCareerRequest.setName(user.getName());
        jobCareerRequest.setAge(user.getAge());
        jobCareerRequest.setGender(user.getGenderId().toString());

        // 職務経歴書情報を取得してフォーム用データにセット
        var jobCareer = user.getJobCareer();
        jobCareerRequest.setNameKana(jobCareer.getNameKana());
        jobCareerRequest.setAffiliation(jobCareer.getAffiliation());
        jobCareerRequest.setNearestStation(jobCareer.getNearestStation());

        // YYYY-MM へ変換する
        if (jobCareer.getOperation() != null) {
            var year = String.valueOf(jobCareer.getOperation().getYear());
            var month = String.format("%02d", jobCareer.getOperation().getMonthValue());
            jobCareerRequest.setOperation(String.format("%s-%s", year, month));
        }

        // 配偶者が未設定の場合はデフォルトとして「なし」の状態とする
        if (jobCareer.getSpouse() == null) {
            jobCareerRequest.setSpouse("2");
        } else {
            jobCareerRequest.setSpouse(jobCareer.getSpouse().toString());
        }
        jobCareerRequest.setCareerDate(jobCareer.getCareerDate());
        jobCareerRequest.setCareerMonth(jobCareer.getCareerMonth());
        jobCareerRequest.setSpecialty(jobCareer.getSpecialty());
        jobCareerRequest.setFavoriteTechnology(jobCareer.getFavoriteTechnology());
        jobCareerRequest.setFavoriteBusiness(jobCareer.getFavoriteTechnology());
        jobCareerRequest.setSelfPublicRelations(jobCareer.getSelfPublicRelations());

        var showCareers = getShowCareers(jobCareer);

        model.addAttribute("jobCareerRequest", jobCareerRequest);
        model.addAttribute("careers", showCareers);

        // 固定データセット
        setFormContent(model);

        return "job_career/edit";
    }

    /**
     * 確認画面(編集)
     * 
     * @param id
     * @param jobCareerRequest
     * @param result
     * @param model
     * @return
     */
    @PostMapping("/edit")
    public String checkEditForm(@PathVariable("id") Integer id, @PathVariable("jobCareerId") Integer jobCareerId,
            @Validated @ModelAttribute JobCareerRequest jobCareerRequest,
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.out.println("<<< Validation Error >>>");
            for (var error : result.getAllErrors()) {
                System.out.println(error.getDefaultMessage());
            }
            setFormContent(model);
            return "job_career/edit";
        }

        var operation = "";
        if (jobCareerRequest.getOperation() != null && !jobCareerRequest.getOperation().isEmpty()) {
            var splitOparation = jobCareerRequest.getOperation().split("-");
            operation = String.format("%s 年 %s 月", splitOparation[0],
                    splitOparation[1]);
        }

        System.out.println(jobCareerRequest.toString());
        var careerDate = jobCareerRequest.getCareerDate();
        if (careerDate == null || careerDate.isEmpty()) {
            careerDate = "0";
            jobCareerRequest.setCareerDate(careerDate);
        }
        var careerMonth = jobCareerRequest.getCareerMonth();
        if (careerMonth == null || careerMonth.isEmpty()) {
            careerMonth = "0";
            jobCareerRequest.setCareerMonth(careerMonth);
        }
        var careerFullDate = String.format("%s 年 %s ヵ月", careerDate,
                careerMonth);

        var showJobCareer = new HashMap<String, String>();
        showJobCareer.put("nameKana", jobCareerRequest.getNameKana());
        showJobCareer.put("affiliation", jobCareerRequest.getAffiliation());
        showJobCareer.put("name", jobCareerRequest.getName());
        showJobCareer.put("gender", genderService.getName(Integer.parseInt(jobCareerRequest.getGender())));
        showJobCareer.put("nearestStation", jobCareerRequest.getNearestStation());
        showJobCareer.put("age", jobCareerRequest.getAge().toString());
        showJobCareer.put("operation", operation);
        showJobCareer.put("spouse", getSpouseName(Integer.parseInt(jobCareerRequest.getSpouse())));
        showJobCareer.put("careerFullDate", careerFullDate);
        showJobCareer.put("specialty", jobCareerRequest.getSpecialty());
        showJobCareer.put("favoriteTechnology", jobCareerRequest.getFavoriteTechnology());
        showJobCareer.put("favoriteBusiness", jobCareerRequest.getFavoriteBusiness());
        showJobCareer.put("selfPublicRelations", jobCareerRequest.getSelfPublicRelations());

        model.addAttribute("jobCareer", showJobCareer);
        return "job_career/confirm";
    }

    // 更新処理を記述する
    @PutMapping("/edit")
    public String update(@PathVariable("id") Integer id, @PathVariable("jobCareerId") Integer jobCareerId,
            @ModelAttribute JobCareerRequest jobCareerRequest,
            SessionStatus sessionStatus, RedirectAttributes redirectAttributes, Model model) {

        // 更新情報を出力
        System.out.println("-------------------------------");
        System.out.println(String.format("フリガナ: [%s]", jobCareerRequest.getNameKana()));
        System.out.println(String.format("所属: [%s]", jobCareerRequest.getAffiliation()));
        System.out.println(String.format("氏名: [%s]", jobCareerRequest.getName()));
        System.out.println(String.format("性別: [%s]", jobCareerRequest.getGender()));
        System.out.println(String.format("最寄駅: [%s]", jobCareerRequest.getNearestStation()));
        System.out.println(String.format("年齢: [%d]", jobCareerRequest.getAge()));
        System.out.println(String.format("稼働: [%s]", jobCareerRequest.getOperation()));
        System.out.println(String.format("配偶者: [%s]", jobCareerRequest.getSpouse()));
        System.out.println(String.format("経歴(年): [%s]", jobCareerRequest.getCareerDate()));
        System.out.println(String.format("経歴(月): [%s]", jobCareerRequest.getCareerMonth()));
        System.out.println(String.format("得意分野: [%s]", jobCareerRequest.getSpecialty()));
        System.out.println(String.format("得意技術: [%s]", jobCareerRequest.getFavoriteTechnology()));
        System.out.println(String.format("得意業務: [%s]", jobCareerRequest.getFavoriteBusiness()));
        System.out.println(String.format("自己PR: [%s]", jobCareerRequest.getSelfPublicRelations()));
        System.out.println("-------------------------------");

        // ユーザー情報の更新
        var user = userService.jobCareerUpdate(id, jobCareerRequest);

        // 経歴書の更新
        jobCareerService.update(user, jobCareerRequest);

        // 画面項目の再セット
        setFormContent(model);

        // セッションを破棄する
        sessionStatus.setComplete();

        redirectAttributes.addFlashAttribute("flashMessage", "更新が完了しました");
        return String.format("redirect:/users/%d/job_career/%d", id, jobCareerId);
    }

    private void setFormContent(Model model) {
        var genders = genderService.findAll();
        model.addAttribute("genders", genders);
        model.addAttribute("spouses", SPOUSES);
    }

    private String getSpouseName(Integer id) {
        return SPOUSES.get(id);
    }

    private ArrayList<LinkedHashMap<String, String>> getShowCareers(JobCareer jobCareer) {
        var careers = jobCareer.getCareers();
        var showCareers = new ArrayList<LinkedHashMap<String, String>>();
        careers.forEach(career -> {
            var showCareer = new LinkedHashMap<String, String>();

            var fromYear = "〇";
            var fromMonth = "△";
            if (career.getFromDate() != null) {
                fromYear = String.valueOf(career.getFromDate().getYear());
                fromMonth = String.valueOf(career.getFromDate().getMonthValue());
            }
            var toYear = "〇";
            var toMonth = "△";
            if (career.getToDate() != null) {
                toYear = String.valueOf(career.getToDate().getYear());
                toMonth = String.valueOf(career.getToDate().getMonthValue());
            }

            // 経歴の期間は同月を1ヵ月とみなす
            // 例)
            // 2023年1月 ~ 2023年1月 (1ヵ月)
            // 2023年5月 ~ 2023年7月 (3ヵ月)
            var betweenDate = "〇";
            if (career.getFromDate() != null && career.getToDate() != null) {
                betweenDate = String.valueOf(ChronoUnit.MONTHS.between(career.getFromDate(), career.getToDate()) + 1);
            }

            showCareer.put("id", career.getId().toString());
            showCareer.put("fromDate", String.format("%s年%s月", fromYear, fromMonth));
            showCareer.put("toDate", String.format("%s年%s月", toYear, toMonth));
            showCareer.put("betweenDate", String.format("(%sヵ月)", betweenDate));
            showCareer.put("businessOutline", career.getBusinessOutline());
            showCareer.put("businessOutlineDescription", career.getBusinessOutlineDescription());
            showCareer.put("role", career.getRole());
            showCareer.put("roleDescription", career.getRoleDescription());
            showCareer.put("useLanguage", career.getUseLanguage());
            showCareer.put("useDatabase", career.getUseDatabase());
            showCareer.put("useServer", career.getUseServer());
            showCareer.put("other", career.getOther());
            showCareer.put("responsibleProcess", career.getResponsibleProcess());

            showCareers.add(showCareer);
        });
        return showCareers;
    }
}
