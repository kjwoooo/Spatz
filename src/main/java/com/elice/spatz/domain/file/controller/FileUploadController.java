package com.elice.spatz.domain.file.controller;

import com.elice.spatz.domain.file.dto.FileRequestDto;
import com.elice.spatz.domain.file.entity.File;
import com.elice.spatz.domain.file.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        System.out.println("Test endpoint hit");
        return ResponseEntity.ok("Test successful");
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<File>> ListFilesByChannelId(@PathVariable Long channelId) {
        // 채널 ID에 해당하는 파일 목록 가져오기
        List<File> fileList = fileService.listFilesByChannelId(channelId);
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<File>> ListFilesByMessageId(@PathVariable String messageId) {
        // 채널 ID에 해당하는 파일 목록 가져오기
        List<File> fileList = fileService.listFilesByMessageId(messageId);
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<File>> ListFilesByUserId(@PathVariable Long userId) {
        // 유저 ID에 해당하는 파일 목록 가져오기
        List<File> fileList = fileService.listFilesByUserId(userId);
        return ResponseEntity.ok(fileList);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,  @ModelAttribute FileRequestDto fileRequestDto) {
        System.out.println("Received userId: " + fileRequestDto.getUserId());
        String key = fileService.uploadFile(file, fileRequestDto);
        if (!key.isEmpty()) {
            return ResponseEntity.ok(key);
        } else {
            return ResponseEntity.status(500).body("File upload failed");
        }
    }

    @DeleteMapping("/delete/{fileKey}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileKey) {
        try {
            boolean fileExists = fileService.doesFileExist(fileKey);
            if (!fileExists) {
                return ResponseEntity.ok("File does not exist.");
            }

            fileService.deleteFile(fileKey);
            return ResponseEntity.ok("File delete success.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File delete fail. Error: " + e.getMessage());
        }

    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, String>>> listFiles() {
        List<Map<String, String>> fileList = fileService.listFiles();
        return ResponseEntity.ok(fileList);
    }

    @GetMapping("/download/{fileKey:.+}")
    public void downloadFile(@PathVariable ("fileKey") String fileKey,
                               @RequestParam (value = "downloadFileName", required = false) String downloadFileName,
                               HttpServletRequest request,
                               HttpServletResponse response) throws BadRequestException {
        try {
            boolean success = fileService.downloadFile(fileKey, downloadFileName, request, response);
            System.out.println(success);

            if (!success) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                response.getWriter().write("Error downloading file");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @GetMapping("/presigned-url/{fileName}")
    public ResponseEntity<URL> generatePresignedUrl(@PathVariable String fileName) {
        String contentType = "application/octet-stream";  // 기본 Content-Type
        // 파일 확장자를 기준으로 Content-Type 설정
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
        } else if (fileName.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (fileName.endsWith(".txt")) {
            contentType = "text/plain";
        } else if (fileName.endsWith(".mp4")) {
            contentType = "video/mp4";
        }

        URL presignedUrl = fileService.generatePresignedUrl(fileName, contentType);
        return ResponseEntity.ok(presignedUrl);
    }
}


