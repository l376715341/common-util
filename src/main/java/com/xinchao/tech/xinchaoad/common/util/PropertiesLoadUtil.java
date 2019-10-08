package com.xinchao.tech.xinchaoad.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

@Slf4j
public class PropertiesLoadUtil {

    public static String loadAsString(String resourceLocation){
        InputStream inputStream=loadAsInputStream(resourceLocation);
        if(inputStream!=null){
            try {
                return IOUtils.toString(inputStream,Charset.forName("UTF-8"));
            }catch (Exception ex){
                log.error("read file as string err,location:{}",resourceLocation,ex);
            }
        }
        return null;
    }

    public static Properties loadAsProperties(String resourceLocation){
        InputStream inputStream=loadAsInputStream(resourceLocation);
        Properties properties=new Properties();
        if(inputStream!=null){
            try {
                properties.load(inputStream);
            }catch (Exception ex){
                log.error("read file as properties err,location:{}",resourceLocation,ex);
            }
        }
        return properties;
    }

    /**
     * spring boot 会把properties打包到jar
     * 所以默认先file加载外部的文件（主要支持debug和外部properties文件）
     * 如果加载外部文件失败，则用inputStream去通过Resource加载
     * @param resourceLocation
     * @return
     */
    public static InputStream loadAsInputStream(String resourceLocation) {
        try {
            File file = ResourceUtils.getFile(resourceLocation);
            return new FileInputStream(file);

        } catch (Exception ex) {
            log.error("load config file as file error,location:{}", resourceLocation);
            try {
                Resource resource = new ClassPathResource(resourceLocation);
                return resource.getInputStream();
            } catch (Exception e) {
                log.error("load config file as resource error,location:{}", resourceLocation, ex);
                return null;
            }

        }
    }
}
