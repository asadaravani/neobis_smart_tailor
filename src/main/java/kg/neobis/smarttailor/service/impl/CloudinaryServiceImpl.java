package kg.neobis.smarttailor.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import kg.neobis.smarttailor.service.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryServiceImpl implements CloudinaryService {

    Cloudinary cloudinary;

    @Override
    public String uploadImage(MultipartFile file){
        Map uploadResult = null;
        try {
            uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return (String) uploadResult.get("secure_url");
    }
    @Override
    public void deleteProductImage(String imageUrl) throws IOException {
        String publicId = extractPublicId(imageUrl);
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
    public String extractPublicId(String imageUrl) {
        String withoutProtocol = imageUrl.replaceFirst("https?://[^/]+/", "");
        String withoutPath = withoutProtocol.substring(0, withoutProtocol.lastIndexOf("/"));
        return withoutPath.substring(withoutPath.lastIndexOf("/") + 1, withoutPath.lastIndexOf("."));
    }
}