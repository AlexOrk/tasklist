package orkhoian.aleksei.tasklist.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import orkhoian.aleksei.tasklist.domain.exception.ImageUploadException;
import orkhoian.aleksei.tasklist.domain.task.TaskImage;
import orkhoian.aleksei.tasklist.service.ImageService;
import orkhoian.aleksei.tasklist.service.props.MinioProperties;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Autowired
    public ImageServiceImpl(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    @Override
    public String upload(TaskImage image) {
        try {
            createBucket();
        } catch (Exception ex) {
            throw new ImageUploadException("Image upload failed: " + ex.getMessage());
        }

        MultipartFile file = image.getFile();
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageUploadException("Image must have name");
        }
        String fileName = generateFileName(file);
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (Exception ex) {
            throw new ImageUploadException("Exception was thrown while trying to get inputStream: " + ex.getMessage());
        }
        saveImage(inputStream, fileName);
        return fileName;
    }

    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, inputStream.available(), -1)
                .bucket(minioProperties.getBucket())
                .object(fileName)
                .build());
    }
}
