package com.imooc.files.controller;

import com.imooc.api.controller.files.FileUploaderControllerApi;
import com.imooc.bo.NewAdminBO;
import com.imooc.exception.GraceException;

import com.imooc.files.service.UploadService;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;

import com.imooc.utils.FileUtils;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
public class FileUploaderController implements FileUploaderControllerApi {

    final static Logger logger = LoggerFactory.getLogger(FileUploaderController.class);

    @Autowired
    private UploadService uploaderService;

    @Resource
    private GridFSBucket gridFSBucket;


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

    /**
     * 上传多个文件
     *
     * @param userId
     * @param files
     * @return
     * @throws Exception
     */
    @Override
    public GraceJSONResult uploadSomeFiles(String userId, MultipartFile[] files) throws Exception {
        List<String> imageUrlList = new ArrayList<>();
        for (MultipartFile file : files) {
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
                    imageUrlList.add(uploaderService.uploadMinio(file, suffix));

                } else {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
                }
            } else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
            }
        }
        return GraceJSONResult.ok(imageUrlList);
    }

    /**
     * 文件上传到mongodb的gridfs中
     *
     * @param newAdminBO
     * @return
     * @throws Exception
     */
    @Override
    public GraceJSONResult uploadToGridFS(NewAdminBO newAdminBO) throws Exception {
        // 获得图片的base64
        String img64 = newAdminBO.getImg64();
        // 将base64字符串转换为byte数组
        byte[] bytes = new BASE64Decoder().decodeBuffer(img64.trim());
        // 获取byte数组的流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 上传返回mongodb中的文件主键
        ObjectId fileId = gridFSBucket.uploadFromStream(newAdminBO.getUsername() + ".png", byteArrayInputStream);
        // 返回主键
        return GraceJSONResult.ok(fileId.toString());
    }

    /**
     * 从gridfs中读取图片内容
     *
     * @param faceId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public void readInGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }
        // 从GridFS中读取文件
        File file = readGridFSByFaceId(faceId);
        // 将文件展示到浏览器
        FileUtils.downloadFileByStream(response,file);
    }


    private File readGridFSByFaceId(String faceId) throws Exception {

        // 按_id主健查询文件
        GridFSFindIterable gridFSFiles
                = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        GridFSFile gridFS = gridFSFiles.first();

        if (gridFS == null) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        // 文件名
        String fileName = gridFS.getFilename();
        System.out.println(fileName);

        // 获取文件流，保存文件到本地或者服务器的临时目录
        File fileTemp = new File("temp_face");
        if (!fileTemp.exists()) {
            fileTemp.mkdirs();
        }

        File myFile = new File("temp_face/" + fileName);

        // 创建文件输出流
        OutputStream os = new FileOutputStream(myFile);
        // 下载到服务器或者本地
        gridFSBucket.downloadToStream(new ObjectId(faceId), os);

        return myFile;
    }


    @Override
    public GraceJSONResult readFace64InGridFS(String faceId,
                                              HttpServletRequest request,
                                              HttpServletResponse response)
            throws Exception {

        // 0. 获得gridfs中人脸文件
        File myface = readGridFSByFaceId(faceId);

        // 1. 转换人脸为base64
        String base64Face = FileUtils.fileToBase64(myface);

        return GraceJSONResult.ok(base64Face);
    }
}
