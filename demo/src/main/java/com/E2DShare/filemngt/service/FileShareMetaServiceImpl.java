package com.E2DShare.filemngt.service;

import com.E2DShare.filemngt.entity.FileShareMetadata;
import com.E2DShare.filemngt.repository.FileShareMetaRepository;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileShareMetaServiceImpl implements FileShareMetaService {

    private final FileShareMetaRepository fileShareMetadataRepository;

    public FileShareMetaServiceImpl(FileShareMetaRepository fileShareMetadataRepository) {
        this.fileShareMetadataRepository = fileShareMetadataRepository;
    }

    @Override
    public void saveMetadata(String attachmentId, String senderId, String receiverId) {
        FileShareMetadata metadata = new FileShareMetadata();
        metadata.setAttachmentId(attachmentId);
        metadata.setSenderId(senderId);
        metadata.setReceiverId(receiverId);
        fileShareMetadataRepository.save(metadata);
    }

    @Override
    public List<String> getAttachmentIdsForReceiver(String receiverId) {
        return fileShareMetadataRepository.findByReceiverId(receiverId)
                .stream()
                .map(FileShareMetadata::getAttachmentId)
                .toList();
    }




}
