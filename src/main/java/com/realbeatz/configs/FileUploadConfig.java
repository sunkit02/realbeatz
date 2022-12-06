package com.realbeatz.configs;

import com.realbeatz.RealBeatzApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Configuration
public class FileUploadConfig {

    private static final String PROJECT_NAME = "realbeatz";
    public static final String APPLICATION_BASE_PATH;

    // Finds the absolute path to the directory the application is currently running in
    static {
        String path = RealBeatzApplication.class.getProtectionDomain().getCodeSource().getLocation().toString();
        log.debug("Original path: " + path);
        if (path.startsWith("jar")) {
            log.info("Running application as a jar");
            // returns the parent folder of the jar file if is in a jar file
            APPLICATION_BASE_PATH = path
                    .replace("jar:file:/", "")
                    .replaceAll(PROJECT_NAME + "([^/]*)[.]jar(.*)!/$", "");
        } else {
            log.info("Running application not in a jar");
            // returns the project root folder as base path if isn't in a jar file
            APPLICATION_BASE_PATH = path.replace("file:/", "")
                    .replaceAll("classes/$", "")
                    .replaceAll("target/$", "");
        }

        log.info("Application base path: " + APPLICATION_BASE_PATH);
    }

    public static final String DEFAULT_PROFILE_PIC_FILENAME =
            "default-profile-picture.jpg";
    public static final String PROFILE_PICTURE_UPLOAD_DIRECTORY_PATH =
            APPLICATION_BASE_PATH + "/profile-pics";
    public static final String DEFAULT_PROFILE_PIC_FULL_NAME =
            DEFAULT_PROFILE_PIC_FILENAME;

    /**
     * This constructor will initialize the configuration and create the
     * needed directories during runtime upon initialization around the
     * running location of the application (base path)
     */
    public FileUploadConfig() {
        try {
            log.info("Creating '/profile-pics' directory...");
            Files.createDirectory(Path.of(APPLICATION_BASE_PATH)
                    .resolve("profile-pics"));
        } catch (FileAlreadyExistsException e) {
            log.info("'/profile-pics' directory already created.");
        } catch (IOException e) {
            log.error("An error occurred when creating 'profile-pics' directory: {}",
                    e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String getDefaultProfilePicFullName() {
        return DEFAULT_PROFILE_PIC_FULL_NAME;
    }
    public static Path getProfilePictureUploadDirectory() {
        return Path.of(PROFILE_PICTURE_UPLOAD_DIRECTORY_PATH);
    }
}
