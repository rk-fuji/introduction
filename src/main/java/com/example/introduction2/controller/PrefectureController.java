package com.example.introduction2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.introduction2.Service.PrefectureService;
import com.example.introduction2.entity.Prefecture;

@RestController
@RequestMapping("/prefectures")
public class PrefectureController {

    @Autowired
    private PrefectureService prefectureService;

    @GetMapping("")
    public List<Prefecture> index() {
        return prefectureService.getPrefectures();
    }
}
