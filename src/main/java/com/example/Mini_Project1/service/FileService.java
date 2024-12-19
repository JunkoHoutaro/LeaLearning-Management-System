package com.example.Mini_Project1.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.response.file.FileResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class FileService {
    private final Cloudinary cloudinary;
    private final List<String> VALID_TYPES = List.of("image", "video", "raw");

    public FileResponse uploadResource(String cloudFolderPath, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));

        String public_id = isSupportedFileType(file) ? fileNameWithoutExtension : fileName;

        Map params = ObjectUtils.asMap(
          "resource_type", "auto",
          "public_id", public_id,
          "asset_folder", cloudFolderPath,
          "overwrite", "true"
        );

        Map response = cloudinary.uploader().upload(file.getBytes(), params);

        String url = response.get("secure_url").toString();
        Integer size = (Integer) response.get("bytes");

        return new FileResponse("ok",size,url);
    }

    public FileResponse removeResource(String url) throws Exception {
        String resourceType = extractResourceType(url);
        String publicId = extractPublicId(url,resourceType);

        Map resources = cloudinary.api().resource(publicId, ObjectUtils.asMap("resource_type",resourceType));
        Integer size = (Integer)resources.get("bytes");

        Map response = cloudinary.uploader().destroy(publicId,ObjectUtils.asMap("resource_type",resourceType));
        String result = response.get("result").toString();

        return new FileResponse(result, size, url);
    }

    private boolean isSupportedFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.startsWith("image/") || contentType.startsWith("video/"));
    }

    private String extractResourceType(String url) {
        String afterCloudName = url.split("res.cloudinary.com/")[1];
        String resourceType = afterCloudName.substring(afterCloudName.indexOf("/") + 1, afterCloudName.indexOf("/upload/"));

        if(!VALID_TYPES.contains(resourceType))
            throw new NotFoundException("Can't find resource type");

        return resourceType;
    }

    private String extractPublicId(String url, String resourceType) {
        int lastSlashIndex = url.lastIndexOf("/");
        String fileName = url.substring(lastSlashIndex + 1);

        if(resourceType.equals("raw")) return fileName;
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}
