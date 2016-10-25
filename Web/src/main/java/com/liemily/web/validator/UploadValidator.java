package com.liemily.web.validator;

import com.liemily.web.domain.Upload;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates uploads from the user to verify the upload is valid
 * @author Emily Li
 */
@Component
public class UploadValidator {
    public static final String FILE_VALUE = "file";
    public static final String NULL_FILE_ERROR = "no.file.provided";
    public static final String NOT_AN_IMG_ERROR = "not.an.image";

    private static final String VALID_CONTENT_TYPE = "image";

    public void validate(Upload target, Errors errors) {
        validate(target.getFile(), errors);
    }

    public void validate(MultipartFile target, Errors errors) {
        if (target == null) {
            errors.rejectValue(FILE_VALUE, NULL_FILE_ERROR);
        } else {
            if (!target.getContentType().startsWith(VALID_CONTENT_TYPE)) {
                errors.rejectValue(FILE_VALUE, NOT_AN_IMG_ERROR);
            }
        }
    }
}
