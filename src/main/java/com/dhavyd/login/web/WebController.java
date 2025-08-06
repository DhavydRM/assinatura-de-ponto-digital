package com.dhavyd.login.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    
    @RequestMapping("/{path:[^\\.]*}")
    public String redirect() {
        return "forward:index.html";
    }
}
