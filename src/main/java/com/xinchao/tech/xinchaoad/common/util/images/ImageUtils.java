package com.xinchao.tech.xinchaoad.common.util.images;

import com.xinchao.tech.xinchaoad.common.util.FileConvertUtil;

import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 图片处理工具类：<br>
 * 功能：缩放图像、切割图像、图像类型转换、彩色转黑白、文字水印、图片水印等
 *
 * @author Administrator
 */
public class ImageUtils {
    /**
     * 几种常见的图片格式
     */
    public static String IMAGE_TYPE_GIF = "gif";// 图形交换格式
    public static String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
    public static String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
    public static String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
    public static String IMAGE_TYPE_PNG = "png";// 可移植网络图形
    public static String IMAGE_TYPE_PSD = "psd";// Photoshop的专用格式Photoshop

    public static InputStream getImages1080X880From1280X720(InputStream file1280X720InputStream){
        ByteArrayOutputStream file1080X880OutputStream = new ByteArrayOutputStream();
        BufferedImage file1080X880Image = getResultImages1080X880From1280X720(file1280X720InputStream);
        BufferedImageUtils.saveImage(file1080X880Image,file1080X880OutputStream);
        try {
            return FileConvertUtil.parse(file1080X880OutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getImages1080X880From1280X720(InputStream file1280X720InputStream, OutputStream file1080X880OutputStream){
        BufferedImage file1080X880Image = getResultImages1080X880From1280X720(file1280X720InputStream);
        BufferedImageUtils.saveImage(file1080X880Image,file1080X880OutputStream);
    }
    public static void getImages1080X880From1280X720(File file1280X720,File file1080X880){
        BufferedImage file1080X880Image = getResultImages1080X880From1280X720(file1280X720);
        BufferedImageUtils.saveImage(file1080X880Image,file1080X880);
    }

    private static BufferedImage getResultImages1080X880From1280X720(File file1280X720){
        BufferedImage outputImgBuffer = null;
        try {
            InputStream fileInputStream = new FileInputStream(file1280X720);
            outputImgBuffer = getResultImages1080X880From1280X720(fileInputStream);
        }catch (IOException e){
            e.printStackTrace();
        }
        return outputImgBuffer;
    }
    private static BufferedImage getResultImages1080X880From1280X720(InputStream file1280X720InputStream){
        BufferedImage outputImgBuffer = null;
        try {
            BufferedImage sourceBufferedImage = BufferedImageUtils.getBufferedImage(file1280X720InputStream);
            BufferedImage image1080X607 = BufferedImageUtils.scaleByPrecent(sourceBufferedImage, 0.84375);

            int newWidth = 1080;
            int newHeight = 3880;

            EasyImage test1 = new EasyImage(sourceBufferedImage);
            test1.resize(newWidth, newHeight);
            BufferedImage outputScaleImg = test1.getAsBufferedImage();
            BufferedImage gasResultImage = ImageBlurUtils.blur(outputScaleImg, 100);

            BufferedImage outputgausUpImg = BufferedImageUtils.cut(gasResultImage, 0, 0, 1080, 136);
            BufferedImage outputgausButtomImg = BufferedImageUtils.cut(gasResultImage, 0, newHeight - 137, 1080, 137);

            BufferedImage outputImgBuffer1 = ImageMerge.mergeImage(outputgausUpImg, image1080X607, false, 0, 0);
            outputImgBuffer = ImageMerge.mergeImage(outputImgBuffer1, outputgausButtomImg, false, 0, 0);
        }catch (IOException e){
            e.printStackTrace();
        }
        return outputImgBuffer;
    }


    public static void main(String[] args) throws IOException {
        String inputImg = "D:\\work\\code\\xinchao\\springbootdemo\\src\\main\\resources\\static\\time.jpg";
        String targetImg = "D:\\work\\code\\xinchao\\springbootdemo\\src\\main\\resources\\static\\time_result2.jpg" ;
        String targetImg2 = "D:\\work\\code\\xinchao\\springbootdemo\\src\\main\\resources\\static\\time_result3.jpg" ;


        System.out.println("-----------------------------------------------------");

//        BufferedImage outputImgBuffer =  ImageUtils.getResultImages1080X880From1280X720(new File(inputImg));
//
//        BufferedImageUtils.saveImage(outputImgBuffer,targetImg,ImageUtils.IMAGE_TYPE_JPG);
//        BufferedImageUtils.saveImage(outputImgBuffer,new File(targetImg2));
        File sFile = new File(inputImg);
        System.out.println(sFile.exists() + "--" + sFile.isFile());
        InputStream fileInputStream = new FileInputStream(sFile);
        OutputStream fileOutputStream = new FileOutputStream(new File(targetImg2));
        ImageUtils.getImages1080X880From1280X720(fileInputStream,fileOutputStream);
        fileOutputStream.flush();
    }
}