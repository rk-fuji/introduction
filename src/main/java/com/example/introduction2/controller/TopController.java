package com.example.introduction2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TopController {

    /**
     * トップページへのアクセスは自己紹介一覧へリダイレクト
     * 
     * @return
     */
    @GetMapping("/")
    public String top() {
        return "redirect:/users";
    }
}
