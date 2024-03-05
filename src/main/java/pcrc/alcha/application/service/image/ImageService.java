package pcrc.alcha.application.service.image;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pcrc.alcha.application.domain.img.ImageType;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Locale;

@Slf4j
@Service
public class ImageService implements ImageOperationUseCase {

    @Value("local.img.user.profile")
    private String PROFILE_IMAGE_PATH;

    @Value("${local.img.user.profile.default}")
    private String PROFILE_DEFAULT_IMAGE_PATH;

    @Value("${local.img.nest.thumbnails}")
    private String NEST_THUMBNAIL_IMAGE_PATH;

    @Override
    public String save(String base64, ImageType type) {

        if (requestDefaultProfileImage(base64, type)) return PROFILE_DEFAULT_IMAGE_PATH;

        byte[] decoded = Base64.getDecoder()
                .decode(base64);

        validateImage(decoded);

        try {
            ImageInfo imageInfo = Imaging.getImageInfo(decoded);
            final String url = generateUrl(imageInfo, type);

            try (OutputStream outputStream = new FileOutputStream(url)) {
                outputStream.write(decoded);
                return url;
            } catch (IOException e) {
                throw new AlchaException(MessageType.INTERNAL_SERVER_ERROR_IMAGE_SAVE);
            }
        } catch (IOException | ImageReadException e) {
            throw new AlchaException(MessageType.INTERNAL_SERVER_ERROR_IMAGE_SAVE);
        }
    }

    private boolean requestDefaultProfileImage(String base64, ImageType type) {
        return base64 == null && type == ImageType.PROFILE;
    }

    private String generateUrl(ImageInfo imageInfo, ImageType type) {
        String name = LocalDateTime.now() + "." + imageInfo.getFormat().getName().toLowerCase(Locale.ROOT);

        switch (type) {
            case PROFILE -> {
                return PROFILE_IMAGE_PATH + name;
            }
            case THUMBNAIL -> {
                return NEST_THUMBNAIL_IMAGE_PATH + name;
            }
            default -> throw new AlchaException(MessageType.INTERNAL_SERVER_ERROR_IMAGE_SAVE);
        }

    }

    private void validateImage(byte[] img) {
        try {
            ImageInfo imageInfo = Imaging.getImageInfo(img);
            final String[] validType = {"image/png", "image/jpeg"};

            String mimeType = imageInfo.getMimeType();
            boolean isMatched = false;
            for (String type : validType) {
                if (mimeType.equals(type)) {
                    isMatched = true;
                    break;
                }
            }
            if (!isMatched) {
                throw new AlchaException(MessageType.INVALID_IMAGE_TYPE);
            }
        } catch (ImageReadException | IOException e) {
            throw new AlchaException(MessageType.INVALID_IMAGE);
        }
    }


}
