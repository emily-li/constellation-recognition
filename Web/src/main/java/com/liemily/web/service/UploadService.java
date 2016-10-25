package com.liemily.web.service;

import com.liemily.web.domain.Upload;
import com.liemily.web.validator.UploadValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Upload Service used to store uploads provided by the user
 * @author Emily Li
 */
@Component
public class UploadService {
    private Map<String, Upload> uploads;
    private UploadValidator uploadValidator;

    @Autowired
    public UploadService(UploadValidator uploadValidator) {
        this.uploadValidator = uploadValidator;
        uploads = new ConcurrentHashMap<>();
    }

    public void add(Upload upload, BindingResult bindingResult) {
        uploadValidator.validate(upload, bindingResult);
        add(upload);
    }

    public void add(Upload upload) {
        uploads.put(upload.getId(), upload);
    }

    public Upload get(String id) {
        return uploads.get(id);
    }
}
