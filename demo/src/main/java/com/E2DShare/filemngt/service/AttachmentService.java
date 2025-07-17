package com.E2DShare.filemngt.service;

import org.springframework.web.multipart.MultipartFile;
import com.E2DShare.filemngt.entity.Attachment;

import java.util.List;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file, String senderId, String receiverId) throws Exception;

    Attachment getAttachment(String fileId) throws Exception;

    byte[] getDecryptedAttachmentData(String fileId, String receiverId) throws Exception;

    List<Attachment> getReceivedAttachments(String receiverId);
    List<Attachment> getAllReceivedFiles(String receiverId) throws Exception;


}
