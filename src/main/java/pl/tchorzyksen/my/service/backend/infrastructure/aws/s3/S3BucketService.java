package pl.tchorzyksen.my.service.backend.infrastructure.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.tchorzyksen.my.service.backend.infrastructure.object_storage.ObjectStorageService;
import pl.tchorzyksen.my.service.backend.model.ListObjectsResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static pl.tchorzyksen.my.service.backend.model.ListObjectsResult.ObjectSummary;
import static pl.tchorzyksen.my.service.backend.shared.DateUtils.convertToLocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
class S3BucketService implements ObjectStorageService {

  private final AmazonS3 amazonS3Client;

  @Value("${app.object-storage.buckets.logo-name}")
  private String bucketName;

  @Override
  public byte[] getObject(String key) throws IOException {
    return amazonS3Client.getObject(bucketName, key).getObjectContent().readAllBytes();
  }

  @Override
  public ListObjectsResult getObjects(String userId) {
    var listObjectsV2Result = amazonS3Client.listObjectsV2(bucketName, userId);
    return mapToListObjectsResult(listObjectsV2Result);
  }

  @Override
  public void putObject(String key, InputStream inputStream, Map<String, Object> metadata) {
    var objectMetadata = new ObjectMetadata();
    metadata.forEach(objectMetadata::setHeader);
    log.info("Upload file with key {} to s3 bucket {}", key, bucketName);
    amazonS3Client.putObject(bucketName, key, inputStream, objectMetadata);
  }

  private ListObjectsResult mapToListObjectsResult(ListObjectsV2Result listObjectsV2Result) {
    var objectSummaryList = listObjectsV2Result.getObjectSummaries().stream()
            .map(s3ObjectSummary -> new ObjectSummary(s3ObjectSummary.getKey(), convertToLocalDateTime(s3ObjectSummary.getLastModified()))).toList();
    return new ListObjectsResult(listObjectsV2Result.getBucketName(), listObjectsV2Result.getPrefix(), objectSummaryList);
  }

}
