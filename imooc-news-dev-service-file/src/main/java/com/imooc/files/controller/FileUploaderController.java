package com.imooc.files.controller;

import com.imooc.api.controller.files.FileUploaderControllerApi;
import com.imooc.bo.NewAdminBO;
import com.imooc.exception.GraceException;

import com.imooc.files.service.UploadService;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.classhierarchy.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploaderController implements FileUploaderControllerApi {

    final static Logger logger = LoggerFactory.getLogger(FileUploaderController.class);

    @Autowired
    private UploadService uploaderService;


    @Override
    public GraceJSONResult uploadFace(String userId,
                                      MultipartFile file) throws Exception {
        String url = "";
        if (file != null) {
            // 获得文件上传的名称
            String fileName = file.getOriginalFilename();

            // 判断文件名不能为空
            if (StringUtils.isNotBlank(fileName)) {
                String fileNameArr[] = fileName.split("\\.");
                // 获得后缀
                String suffix = fileNameArr[fileNameArr.length - 1];
                // 判断后缀符合我们的预定义规范
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")
                ) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }
                // 执行上传
                url = uploaderService.uploadMinio(file, suffix);

            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        } else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
        logger.info("url = " + url);
        return GraceJSONResult.ok(url);
    }
}
