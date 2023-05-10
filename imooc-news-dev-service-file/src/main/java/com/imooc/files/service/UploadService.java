package com.imooc.files.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Description 上传服务
 * @Date 2023-05-09-15-48
 * @Author qianzhikang
 */
public interface UploadService {
    String uploadMinio(MultipartFile multipartFile,String suffix) throws Exception;
}
