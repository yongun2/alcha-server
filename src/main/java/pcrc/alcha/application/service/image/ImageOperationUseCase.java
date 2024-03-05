package pcrc.alcha.application.service.image;

import pcrc.alcha.application.domain.img.ImageType;

public interface ImageOperationUseCase {

    String save(String base64, ImageType type);
}
