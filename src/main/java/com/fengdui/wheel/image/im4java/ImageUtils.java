package com.fengdui.wheel.image.im4java;

import org.im4java.core.GMOperation;
import org.im4java.core.GraphicsMagickCmd;
import org.im4java.core.IM4JavaException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageUtils
{
    private static GraphicsMagickCmd cmd = new GraphicsMagickCmd("convert");

    public static void compress(String sourcePath, String targetPath) throws IOException, InterruptedException, IM4JavaException
    {
        compress(sourcePath, targetPath, "", "");
    }

    public static void compress(String sourcePath, String targetPath, String imgWidth, String imgHeight) throws IOException, InterruptedException, IM4JavaException
    {
        GMOperation op = new GMOperation();
        op.addImage();

        boolean noWidth = "".equals(imgWidth);
        boolean noHeight = "".equals(imgHeight);
        if (!noWidth && !noHeight)
        {
            op.addRawArgs("-resize", imgWidth + "x" + imgHeight);
        }
        else if (!noWidth && noHeight)
        {
            op.addRawArgs("-resize", imgWidth);
        }
        else if (noWidth && !noHeight)
        {
            op.addRawArgs("-resize", "x" + imgHeight);
        }
        op.addRawArgs("+profile", "\"*\"");
        op.addRawArgs("-quality", "85");
        op.addImage();

        cmd.run(op, sourcePath, targetPath);
    }

    public static void crop(String sourcePath, String targetPath, String crop_args) throws IOException, InterruptedException, IM4JavaException
    {
        GMOperation op = new GMOperation();
        op.addImage();
        op.addRawArgs("-crop", crop_args);
        op.addRawArgs("-quality", "90");
        op.addImage();
        cmd.run(op, sourcePath, targetPath);
    }

    public static BufferedImage compress(String imagePath, int maxLength) throws IOException
    {
        File file = new File(imagePath);
        if (!file.exists())
        {
            return null;
        }

        return compress(ImageIO.read(new FileInputStream(file)), maxLength);
    }

    public static BufferedImage compress(BufferedImage src, int maxLength) throws IOException
    {
        if (null != src)
        {
            int old_w = src.getWidth();
            // 得到源图宽  
            int old_h = src.getHeight();
            // 得到源图长  
            int new_w = 0;
            // 新图的宽  
            int new_h = 0;
            // 新图的长  
            // 根据图片尺寸压缩比得到新图的尺寸  
            if (old_w < maxLength && old_h < maxLength)
            {
                new_w = old_w;
                new_h = old_h;
            }
            else if (old_w > old_h)
            {
                // 图片要缩放的比例  
                new_w = maxLength;
                new_h = Math.round(old_h * ((float) maxLength / old_w));
            }
            else
            {
                new_w = Math.round(old_w * ((float) maxLength / old_h));
                new_h = maxLength;
            }
            return getNewImage(src, new_w, new_h);
        }
        return null;
    }

    private static BufferedImage getNewImage(BufferedImage src, int width, int height) throws IOException
    {
        // 得到图片  
        int old_w = src.getWidth();
        // 得到源图宽  
        int old_h = src.getHeight();
        // 得到源图长  
        BufferedImage newImg = null;

        newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = newImg.createGraphics();
        // 从原图上取颜色绘制新图  
        g.drawImage(src, 0, 0, old_w, old_h, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸  
        newImg.getGraphics().drawImage(
                src.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0,
                null);
        return newImg;
        //
    }


}
