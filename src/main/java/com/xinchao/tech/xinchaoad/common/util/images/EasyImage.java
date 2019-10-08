package com.xinchao.tech.xinchaoad.common.util.images;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * EasyImage lets you do all the basic image operations -
 * converting, cropping, resizing, rotating, flipping…
 * Plus it let’s you do some really cool affects.
 * All is done super easily.
 * Combining operations can produce some very cool results.
 *
 * Operations:
 * Open image.
 * Save image
 * Convert image
 * Re-size image
 * Crop image
 * Convert to black and white image
 * Rotate image
 * Flip image
 * Add color to image
 * Create image with multiple instance of the original
 * Combining 2 images together
 * Emphasize parts of the image
 * Affine transform image
 *
 *
 * @author Avi Yehuda
 *
 */
public class EasyImage {
    private BufferedImage bufferedImage;
    private String fileName;

    /**
     * Constructor - loads from an image file.
     * @param imageFile
     */
    public EasyImage(File imageFile) {
        try {
            bufferedImage = ImageIO.read(imageFile);
            fileName = imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            bufferedImage = null;
            imageFile = null;
        }
    }
    /**
     * Constructor - loads from an image file.
     * @param inputStream
     */
    public EasyImage(InputStream inputStream) {
        try {
            bufferedImage = ImageIO.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            bufferedImage = null;
        }
    }
    /**
     * Constructor - loads from an image file.
     * @param bufferedImage
     */
    public EasyImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
    /**
     * Constructor - loads from an image file.
     * @param imageFilePath
     */
    public EasyImage(String imageFilePath) {
        this(new File(imageFilePath));
    }

    /**
     * Return image as java.awt.image.BufferedImage
     * @return image as java.awt.image.BufferedImage
     */
    public BufferedImage getAsBufferedImage(){
        return bufferedImage;
    }

    /**
     * Save the image as a new image file.
     * Can also convert the image according to file type.
     * @param fileName
     */
    public void saveAs(String fileName){
        saveImage(new File(fileName));
        this.fileName = fileName;
    }


    /**
     * Saves the image to the original file.
     */
    public void save(){
        saveImage(new File(fileName));
    }



    /**
     * Resizing the image by percentage of the original.
     * @param percentOfOriginal
     */
    public void resize( int percentOfOriginal){
        int newWidth = bufferedImage.getWidth()  * percentOfOriginal / 100;
        int newHeight = bufferedImage.getHeight() * percentOfOriginal / 100;
        resize(newWidth, newHeight);
    }

    /**
     * Resizing the image by width and height.
     * @param newWidth
     * @param newHeight
     */
    public void resize(int newWidth, int newHeight){

        int oldWidth = bufferedImage.getWidth();
        int oldHeight = bufferedImage.getHeight();

        if(newWidth == -1 || newHeight == -1){
            if(newWidth == -1){
                if(newHeight == -1){
                    return;
                }
                newWidth = newHeight * oldWidth/ oldHeight;
            }
            else {
                newHeight = newWidth * oldHeight / oldWidth;
            }
        }

        BufferedImage result =
                new BufferedImage(newWidth , newHeight, BufferedImage.TYPE_INT_BGR);

        double widthSkip =  new Double(oldWidth-newWidth) / new Double(newWidth);
        double heightSkip =  new Double(oldHeight-newHeight) / new Double(newHeight);

        double widthCounter = 0;
        double heightCounter = 0;

        int newY = 0;

        boolean isNewImageWidthSmaller = widthSkip >0;
        boolean isNewImageHeightSmaller = heightSkip >0;

        for (int y = 0; y < oldHeight && newY < newHeight; y++) {
            if(isNewImageHeightSmaller && heightCounter > 1){ //new image suppose to be smaller - skip row
                heightCounter -= 1;
            }
            else if (heightCounter < -1){ //new image suppose to be bigger - duplicate row
                heightCounter += 1;

                if (y > 1) {
                    y = y - 2;
                } else {
                    y = y - 1;
                }
            }
            else{

                heightCounter += heightSkip;

                int newX = 0;

                for (int x = 0; x < oldWidth && newX < newWidth; x++) {

                    if(isNewImageWidthSmaller && widthCounter > 1){ //new image suppose to be smaller - skip column
                        widthCounter -= 1;
                    }
                    else if (widthCounter < -1){ //new image suppose to be bigger - duplicate pixel
                        widthCounter += 1;

                        if (x > 1) {
                            x = x - 2;
                        } else {
                            x = x - 1;
                        }
                    }
                    else{
                        int rgb = bufferedImage.getRGB(x, y);
                        result.setRGB(newX, newY, rgb);
                        newX++;
                        widthCounter += widthSkip;
                    }
                }
                newY++;
            }
        }
        bufferedImage = result;
    }

    /**
     * Add color to the RGB of the pixel
     * @param numToAdd
     */
    public void addPixelColor(int numToAdd){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                int rgb = bufferedImage.getRGB(x, y);
                bufferedImage.setRGB(x, y, rgb+numToAdd);
            }
        }
    }

    /**
     * Covert image to black and white.
     */
    public void convertToBlackAndWhite() {
        ColorSpace graySpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp convertToGrayOp = new ColorConvertOp(graySpace, null);
        convertToGrayOp.filter(bufferedImage, bufferedImage);
    }


    /**
     * Rotates image 90 degrees to the left.
     */
    public void rotateLeft(){

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage result = new BufferedImage(height,
                width, BufferedImage.TYPE_INT_BGR);

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(y, x, rgb);
            }
        }

        bufferedImage = result;

    }

    /**
     * Rotates image 90 degrees to the right.
     */
    public void rotateRight(){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage result = new BufferedImage(height,
                width, BufferedImage.TYPE_INT_BGR);

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(height-y-1, x, rgb);
            }
        }

        bufferedImage = result;
    }


    /**
     * Rotates image 180 degrees.
     */
    public void rotate180(){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage result = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_BGR);

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(width-x-1, height-y-1, rgb);
            }
        }

        bufferedImage = result;

    }

    /**
     * Flips the image horizontally
     */
    public void flipHorizontally(){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage result = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_BGR);

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(width-x-1, y, rgb);
            }
        }

        bufferedImage = result;

    }

    /**
     * Flips the image vertically.
     */
    public void flipVertically(){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage result = new BufferedImage(width,
                height, BufferedImage.TYPE_INT_BGR);

        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y ++) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(x, height-y-1, rgb);
            }
        }

        bufferedImage = result;

    }

    /**
     * Multiply the image.
     * @param timesToMultiplyVertically
     * @param timesToMultiplyHorizantelly
     */
    public void multiply(int timesToMultiplyVertically,
                         int timesToMultiplyHorizantelly){
        multiply(timesToMultiplyVertically,timesToMultiplyHorizantelly,0);
    }

    /**
     * Multiply the image and also add color each of the multiplied images.
     * @param timesToMultiplyVertically
     * @param timesToMultiplyHorizantelly
     */
    public void multiply(int timesToMultiplyVertically,
                         int timesToMultiplyHorizantelly, int colorToHenhancePerPixel){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        BufferedImage result = new BufferedImage(width*timesToMultiplyVertically,
                height*timesToMultiplyHorizantelly, BufferedImage.TYPE_INT_BGR);

        for (int xx = 0; xx < timesToMultiplyVertically; xx ++) {
            for (int yy = 0; yy < timesToMultiplyHorizantelly; yy ++) {
                for (int x = 0; x < width; x ++) {
                    for (int y = 0; y < height; y ++) {
                        int rgb = bufferedImage.getRGB(x, y);
                        result.setRGB(width*xx+x, height*yy+y, rgb+colorToHenhancePerPixel*(yy+xx));

                    }
                }
            }
        }

        bufferedImage = result;
    }

    /**
     * Combines the image with another image in an equal presence to both;
     * @param newImagePath - image to combine with
     */
    public void combineWithPicture(String newImagePath){
        combineWithPicture(newImagePath, 2);
    }



    /**
     * Combines the image with another image.
     * jump = 2 means that every two pixels the new image is replaced.
     * This makes the 2 images equal in presence. If jump=3 than every 3rd
     * pixel is replaced by the new image.
     * As the jump is higher this is how much the new image has less presence.
     *
     * @param newImagePath
     * @param jump
     */
    public void combineWithPicture(String newImagePath, int jump){
        try {
            BufferedImage bufferedImage2 = ImageIO.read(new File(newImagePath));
            combineWithPicture(bufferedImage2, jump, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public void combineWithPicture(EasyImage image2){
        combineWithPicture(image2.getAsBufferedImage(), 2, null);
    }
    public void combineWithPicture(EasyImage image2, int jump){
        combineWithPicture(image2.getAsBufferedImage(), jump, null);
    }

    public void combineWithPicture(EasyImage image2, Color ignoreColor){
        combineWithPicture(image2.getAsBufferedImage(), 2, ignoreColor);
    }
    public void combineWithPicture(EasyImage image2, int jump, Color ignoreColor){
        combineWithPicture(image2.getAsBufferedImage(), jump, ignoreColor);
    }

    /**
     * Combines the image with another image.
     * jump = 2 means that every two pixels the new image is replaced.
     * This makes the 2 images equal in presence. If jump=3 than every 3rd
     * pixel is replaced by the new image.
     * As the jump is higher this is how much the new image has less presence.
     *
     * ignoreColor is a color in the new image that will not be copied -
     * this is good where there is a background which you do not want to copy.
     *
     * @param bufferedImage2
     * @param jump
     * @param ignoreColor
     */
    private void combineWithPicture(BufferedImage bufferedImage2,
                                    int jump, Color ignoreColor){
        checkJump(jump);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        int width2 = bufferedImage2.getWidth();
        int height2 = bufferedImage2.getHeight();

        int ignoreColorRgb = -1;

        if(ignoreColor != null){
            ignoreColorRgb = ignoreColor.getRGB();
        }

        for (int y = 0; y < height; y ++) {
            for (int x = y%jump; x < width; x +=jump) {
                if(x >= width2 || y>= height2){
                    continue;
                }

                int rgb = bufferedImage2.getRGB(x, y);

                if( rgb != ignoreColorRgb ){
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }

    }


    public void crop(int startX, int startY, int endX, int endY){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if(startX == -1){
            startX = 0;
        }

        if(startY == -1){
            startY = 0;
        }

        if(endX == -1){
            endX = width-1;
        }

        if(endY == -1){
            endY = height-1;
        }

        BufferedImage result = new BufferedImage(endX-startX+1,
                endY-startY+1, BufferedImage.TYPE_INT_BGR);

        for (int y = startY; y < endY; y ++) {
            for (int x = startX; x < endX; x ++) {
                int rgb = bufferedImage.getRGB(x, y);
                result.setRGB(x-startX, y-startY, rgb);
            }
        }
        bufferedImage = result;
    }

    private void saveImage(File file) {
        try {
            ImageIO.write(bufferedImage, getFileType(file), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void emphasize(int startX, int startY, int endX, int endY){
        emphasize(startX, startY, endX, endY, Color.BLACK, 3 );
    }

    public void emphasize(int startX, int startY, int endX, int endY, Color backgroundColor){
        emphasize(startX, startY, endX, endY, backgroundColor, 3 );
    }

    public void emphasize(int startX, int startY, int endX, int endY,int jump){
        emphasize(startX, startY, endX, endY, Color.BLACK, jump );
    }
    public void emphasize(int startX, int startY, int endX, int endY, Color backgroundColor,int jump){

        checkJump(jump);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if(startX == -1){
            startX = 0;
        }

        if(startY == -1){
            startY = 0;
        }

        if(endX == -1){
            endX = width-1;
        }

        if(endY == -1){
            endY = height-1;
        }


        for (int y = 0; y < height; y ++) {
            for (int x = y%jump; x < width; x +=jump) {

                if(y >= startY && y <= endY && x >= startX && x <= endX){
                    continue;
                }

                bufferedImage.setRGB(x, y, backgroundColor.getRGB());
            }
        }

    }

    private void checkJump(int jump) {
        if(jump<1){
            throw new RuntimeException("Error: jump can not be less than 1");
        }

    }

    public void addColorToImage(Color color, int jump){
        addColorToImage(color.getRGB(),jump);
    }

    public void addColorToImage(int rgb, int jump){
        checkJump(jump);

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        for (int y = 0; y < height; y ++) {
            for (int x = y%jump; x < width; x +=jump) {
                bufferedImage.setRGB(x, y, rgb);
            }
        }
    }



    public void affineTransform (double fShxFactor, double fShyFactor) {

        try {
            AffineTransform shearer =
                    AffineTransform.getShearInstance (fShxFactor, fShyFactor);
            AffineTransformOp shearOp =
                    new AffineTransformOp (shearer, null);
            bufferedImage = shearOp.filter (bufferedImage, null);
        }
        catch (Exception e) {
            System.out.println("Shearing exception = " + e);
        }
    }

    private String getFileType(File file) {
        String fileName = file.getName();
        int idx =  fileName.lastIndexOf(".");
        if(idx == -1){
            throw new RuntimeException("Invalid file name");
        }

        return fileName.substring(idx+1);
    }


    public int getWidth(){
        return bufferedImage.getWidth();
    }

    public int getHeight(){
        return bufferedImage.getHeight();
    }




    public static void main(String[] args) {
        /*
        Image image  = new Image("c:/pics/p1.jpg");
        image.resize(10);
        image.multiply(5, 5, 11111);
        image.saveAs("c:/pics/multiply+color.jpg");
        */
        /*
        Image image  = new Image("c:/pics/israel_flag.gif");
        Image image2  = new Image("c:/pics/palestine_flag.gif");
        //image.resize(50);
        //image2.resize(50);
        //image.affineTransform(0, 0.3);
        //image2.affineTransform(0, -0.3);
        image.combineWithPicture(image2);
        image.saveAs("c:/pics/affineTransformAndCombine2.jpg");
       */
       /*
        Image image  = new Image("c:/pics/p1.jpg");
        image.resize(50);
        image.affineTransform(0.0, 0.5);
        image.saveAs("c:/pics/affineTransform.jpg");
        */
     /*
        Image image  = new Image("c:/pics/heart.gif");
        image.multiply(20, 20);
        Image image2  = new Image("c:/pics/p6.jpg");
        image2.crop(800, 0, -1, -1);
        image2.resize(50);
        image2.combineWithPicture(image,3,Color.white);
        image2.saveAs("c:/pics/combineWithPictureWithoutBackground.jpg");
        /*
        image.resize(5);
        image.multiply(20, 20);
        image.combineWithPicture("c:/p2.jpg");
        //image.addColorToImage(Color.yellow, 3);
        //image.addColorToImage(Color.red, 5);
        //image.combineWithPicture("c:/p2.jpg",3);

      */

        EasyImage image  = new EasyImage("c:/pics/p1.jpg");
        int width = image.getWidth();
        int height = image.getHeight();
        for(int i=0,c=0;i<height;c++,i+=50){
            int x = width/2  - i;
            int y = height/2 - i;

            image.emphasize(x, y, width-1-x, height-1-y, Color.BLACK, 12 - c/4);
        }
        image.saveAs("c:/pics/emphesizeTrick.jpg");
        //  */
        // image.saveAs("c:/xxx.jpg");
        /*
        Image image  = new Image("c:/pics/p1.jpg");
        image.addColorToImage(Color.red, 5);
        image.saveAs("c:/pics/addColorToImage.jpg");
        */
    }
}