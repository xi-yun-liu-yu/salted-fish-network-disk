package com.xiyun.saltedfishnetdish.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class textController {
    @RequestMapping("/t2")
    public String index(Model model){
//Spring MVC会自动实例化一个Model对象用于向视图中传值
        model.addAttribute("msg", "ControllerTest2");
//返回视图位置
        return "test";
    }

}
