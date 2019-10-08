package com.xinchao.tech.xinchaoad.common.util.images;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

public class BufferedImageUtils {
    /**
     * @param fileUrl 文件绝对路径或相对路径
     * @return 读取到的缓存图像
     * @throws IOException 路径错误或者不存在该文件时抛出IO异常
     */
    public static BufferedImage getBufferedImage(String fileUrl) throws IOException {
        File f = new File(fileUrl);
        return ImageIO.read(f);
    }
    /**
     * @return 读取到的缓存图像
     * @throws IOException 路径错误或者不存在该文件时抛出IO异常
     */
    public static BufferedImage getBufferedImage(InputStream fileInputStream) throws IOException {
        return ImageIO.read(fileInputStream);
    }
    /**
     * @return 读取到的缓存图像
     * @throws IOException 路径错误或者不存在该文件时抛出IO异常
     */
    public static BufferedImage getBufferedImage(File file) throws IOException {
        return ImageIO.read(file);
    }
    /**
     * 缩放图像（按比例缩放）
     * @param scale        缩放比例
     */
    public static BufferedImage scaleByPrecent(BufferedImage source,double scale) {
        int width = source.getWidth(); // 得到源图宽
        int height = source.getHeight(); // 得到源图长

        double targetWidth = width ;
        double targetHeight = height;
        targetWidth = width * scale;
        targetHeight = height * scale;

        Image image = source.getScaledInstance(doubleToInt(targetWidth,0),doubleToInt(targetHeight,0),Image.SCALE_DEFAULT);
        BufferedImage tag = new BufferedImage(doubleToInt(targetWidth,0),doubleToInt(targetHeight,0) ,BufferedImage.TYPE_INT_RGB);
        Graphics g = tag.getGraphics();
        g.drawImage(image, 0, 0, null); // 绘制缩小后的图
        g.dispose();
        return tag;
    }



    /**
     * 图像切割(按指定起点坐标和宽高切割)
     *
     * @param x            目标切片起点坐标X
     * @param y            目标切片起点坐标Y
     * @param width        目标切片宽度
     * @param height       目标切片高度
     */
    public static BufferedImage cut(BufferedImage source,int x, int y, int width, int height) {
        int srcWidth = source.getWidth(); // 源图宽度
        int srcHeight = source.getHeight(); // 源图高度
        if (srcWidth > 0 && srcHeight > 0) {
            Image image = source.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
            // 四个参数分别为图像起点坐标和宽高
            // 即: CropImageFilter(int x,int y,int width,int height)
            ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
            Image img = Toolkit.getDefaultToolkit().createImage(
                    new FilteredImageSource(image.getSource(),
                            cropFilter));
            BufferedImage targetImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = targetImage.getGraphics();
            g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
            g.dispose();
            return targetImage;
        }
        return null;
    }


    private static int doubleToInt(double number,int scale){
        BigDecimal bd=new BigDecimal(number).setScale(scale, BigDecimal.ROUND_FLOOR);
        return Integer.parseInt(bd.toString());
    }


    /**
     * @param savedImg 待保存的图像
     * @param targetfileName 保存的文件路径，必须带后缀，比如 "beauty.jpg"
     * @param format   文件格式：jpg、png或者bmp
     * @return
     */
    public static boolean saveImage(BufferedImage savedImg, String targetfileName, String format) {
        boolean flag = false;
        String fileUrl = targetfileName;
        File file = new File(fileUrl);
        try {
            flag = ImageIO.write(savedImg, format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * @param savedImg 待保存的图像
     * @param targetfile 保存的文件，必须带后缀，比如 "beauty.jpg"
     * @return
     */
    public static void saveImage(BufferedImage savedImg, File targetfile) {
        try {
            String fileName = targetfile.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            ImageIO.write(savedImg, suffix, targetfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * @param savedImg 待保存的图像
     * @param targetfile 保存的文件，必须带后缀，比如 "beauty.jpg"
     * @return
     */
    public static void saveImage(BufferedImage savedImg, OutputStream targetfile) {
        try {
            ImageIO.write(savedImg, "jpg", targetfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
