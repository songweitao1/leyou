package com.leyou.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.emuns.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.config.UpLoadPropertyConfig;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@EnableConfigurationProperties(UpLoadPropertyConfig.class)
public class UploadService {
    @Autowired
    private UpLoadPropertyConfig prop;

    @Autowired
    private FastFileStorageClient storageClient;



    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    public String upload(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        // 校验文件的类型
        String contentType = file.getContentType();
        System.out.println(prop.getAllowTypes() + "======================================================================================================");
        if (!prop.getAllowTypes().contains(contentType)){
            // 文件类型不合法，直接返回null
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }

        try {
            // 校验文件的内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null){
                throw new LyException(ExceptionEnum.NOT_ALLOW_TYPE);
            }

            // 保存到服务器
            // file.transferTo(new File("C:\\leyou\\images\\" + originalFilename));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            // 生成url地址，返回
            System.out.println(storePath.getFullPath());
            return prop.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
           throw new LyException(ExceptionEnum.NOT_ALLOW_TYPE);
        }
    }
}