package com.liemily.web.domain;

import org.springframework.web.multipart.MultipartFile;

/**
 * Domain object wrapping MultipartFile's provided by the Spring Boot MVC application
 * @author Emily Li
 */
public class Upload {
    private MultipartFile file;
    private String id;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
        this.id = String.valueOf(file.hashCode());
    }

    public String getId() {
        return id;
    }
}
