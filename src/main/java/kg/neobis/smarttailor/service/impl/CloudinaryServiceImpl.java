package kg.neobis.smarttailor.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import kg.neobis.smarttailor.entity.Image;
import kg.neobis.smarttailor.service.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceImpl implements CloudinaryService {

    Cloudinary cloudinary;

    @Override
    public void deleteImage(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    @Override
    public String extractPublicId(String imageUrl) {
        String withoutProtocol = imageUrl.replaceFirst("https?://[^/]+/", "");
        String withoutPath = withoutProtocol.substring(0, withoutProtocol.lastIndexOf("."));
        return withoutPath.substring(withoutPath.lastIndexOf("/") + 1);
    }

    @Override
    public List<Image> saveImages(List<MultipartFile> images) {
        List<Image> imageList = new ArrayList<>();
        for (MultipartFile file : images) {
            imageList.add(new Image(uploadImage(file)));
        }
        return imageList;
    }

    @Override
    public String uploadImage(MultipartFile file){
        Map uploadResult;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (String) uploadResult.get("secure_url");
    }

    @Override
    public void validateImages(List<MultipartFile> images) {
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty() || image.getContentType() == null || !image.getContentType().startsWith("image/")) {
                throw new IllegalArgumentException("The file is not an image!");
            }
        }
    }
}