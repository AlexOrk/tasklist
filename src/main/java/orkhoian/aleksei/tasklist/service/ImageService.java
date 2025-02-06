package orkhoian.aleksei.tasklist.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String upload(MultipartFile file);
}
