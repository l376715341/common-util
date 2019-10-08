package com.xinchao.tech.xinchaoad.common.util.images;


import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


/**
 * <p>Title: PictureMerge</p>
 * <p>Description: 图片合并</p>
 * <p>Company: DINGGE</p>
 *
 * @author FANQIBU
 * @date 2017年12月1日
 */
public class ImageMerge {
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
     * @param savedImg 待保存的图像
     * @param saveDir  保存的目录
     * @param fileName 保存的文件名，必须带后缀，比如 "beauty.jpg"
     * @param format   文件格式：jpg、png或者bmp
     * @return
     */
    public static boolean saveImage(BufferedImage savedImg, String saveDir, String fileName, String format) {
        boolean flag = false;
        // 先检查保存的图片格式是否正确
        String[] legalFormats = {"jpg", "JPG", "png", "PNG", "bmp", "BMP"};
        int i = 0;
        for (i = 0; i < legalFormats.length; i++) {
            if (format.equals(legalFormats[i])) {
                break;
            }
        }
        if (i == legalFormats.length) { // 图片格式不支持
            System.out.println("不是保存所支持的图片格式!");
            return false;
        }

        // 再检查文件后缀和保存的格式是否一致
        String postfix = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (!postfix.equalsIgnoreCase(format)) {
            System.out.println("待保存文件后缀和保存的格式不一致!");
            return false;
        }

        String fileUrl = saveDir + fileName;
        File file = new File(fileUrl);
        try {
            flag = ImageIO.write(savedImg, format, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 待合并的两张图必须满足这样的前提，如果水平方向合并，则高度必须相等；如果是垂直方向合并，宽度必须相等。
     * mergeImage方法不做判断，自己判断。
     *
     * @param img1         待合并的第一张图
     * @param img2         带合并的第二张图
     * @param isHorizontal 为true时表示水平方向合并，为false时表示垂直方向合并
     * @return 返回合并后的BufferedImage对象
     * @throws IOException
     */
    public static BufferedImage mergeImage(BufferedImage img1, BufferedImage img2, boolean isHorizontal, int startX, int startY) throws IOException {
        int w1 = img1.getWidth();
        int h1 = img1.getHeight();
        int w2 = img2.getWidth();
        int h2 = img2.getHeight();

        // 从图片中读取RGB
        int[] imageArrayOne = new int[w1 * h1];
        imageArrayOne = img1.getRGB(0, 0, w1, h1, imageArrayOne, 0, w1); // 逐行扫描图像中各个像素的RGB到数组中
        int[] imageArrayTwo = new int[w2 * h2];
        imageArrayTwo = img2.getRGB(0, 0, w2, h2, imageArrayTwo, 0, w2);

        // 生成新图片
        BufferedImage destImage = null;
        if (isHorizontal) { // 水平方向合并
            destImage = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_RGB);
            destImage.setRGB(0, 0, w1, h1, imageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            destImage.setRGB(startX, startY, w2, h2, imageArrayTwo, 0, w2); // 设置下半部分的RGB
        } else { // 垂直方向合并
            destImage = new BufferedImage(w1, h1 + h2, BufferedImage.TYPE_INT_RGB);
            destImage.setRGB(0, 0, w1, h1, imageArrayOne, 0, w1); // 设置上半部分或左半部分的RGB
            destImage.setRGB(0, h1, w2, h2, imageArrayTwo, 0, w2); // 设置下半部分的RGB
        }
        return destImage;
    }

    /**
     * <p>Title: getImageStream</p>
     * <p>Description: 获取图片InputStream</p>
     *
     * @param destImg
     * @return
     */
    public static InputStream getImageStream(BufferedImage destImg) {
        InputStream is = null;

        BufferedImage bi = destImg;

        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        ImageOutputStream imOut;
        try {
            imOut = ImageIO.createImageOutputStream(bs);

            ImageIO.write(bi, "png", imOut);

            is = new ByteArrayInputStream(bs.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    /**
     * <p>Title: drawTextInImg</p>
     * <p>Description: 图片上添加文字业务需求要在图片上添加水</p>
     *
     * @param text
     */
    public static BufferedImage drawTextInImg(BufferedImage bImage, FontText text, int left, int top) {
        Graphics2D g = bImage.createGraphics();
        g.setColor(getColor(text.getWmTextColor()));
        g.setBackground(Color.white);
        Font font = new Font(text.getWmTextFont(), Font.BOLD,
                text.getWmTextSize());
        g.setFont(font);
        //g.drawBytes(text.getText().getBytes(), 0,text.getText().getBytes().length, left-50, top-50);
        g.drawString(text.getText(), left, top);
        g.dispose();
        return bImage;
    }

    // color #2395439
    public static Color getColor(String color) {
        if (color.charAt(0) == '#') {
            color = color.substring(1);
        }
        if (color.length() != 6) {
            return null;
        }
        try {
            int r = Integer.parseInt(color.substring(0, 2), 16);
            int g = Integer.parseInt(color.substring(2, 4), 16);
            int b = Integer.parseInt(color.substring(4), 16);
            return new Color(r, g, b);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }


}