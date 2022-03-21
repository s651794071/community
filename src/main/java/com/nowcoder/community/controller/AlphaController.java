package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello, Spring!";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    // 底层实现
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());

        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset-utf-8");
        try (PrintWriter writer = response.getWriter()){
            writer.write("<p>Hello,Spring MVC!<p>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET请求 --浏览器向服务浏览器发送 获取数据的请求

    // /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",  required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false,defaultValue = "10") int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // POST请求 --浏览器向服务器发送 提交数据请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    // http://localhost:8080/html/student.html 要用这个来访问
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "Save successfully!";
    }

    // 响应html数据
    // 方式1
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age","25");
        mav.setViewName("/demo/view");
        return mav;
    }

    // 方式2
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","清华大学");
        model.addAttribute("age",110);
        model.addAttribute("address","BeiJing");
        return "/demo/view";
    }

    // 响应JSON数据 （异步请求）
    // Java对象 -> JSON字符串 -> JS对象
    @RequestMapping(path="/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "Ming");
        emp.put("age", "25");
        emp.put("salary","20000");
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "Ming");
        emp.put("age", "25");
        emp.put("salary","20000");
        list.add(emp);

        emp.put("name", "Ning");
        emp.put("age", "26");
        emp.put("salary","22000");
        list.add(emp);

        return list;
    }

    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String ajax(String name, int age) {
        return CommunityUtil.getJsonString(0, "!");
    }
}
