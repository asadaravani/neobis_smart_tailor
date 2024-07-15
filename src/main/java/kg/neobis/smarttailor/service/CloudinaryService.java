package kg.neobis.smarttailor.service;

import kg.neobis.smarttailor.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CloudinaryService {

    void deleteImage(String imageUrl) throws IOException;

    String extractPublicId(String imageUrl);

    Image saveImage(MultipartFile image);

    List<Image> saveImages(List<MultipartFile> images);

    String uploadImage(MultipartFile file) throws IOException;

}