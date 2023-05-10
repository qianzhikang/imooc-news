package com.imooc.files.service.impl;

import cn.hutool.core.lang.UUID;
import com.imooc.files.minio.MinioProperties;
import com.imooc.files.minio.MinioUtil;
import com.imooc.files.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Description 实现上传
 * @Date 2023-05-09-15-49
 * @Author qianzhikang
 */
@Service
public class UploadServiceImpl implements UploadService {
    @Resource
    private MinioProperties minioProperties;

    @Override
    public String uploadMinio(MultipartFile multipartFile,String suffix) throws Exception {
        // 文件目录 + 文件名（随机uuid + 时间戳）
        String fileName = "file/" + UUID.randomUUID(false) + System.currentTimeMillis() + "." + suffix;
        MinioUtil.uploadFile(minioProperties.getBucket(),multipartFile,fileName);
        String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/" + fileName;
        return url;
    }
}
