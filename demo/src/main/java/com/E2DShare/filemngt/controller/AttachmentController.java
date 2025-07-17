package com.E2DShare.filemngt.controller;

import com.E2DShare.filemngt.entity.Attachment;
import com.E2DShare.filemngt.model.ResponseData;
import com.E2DShare.filemngt.service.AttachmentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("senderId") String senderId,
            @RequestParam("receiverId") String receiverId) {
        try {
            Attachment attachment = attachmentService.saveAttachment(file, senderId, receiverId);

            String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/download/")
                    .path(attachment.getId())
                    .toUriString();

            ResponseData responseData = new ResponseData(
                    attachment.getFileName(),
                    downloadURL,
                    attachment.getFileType(),
                    attachment.getData().length
            );

            return ResponseEntity.ok(responseData);
        } catch (SecurityException se) {
            return ResponseEntity.status(403)
                    .body("Upload blocked: malware detected in file.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileId,
            @RequestParam("receiverId") String receiverId) throws Exception {

        byte[] decryptedData = attachmentService.getDecryptedAttachmentData(fileId, receiverId);
        Attachment attachment = attachmentService.getAttachment(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(new ByteArrayResource(decryptedData));
    }

    @GetMapping("/received")
    public ResponseEntity<?> viewReceivedFiles(@RequestParam("receiverId") String receiverId) {
        try {
            List<Attachment> files = attachmentService.getAllReceivedFiles(receiverId);

            List<ResponseData> responses = files.stream().map(file -> new ResponseData(
                    file.getFileName(),
                    "http://localhost:8080/download/" + file.getId() + "?receiverId=" + receiverId,
                    file.getFileType(),
                    file.getData().length
            )).toList();

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/received/{receiverId}")
    public ResponseEntity<?> getReceivedFiles(@PathVariable String receiverId) {
        List<Attachment> received = attachmentService.getReceivedAttachments(receiverId);

        List<ResponseData> responses = received.stream().map(a ->
                new ResponseData(
                        a.getFileName(),
                        "http://localhost:8080/download/" + a.getId() + "?receiverId=" + receiverId,
                        a.getFileType(),
                        a.getData().length
                )
        ).toList();

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
