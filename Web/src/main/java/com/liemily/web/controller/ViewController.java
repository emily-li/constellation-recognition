package com.liemily.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple view controller for mappings not directly related to functionality
 * @author Emily Li
 */
@Controller
public class ViewController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
