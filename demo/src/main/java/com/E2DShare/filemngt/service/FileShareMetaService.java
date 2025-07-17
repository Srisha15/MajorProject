package com.E2DShare.filemngt.service;

import java.util.List;

public interface FileShareMetaService {

        void saveMetadata(String attachmentId, String senderId, String receiverId);
        List<String> getAttachmentIdsForReceiver(String receiverId);



}
