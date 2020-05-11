package com.fengdui.wheel.qrcode;

import com.swetake.util.Qrcode;
import lombok.extern.log4j.Log4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

@Log4j
public class QrcodeGenerator
{

    private final static int imageWidth = 190;
    private final static int imageHeight = 190;
    private static String filePath = "";
    private final static String IMG_SUFFIX = "png";
    private final static Qrcode qrcode = new Qrcode();
    private final static Qrcode qrcode10 = new Qrcode();
    private final static Qrcode qrcode11 = new Qrcode();
    private final static Qrcode qrcode12 = new Qrcode();
    private final static Qrcode qrcode13 = new Qrcode();
    private static BufferedImage defaultBI = new BufferedImage(290, 290, BufferedImage.TYPE_INT_RGB);
    private static BufferedImage bi;
    private static BufferedImage logoImg;
    private static OssService ossService = null;

    static
    {
        try
        {
            //加载二维码中间logo
            //String logoMinPath = QrcodeGenerator.class.getResource("logo-min.png").getPath();
            URL logoRes = QrcodeGenerator.class.getClassLoader().getResource("logo-min.png");
            if(null != logoRes)
            {
                String logoMinPath = logoRes.getPath();
                File logoFile = new File(logoMinPath);
                if(logoFile.exists())
                {
                    logoImg = ImageIO.read(logoFile);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        qrcode.setQrcodeErrorCorrect('M');
        qrcode.setQrcodeEncodeMode('B');
        qrcode.setQrcodeVersion(5);

        qrcode10.setQrcodeErrorCorrect('M');
        qrcode10.setQrcodeEncodeMode('B');
        qrcode10.setQrcodeVersion(10);

        qrcode11.setQrcodeErrorCorrect('M');
        qrcode11.setQrcodeEncodeMode('B');
        qrcode11.setQrcodeVersion(11);

        qrcode12.setQrcodeErrorCorrect('M');
        qrcode12.setQrcodeEncodeMode('B');
        qrcode12.setQrcodeVersion(12);

        qrcode13.setQrcodeErrorCorrect('M');
        qrcode13.setQrcodeEncodeMode('B');
        qrcode13.setQrcodeVersion(13);
        bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
    }

    public static String getQrcodeImageUrl(String imageName)
    {
        if (imageName == null || imageName == "")
        {
            return "";
        }
        return "/img/qrcode/" + imageName;
    }

    public static String getQrcodeImageUrlWithSuffix(String imageName)
    {
        if (imageName == null || imageName == "")
        {
            return "";
        }
        return ConstantValue.OSS_IMG_QRCODE_PATH + imageName + "." + IMG_SUFFIX;
    }

    public static String generateWithLogo(String message, String imageName)
    {
        return generate(message, imageName, logoImg);
    }

    /**
     * 生成二维码图片
     *
     * @param message
     * @param imageName 不带后缀
     * @param logo      二维码中间小图，可以为null
     * @return 二维码图片名
     */
    public static String generate(String message, String imageName, BufferedImage logo)
    {
        //--------------------------------------------------mod by guoyd @ 2015/8/25
        // 图片文件上传至oss
//        if ("".equals(filePath))
//        {
//            synchronized (filePath)
//            {
//                filePath = SpringContextHelper.getBean(GlobalSetting.class).getImg_upload_dir() + "/qrcode";
//                File dir = new File(filePath);
//                if (!dir.exists())
//                {
//                    dir.mkdirs();
//                }
//            }
//        }

        String fileName = ConstantValue.OSS_IMG_QRCODE_PATH + imageName + "." + IMG_SUFFIX;
//        File f = new File(filePath + "/" + fileName);
        //--------------------------------------------------del

        try
        {
            int level = 10;
            int imgSize = 290;
            int blankBegin = 105;
            int blankEnd = 185;
            int imgBegin = 115;
            int px_unit = 5;//单位5像素
            BufferedImage currentBI = defaultBI;
            Qrcode currentQrcode = qrcode10;
            byte[] mBytes = message.getBytes("utf-8");
            int msgSize = mBytes.length;
            if (msgSize >= 213 && msgSize < 251)
            {
                level = 11;
                imgSize = getImgSize(level);
                blankBegin = getBlankBegin(level);
                blankEnd = getBlankEnd(level);
                imgBegin = getImgBegin(level);
                currentBI = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
                currentQrcode = qrcode11;
            }
            else if (msgSize >= 251 && msgSize < 287)
            {
                level = 12;
                imgSize = getImgSize(level);
                blankBegin = getBlankBegin(level);
                blankEnd = getBlankEnd(level);
                imgBegin = getImgBegin(level);
                currentBI = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
                currentQrcode = qrcode12;
            }
            else if (msgSize >= 287 && msgSize < 334)
            {
                level = 13;
                imgSize = getImgSize(level);
                blankBegin = getBlankBegin(level);
                blankEnd = getBlankEnd(level);
                imgBegin = getImgBegin(level);
                currentBI = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
                currentQrcode = qrcode13;
            }
            else
            {
                level = 5;
                imgSize = 190*2-5;
                blankBegin = 60*2+10;
                blankEnd = 120*2;
                imgBegin = 70*2;
                currentBI = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
                currentQrcode = qrcode;
                px_unit = 10;
            }
            // createGraphics
            Graphics2D g = currentBI.createGraphics();
            // set background
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, imgSize, imgSize);
            g.setColor(Color.BLACK);
            if (mBytes.length > 0 && mBytes.length < 500)
            {
                boolean[][] b = currentQrcode.calQrcode(mBytes);
                for (int i = 0; i < b.length; i++)
                {
                    for (int j = 0; j < b.length; j++)
                    {
                        if (b[j][i] && (logo == null || !(px_unit * j > blankBegin && px_unit * j < blankEnd && px_unit * i > blankBegin && px_unit * i < blankEnd)))
                        {
                            g.fillRect(j * px_unit + 2, i * px_unit + 2, px_unit, px_unit);
                        }

                    }
                }
            }
            if (logo != null)
            {
                g.drawImage(logo, imgBegin, imgBegin, null);
            }
            g.dispose();
            currentBI.flush();

//            ImageIO.write(currentBI, IMG_SUFFIX, f);//----del @2015/8/25 文件保存到oss
            int tryCount = 3;
            boolean tryResult = false;
            while (!tryResult && tryCount > 0) {//-----mod by guoyd @2016/1/26 失败重试
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                ImageIO.write(currentBI, IMG_SUFFIX, ostream);
                InputStream istream = new ByteArrayInputStream(ostream.toByteArray());
                initOss();
                tryResult = ossService.putFile(fileName, istream);
                tryCount--;
            }
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("qrcode generate UnsupportedEncodingException:" + message);
        }
        catch (IOException e)
        {
            log.error("qrcode generate IOException:" + message);
        }
        return fileName;
    }

    private static int getImgSize(int level)
    {
        return 290 + (level - 10) * 20;
    }

    private static int getBlankBegin(int level)
    {
        return 105 + (level - 10) * 10;
    }

    private static int getBlankEnd(int level)
    {
        return 185 + (level - 10) * 10;
    }

    private static int getImgBegin(int level)
    {
        return 115 + (level - 10) * 10;
    }

    private static void initOss() {
        if (ossService == null) {
            ossService = SpringContextHelper.getBean(OssService.class);
        }
    }
    /**
     * 二维码是否已生成
     *
     * @param qrcodeName 不带后缀
     * @return
     */
    public static boolean qrcodeExist(String qrcodeName)
    {
        if ("".equals(filePath))
        {
            return false;
        }

        String fileName = qrcodeName + "." + IMG_SUFFIX;
        File f = new File(filePath + "/" + fileName);
        return f.exists();
    }

    public static void main(String[] args) throws IOException
    {
        String msg = "http://192.168.12.220:8081/q3/q/1111";

        System.out.println(msg.getBytes("utf-8").length);
        generate(msg, "t111", logoImg);
        System.out.println(msg.getBytes("utf-8").length);
    }

}
