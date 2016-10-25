package com.liemily.web.controller;

import com.liemily.web.domain.Upload;
import com.liemily.web.service.ConstellationRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Constellation recognition application controller, providing access to the ConstellationRecognitionService
 * @author Emily Li
 */
@Controller
public class ConstellationRecognitionController {

    private ConstellationRecognitionService constellationRecognitionService;

    @Autowired
    public ConstellationRecognitionController(ConstellationRecognitionService constellationRecognitionService) {
        this.constellationRecognitionService = constellationRecognitionService;
    }

    @RequestMapping("/identify")
    public String identify(Upload file, Model model) throws IOException {
        Enum result = constellationRecognitionService.identify(file.getFile());
        if (result == null) {
            return "no-result";
        } else {
            model.addAttribute("constellationresult", result.toString());
            model.addAttribute("constellationfile", result.name());
            model.addAttribute("fileid", file.getId());
            return "result";
        }
    }
}
