package com.liemily.web.controller;

import com.liemily.web.domain.Upload;
import com.liemily.web.service.UploadService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.annotation.MultipartConfig;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * Upload controller allowing the user to upload files
 * @author Emily Li
 */
@Controller
@RequestMapping("/upload")
@MultipartConfig
public class UploadController {
    private static final Logger logger = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    private UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @RequestMapping(value="/file/{id}", method = RequestMethod.GET)
    public @ResponseBody byte[] returnUpload(@PathVariable String id) throws IOException {
        Upload upload = uploadService.get(id);
        if (upload != null) {
            byte[] byteArray = upload.getFile().getBytes();
            return byteArray;
        } else {
            return new byte[0];
        }
    }

    @RequestMapping(value="/file", method = RequestMethod.POST)
    public String upload(Upload file, BindingResult bindingResult) {
        uploadService.add(file, bindingResult);
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                logger.info("Found error: " + error);
            }
            return "invalid-file";
        }
        return "forward:/identify";
    }
}
