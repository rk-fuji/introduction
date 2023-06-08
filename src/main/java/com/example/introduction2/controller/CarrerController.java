package com.example.introduction2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.introduction2.Service.CareerService;
import com.example.introduction2.Service.UserService;
import com.example.introduction2.dto.CareerRequest;
import com.example.introduction2.entity.Career;

@Controller
@RequestMapping("/users/{id}/job_career/{jobCareerId}/careers")
public class CarrerController {

    @Autowired
    private CareerService careerService;

    @Autowired
    private UserService userService;

    @GetMapping("/{careerId}")
    @ResponseBody
    public Career get(@PathVariable Integer careerId,
            @ModelAttribute CareerRequest careerRequest, Model model) {
        model.addAttribute("career", careerRequest);
        return careerService.getCareer(careerId);
    }

    @PostMapping("")
    public String create(@PathVariable Integer id, @PathVariable Integer jobCareerId,
            @ModelAttribute CareerRequest careerRequest, RedirectAttributes redirectAttributes, Model model) {
        var jobCareer = userService.getUser(id).getJobCareer();
        careerService.createCareer(careerRequest, jobCareer);

        redirectAttributes.addFlashAttribute("flashMessage", "経歴の登録が完了しました");
        return String.format("redirect:/users/%d/job_career/%d", id, jobCareerId);
    }

    @PutMapping("/{careerId}")
    public String update(@PathVariable Integer id, @PathVariable Integer jobCareerId, @PathVariable Integer careerId,
            @ModelAttribute CareerRequest careerRequest, RedirectAttributes redirectAttributes, Model model) {
        careerService.showRequestLog(careerRequest);
        var jobCareer = userService.getUser(id).getJobCareer();
        careerService.updateCareer(careerId, careerRequest, jobCareer);

        redirectAttributes.addFlashAttribute("flashMessage", "経歴の更新が完了しました");
        return String.format("redirect:/users/%d/job_career/%d", id, jobCareerId);
    }

    @DeleteMapping("/{careerId}")
    public String delete(@PathVariable Integer id, @PathVariable Integer jobCareerId, @PathVariable Integer careerId,
            @ModelAttribute CareerRequest careerRequest, RedirectAttributes redirectAttributes, Model model) {
        careerService.deleteCareer(careerId);

        redirectAttributes.addFlashAttribute("flashMessage", "経歴の削除が完了しました");
        return String.format("redirect:/users/%d/job_career/%d", id, jobCareerId);
    }
}
