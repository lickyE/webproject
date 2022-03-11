package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.service.AlphaService;
import com.nowcoder.community.community.util.Communityutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")

public class alphacontroller {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Spring Boot";
    }
    @Autowired
    AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String val = request.getHeader(name);
            System.out.println(name + ": " + val);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(PrintWriter printWriter = response.getWriter();) {
            printWriter.write("<h1>mycode</h1>");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //GET请求
    // /students?current=1&limit=20
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents (@RequestParam(name = "current",required = false,defaultValue = "1") int current,
                                   @RequestParam(name = "limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getstudent(@PathVariable("id") int id){
        System.out.println(id);
        return("a student");
    }

    //POST请求  提交数据
    @RequestMapping(path = "/student",method =  RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return "sucess";
    }

    //响应请求 html数据（前面都是默认响应字符串)  不加注解默认返回html
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","zhangsan");
        modelAndView.addObject("age","30");
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","scu");
        model.addAttribute("age",120);
        return "/demo/view";
    }
    //响应JSON数据 在异步请求当中
    //JAVA对象 -> JSON字符串-> JS对象

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","zahngsan");
        emp.put("age",23);
        emp.put("salary",8000);
        return emp;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        System.out.println("查询所有员工");
        List<Map<String,Object>> emps = new ArrayList<>();
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","zahngsan");
        emp.put("age",23);
        emp.put("salary",8000);
        emps.add(emp);
        emp = new HashMap<>();
        emp.put("name","zahnger");
        emp.put("age",24);
        emp.put("salary",800);
        emps.add(emp);
        emp = new HashMap<>();
        emp.put("name","wangwu");
        emp.put("age",25);
        emp.put("salary",80);
        emps.add(emp);
        return emps;
    }

    //cookie
    @RequestMapping(path="/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("code", Communityutil.generateUUID());
        //设置生效范围
        cookie.setPath("/community/alpha");
        //设置生存时间 设置后保存在硬盘里
        cookie.setMaxAge(60 * 10);//秒为单位
        //发送cookie
        response.addCookie(cookie);
        return "set";
    }

    @RequestMapping(path="/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code){
        return code;
    }

    //session
    @RequestMapping(path="/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return"set";
    }

    @RequestMapping(path="/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get";
    }
}
