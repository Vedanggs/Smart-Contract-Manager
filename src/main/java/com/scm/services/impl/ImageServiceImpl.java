package com.scm.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helpers.AppConstants;
import com.scm.services.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Cloudinary cloudinary;

    @Value("${cloudinary.cloud.name:}")
    private String cloudName;

    // Directory (relative to the app's working dir) used when Cloudinary is not
    // configured. Served publicly at /uploads/** by LocalStorageConfig.
    public static final String LOCAL_UPLOAD_DIR = "uploads";

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    private boolean isCloudinaryConfigured() {
        return cloudName != null
                && !cloudName.isBlank()
                && !cloudName.equalsIgnoreCase("dummy");
    }

    @Override
    public String uploadImage(MultipartFile contactImage, String filename) {
        if (contactImage == null || contactImage.isEmpty()) {
            return null;
        }

        if (isCloudinaryConfigured()) {
            return uploadToCloudinary(contactImage, filename);
        }
        return uploadToLocalDisk(contactImage, filename);
    }

    private String uploadToCloudinary(MultipartFile contactImage, String filename) {
        try {
            byte[] data = contactImage.getBytes();
            cloudinary.uploader().upload(data, ObjectUtils.asMap("public_id", filename));
            return getUrlFromPublicId(filename);
        } catch (IOException e) {
            logger.error("Cloudinary upload failed: {}", e.getMessage());
            return null;
        }
    }

    private String uploadToLocalDisk(MultipartFile contactImage, String filename) {
        try {
            String extension = getExtension(contactImage.getOriginalFilename());
            String storedName = filename + extension;

            Path dir = Paths.get(LOCAL_UPLOAD_DIR).toAbsolutePath();
            Files.createDirectories(dir);
            Path target = dir.resolve(storedName);
            Files.copy(contactImage.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            logger.info("Image saved locally at {}", target);
            // Public URL served by LocalStorageConfig resource handler
            return "/uploads/" + storedName;
        } catch (IOException e) {
            logger.error("Local image save failed: {}", e.getMessage());
            return null;
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
            // basic allow-list to avoid odd values
            if (ext.matches("\\.(png|jpg|jpeg|gif|webp|bmp)")) {
                return ext;
            }
        }
        return ".png";
    }

    @Override
    public String getUrlFromPublicId(String publicId) {
        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                .crop(AppConstants.CONTACT_IMAGE_CROP))
                .generate(publicId);
    }

}
