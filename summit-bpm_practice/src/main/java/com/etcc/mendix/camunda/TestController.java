package com.etcc.mendix.camunda;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

	@RequestMapping("/hello") 
    @ResponseBody
    public String helloGFG() 
    { 
		System.out.println("hellfo teting");
        return "Hello GeeksForGeeks"; 
    } 
	@RequestMapping("/helloObj") 
    @ResponseBody
    public Person helloObj() 
    { 
		System.out.println("hellfo object");
		Person p =new Person();
		p.setLocation("HYD");
		p.setName("Raghu");
		p.setPersonId(1);
        return p; 
    } 
	@PostMapping(value="/helloObjInput") 
    @ResponseBody
    public Person helloObjInput(@RequestBody PersonWrapperDTO p) 
    { 
		System.out.println("PersonWrapperDTO helloObjInput Name"+p.getPerson().getName());
		
        return p.getPerson(); 
    } 
	
	@RequestMapping("/plain") 
    public void plain() 
    { 
		System.out.println("plain teting");
    } 
}
