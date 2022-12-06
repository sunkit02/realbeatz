package com.realbeatz.utils;

import com.realbeatz.exceptions.IllegalFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Base64;

import static com.realbeatz.configs.FileUploadConfig.PROFILE_PICTURE_UPLOAD_DIRECTORY_PATH;

@Slf4j
public class FileUtils {

    private static final String[] ACCEPTABLE_IMAGE_EXTENSIONS =
            {".png", ".jpeg", ".jpg"};
    private static final long MAX_PROFILE_PIC_BYTE_SIZE = 10485760L;


    /**
     * Stores a picture file into the default directory for profile pictures
     *
     * @param file the picture file to be stored
     * @return The full file name with a unique file code attached to the beginning (Ex. 'XXXXXXXX-filename.ext')
     * @throws IllegalFileTypeException when the file passed in is not an acceptable
     *                                  image file
     */
    public static String saveProfilePicture(MultipartFile file, Path targetDirectory) throws IllegalFileTypeException, IOException {
        log.debug("Saving profile picture: {}", file.getOriginalFilename());
        System.out.println("saving profile picture: " + file.getOriginalFilename());
        validateProfilePicture(file);
        // create file code
        String fileCode = RandomStringUtils.randomAlphabetic(8);
        String fullFileName = fileCode + "-" + file.getOriginalFilename();
        log.debug("Generated file code: {}, full file name: {}", fileCode, fullFileName);

        Path targetPath = targetDirectory.resolve(fullFileName);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.debug("File: {} saved successfully.", fullFileName);

        return fullFileName;
    }

    private static void validateProfilePicture(MultipartFile file) throws IllegalFileTypeException, FileSizeLimitExceededException {
        String filename = file.getOriginalFilename();
        if (!isAcceptableImageFile(filename)) {
            throw new IllegalFileTypeException(String.format(
                    "File: '%s' is not a valid image file. Valid file types: %s",
                    filename, Arrays.toString(ACCEPTABLE_IMAGE_EXTENSIONS)
            ));
        }

        if (file.getSize() > MAX_PROFILE_PIC_BYTE_SIZE) {
            throw new FileSizeLimitExceededException(String.format(
                    "File: '%s' exceeds the maximum upload size of %dMB.",
                    filename, bytesToMB(MAX_PROFILE_PIC_BYTE_SIZE)),
                    file.getSize(),
                    MAX_PROFILE_PIC_BYTE_SIZE);
        }
    }

    private static long bytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }

    private static boolean isAcceptableImageFile(String originalFilename) {
        boolean isValid = false;
        for (String extension : ACCEPTABLE_IMAGE_EXTENSIONS) {
            if (originalFilename.endsWith(extension)) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    /**
     * Fetches the profile picture requested as a byte array
     * @param fileFullName the file name with file code attached (Ex. 'XXXXXXXX-filename.ext')
     * @return a byte array representing the requested profile picture
     * @throws IOException when the picture cannot be fetched
     */
    public static byte[] getProfilePictureAsBinary(String fileFullName) throws IOException {
        log.debug("Fetching profile picture: {}", fileFullName);
        Path filePath =
                Path.of(PROFILE_PICTURE_UPLOAD_DIRECTORY_PATH)
                        .resolve(fileFullName);
        log.debug("Fetching profile picture {} from {}", fileFullName, filePath);
        return Files.readAllBytes(filePath);
    }

    public static byte[] getProfilePictureAsBase64(String fileFullName) throws IOException {
        return Base64.getEncoder()
                .encode(getProfilePictureAsBinary(fileFullName));
    }


    @Bean
    public FileUtils fileUtils() {
        return new FileUtils();
    }
}
