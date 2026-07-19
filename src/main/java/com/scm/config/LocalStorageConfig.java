package com.scm.config;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.scm.services.impl.ImageServiceImpl;

/**
 * Serves locally-uploaded images (used when Cloudinary is not configured) from
 * the on-disk {@code uploads/} directory at the public URL path {@code /uploads/**}.
 */
@Configuration
public class LocalStorageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Build a "file:/absolute/path/" location with forward slashes and a
        // trailing slash. Spaces are kept literal (no URL-encoding) so Windows
        // paths like ".../Spring Boot Project/..." resolve correctly.
        String absolute = Paths.get(ImageServiceImpl.LOCAL_UPLOAD_DIR)
                .toAbsolutePath()
                .toString()
                .replace(File.separatorChar, '/');
        String uploadPath = "file:" + absolute + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
