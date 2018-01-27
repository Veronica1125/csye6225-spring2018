package com.csye6225.spring2018.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

  private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
  
  @RequestMapping("/")
  public String index() {
    logger.info("Loading home page.");
    return "register";
  }

}
