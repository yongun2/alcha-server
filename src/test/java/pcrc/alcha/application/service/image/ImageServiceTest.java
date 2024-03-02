package pcrc.alcha.application.service.image;

import org.apache.commons.imaging.ImageInfo;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageServiceTest {

    @Value("${local.img.directory}")
    private String LOCAL_SAVE_DIRECTORY_PATH;

    @Autowired
    private ImageService service;

    @Test
    @DisplayName("로컬에 이미지 파일 저장")
    void saveToLocal() throws IOException, ImageReadException {
        // given
        final String LOCAL_PATH = "/Users/gyeyong-un/Desktop/code.png";
        File file = new File(LOCAL_PATH);
        FileInputStream fis = new FileInputStream(file);

        byte[] bytesArray = new byte[(int) file.length()];
        fis.read(bytesArray); // 파일의 모든 내용을 읽어 바이트 배열에 저장
        fis.close();

        String imgBase64 = Base64.getEncoder()
                .encodeToString(bytesArray);
        ImageInfo imageInfo = Imaging.getImageInfo(bytesArray);

        // when
        String path = service.save(imgBase64);

        // then
        assertThat(path).isEqualTo(LOCAL_SAVE_DIRECTORY_PATH + "profile." + imageInfo.getFormat().getName().toLowerCase(Locale.ROOT));
    }
}