package com.E2DShare.filemngt.service;

import org.springframework.web.multipart.MultipartFile;
import com.E2DShare.filemngt.entity.Attachment;

public interface AttachmentService {
    Attachment saveAttachment(MultipartFile file) throws Exception;

    Attachment getAttachment(String fileId) throws Exception;
}
