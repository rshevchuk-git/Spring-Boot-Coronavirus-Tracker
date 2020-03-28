package com.springvirus.tracker.controllers;

import com.springvirus.tracker.services.DailyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


@Controller
public class WebController {

    final DailyDataService service;

    @Autowired
    public WebController(DailyDataService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model){

        model.addAttribute("totalReportedCases", service.getTotalCases());
        model.addAttribute("totalNewCases", service.getNewCases());
        model.addAttribute("locationStat", service.getAllCases());

        return "index";
    }

    @GetMapping("/search")
    public String searchByCountry(@RequestParam("country") String countryName,
                                  Model model){

        model.addAttribute("locationStat", countryName.equals("") ? service.getAllCases() : service.getByCountry(countryName));
        model.addAttribute("totalReportedCases", service.getTotalCases());
        model.addAttribute("totalNewCases", service.getNewCases());

        return "index";
    }

    @GetMapping("/countries")
    @ResponseBody
    public List<String> getCountryNames(@RequestParam(value = "term",required = false, defaultValue = "") String term){
       return service.getCountryNames().stream()
               .filter(e -> e.contains(StringUtils.capitalize(term.toLowerCase())))
               .collect(Collectors.toList());
    }

}
