package com.E2DShare.filemngt.service;

import com.E2DShare.filemngt.entity.Attachment;
import com.E2DShare.filemngt.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static com.E2DShare.filemngt.security.HybridEncryptionUtil.generateAESKey;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final MalwareScanner malwareScanner;
    private final UserKeyService userKeyService;
    private final FileShareMetaServiceImpl fileShareMetaServiceImpl;

    public AttachmentServiceImpl(
            AttachmentRepository attachmentRepository,
            MalwareScanner malwareScanner,
            UserKeyService userKeyService,
            FileShareMetaServiceImpl fileShareMetaServiceImpl
    ) {
        this.attachmentRepository = attachmentRepository;
        this.malwareScanner = malwareScanner;
        this.userKeyService = userKeyService;
        this.fileShareMetaServiceImpl = fileShareMetaServiceImpl;
    }

    @Override
    public Attachment saveAttachment(MultipartFile file, String senderId, String receiverId) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (fileName.contains("..")) {
            throw new Exception("File name contains invalid path sequence: " + fileName);
        }

        if (!malwareScanner.isFileClean(file)) {
            throw new SecurityException("Malware detected in file: " + file.getOriginalFilename());
        }

        SecretKey aesKey = generateAESKey();
        byte[] encryptedData = encryptWithAES(file.getBytes(), aesKey);

        PublicKey publicKey = userKeyService.getReceiverPublicKey(receiverId);
        byte[] encryptedKey = encryptWithRSA(aesKey.getEncoded(), publicKey);

        //First save Attachment to get the generated ID

        Attachment attachment = new Attachment(fileName, file.getContentType(), encryptedData);
        attachment.setEncryptedKey(Base64.getEncoder().encodeToString(encryptedKey));
        Attachment saved = attachmentRepository.save(attachment);

        //Then save Metadata with attachmentID

        fileShareMetaServiceImpl.saveMetadata(saved.getId(), senderId, receiverId);
        return saved;
    }

    @Override
    public Attachment getAttachment(String fileId) throws Exception {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new Exception("File not found with Id: " + fileId));
    }

    private byte[] encryptWithAES(byte[] data, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.doFinal(data);
    }

    private byte[] encryptWithRSA(byte[] aesKeyBytes, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(aesKeyBytes);
    }

    @Override
    public byte[] getDecryptedAttachmentData(String fileId, String receiverId) throws Exception {
        Attachment attachment = getAttachment(fileId);

        // Decode AES key (Base64 -> bytes)
        byte[] encryptedAesKey = Base64.getDecoder().decode(attachment.getEncryptedKey());

        // Get private key of receiver
        PrivateKey privateKey = userKeyService.getReceiverPrivateKey(receiverId);

        // Decrypt AES key
        Cipher rsa = Cipher.getInstance("RSA");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsa.doFinal(encryptedAesKey);
        SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // Decrypt file data
        Cipher aes = Cipher.getInstance("AES");
        aes.init(Cipher.DECRYPT_MODE, aesKey);
        return aes.doFinal(attachment.getData());
    }

    @Override
    public List<Attachment> getReceivedAttachments(String receiverId) {
        List<String> attachmentIds = fileShareMetaServiceImpl
                .getAttachmentIdsForReceiver(receiverId);
        return attachmentRepository.findAllById(attachmentIds);
    }

    @Override
    public List<Attachment> getAllReceivedFiles(String receiverId) throws Exception {
        List<String> attachmentIds = fileShareMetaServiceImpl.getAttachmentIdsForReceiver(receiverId);
        List<Attachment> files = new ArrayList<>();

        for (String id : attachmentIds) {
            files.add(getAttachment(id));
        }

        return files;
    }



}

