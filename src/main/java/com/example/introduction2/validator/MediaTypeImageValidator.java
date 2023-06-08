package com.example.introduction2.validator;

import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MediaTypeImageValidator implements ConstraintValidator<MediaTypeImage, MultipartFile> {

	@Override
	public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
		if (value.isEmpty()) {
			return true;
		}

		var mediaType = MediaType.parseMediaType(value.getContentType());
		var ext = FilenameUtils.getExtension(value.getOriginalFilename());

		var mediaTypeList = Arrays.asList(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG, MediaType.IMAGE_GIF);
		var extList = Arrays.asList("jpg", "jpeg", "png", "gif");

		return mediaTypeList.stream().anyMatch((mType) -> mediaType.includes(mType))
				&& extList.stream().anyMatch((v) -> ext.toLowerCase().equals(v));
	}
}
