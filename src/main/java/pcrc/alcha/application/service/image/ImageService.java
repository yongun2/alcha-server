package pcrc.alcha.application.service.image;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Locale;

@Slf4j
@Service
public class ImageService implements ImageOperationUseCase {

    @Value("${local.img.directory}")
    private String IMAGE_PATH;
    @Value("${local.img.default}")
    private String DEFAULT_IMAGE_PATH;

    @Override
    public String save(String base64) {

        if (base64 == null) {
            return DEFAULT_IMAGE_PATH;
        }

        byte[] decoded = Base64.getDecoder()
                .decode(base64);

        validateImage(decoded);

        try {
            ImageInfo imageInfo = Imaging.getImageInfo(decoded);
            final String url = IMAGE_PATH + "profile." + imageInfo.getFormat().getName().toLowerCase(Locale.ROOT);

            try (OutputStream outputStream = new FileOutputStream(url)) {
                outputStream.write(decoded);
                return url;
            } catch (IOException e) {
                throw new AlchaException(MessageType.INTERNAL_SERVER_ERROR_IMAGE_SAVE);
            }
        } catch (IOException | ImageReadException e) {
            throw new RuntimeException(e);
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
