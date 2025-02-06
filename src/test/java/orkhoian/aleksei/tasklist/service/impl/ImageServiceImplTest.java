package orkhoian.aleksei.tasklist.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import orkhoian.aleksei.tasklist.domain.exception.ImageUploadException;
import orkhoian.aleksei.tasklist.service.props.MinioProperties;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        when(minioProperties.getBucket()).thenReturn("test-bucket");
    }

    @SneakyThrows
    @Test
    @DisplayName("Upload image successfully")
    void upload() {
        InputStream inputStream = new ByteArrayInputStream(new byte[]{1, 2, 3});

        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String actualFileName = imageService.upload(multipartFile);

        assertNotNull(actualFileName);
        assertTrue(actualFileName.endsWith(".jpg"));
        verify(minioProperties, times(2)).getBucket();
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Upload image failed while creating bucket")
    void uploadFail() {
        when(minioProperties.getBucket()).thenReturn(null);
        assertThrows(ImageUploadException.class, () -> imageService.upload(multipartFile));
    }

    @Test
    @DisplayName("Upload image failed because images file is empty or does not contain original name")
    void uploadFail2() {
        when(multipartFile.isEmpty()).thenReturn(true);
        assertThrows(ImageUploadException.class, () -> imageService.upload(multipartFile));

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        assertThrows(ImageUploadException.class, () -> imageService.upload(multipartFile));
    }

    @SneakyThrows
    @Test
    @DisplayName("Upload image failed while trying to get inputStream")
    void uploadFail3() {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("image.jpg");
        when(multipartFile.getInputStream()).thenThrow(new RuntimeException());

        assertThrows(ImageUploadException.class, () -> imageService.upload(multipartFile));
    }

}
