
package com.fengdui.tool.image.thumbnailator;

import com.fengdui.tool.image.ImageMarkLogoByIcon;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ImageUtil {
	/**
	 * 检查图片是否符合标准
	 * @param limitSize 限制大小,单位K
	 * @param limitWidth 限制宽度
	 * @param limitHeight 限制高度
	 * @return
	 * @throws Exception
	 */
	public static void checkImage(File pic, Integer limitSize,
			Integer limitWidth, Integer limitHeight) throws Exception {
		if (null != pic && pic.exists()) {
			if (null != limitSize) {
				if (pic.length() > limitSize * 1024) {
					throw new Exception("图片最大允许" + limitSize + "K");
				}
			}
			if (null != limitWidth) {
				int imgWidth = getImageWidth(pic);
				if (imgWidth > limitWidth) {
					throw new Exception("图片最大宽度：" + limitWidth + "px");
				}
			}
			if (null != limitHeight) {
				int imgHeight = getImageHeight(pic);
				if (imgHeight > limitHeight) {
					throw new Exception("图片最大高度：" + limitHeight + "px");
				}
			}
		} else {
			throw new Exception("图片不能为空");
		}
	}
	
	public static File handleImage(File srcImg, Integer limitSize,
			Integer limitWidth, Integer limitHeight) throws Exception {
		String dirPath = File.separator + "upload_pic_temp";
		File dirFile = new File(dirPath);
		if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
			dirFile.mkdirs();
		}
		File newPic = null;
		if (null != srcImg) {
			String fileName = UUID.randomUUID().toString();
			String targetPath = dirPath + File.separator + fileName + ".jpg";
			if(null != limitWidth || null != limitHeight){
				newPic = new File(targetPath);
				resizeImage(srcImg, newPic, limitWidth, limitHeight, "jpg");
			}
			if(null != newPic){
				srcImg = newPic;
			}
			if (null != limitSize) {
				if (srcImg.length() > limitSize * 1024) {
					ImageMarkLogoByIcon.markImageByIcon(ImageMarkLogoByIcon.ICON_PATH, srcImg.getAbsolutePath(), targetPath);
					
					newPic = new File(targetPath);
					if(newPic.length() > limitSize * 1024){
						int time = 0;
						float quality = 1.0f;
						String targetPathBak = dirPath + File.separator + fileName + " - bak.jpg";
						FileUtils.copyFile(newPic, new File(targetPathBak));
						while(new File(targetPathBak).length() > limitSize * 1024 && time < 10){
							resizeImage(newPic, new File(targetPathBak), quality);
							quality = quality - 0.1f;
							time++;
						}
						newPic.delete();
						return new File(targetPathBak);
					} else {
						return new File(targetPath);
					}
				}
			}
		} else {
			throw new Exception("图片不能为空");
		}
		return srcImg;
	}
	
	public static File handleImage(File srcImg, String dpi, int imgWidth, int imgHeight, Integer limitWidth, Integer limitHeight, Integer limitSize) throws Exception {
		String dirPath = File.separator + "upload_pic_temp";
		File dirFile = new File(dirPath);
		if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
			dirFile.mkdirs();
		}
		File newImg = null;
		if (null != srcImg) {
			String fileName = UUID.randomUUID().toString();
			String targetPath = dirPath + File.separator + fileName + "_" + dpi + ".jpg";
			if(null != limitWidth || null != limitHeight){
				newImg = new File(targetPath);
				if(limitWidth.intValue() >= imgWidth && limitHeight.intValue() >= imgHeight){
					FileUtils.copyFile(srcImg, newImg);
				} else {
					double scaleW = (double)limitWidth / (double)imgWidth;
					double scaleH = (double)limitHeight / (double)imgHeight;
					double scale = scaleW > scaleH ? scaleH : scaleW;
					limitWidth = (int) (imgWidth * scale);
					limitHeight = (int) (imgHeight * scale);
					resizeImage(srcImg, newImg, limitWidth, limitHeight, "jpg");
				}
			}
			if (null != limitSize) {
				if(null == newImg){
					newImg = srcImg;
				}
				if (newImg.length() > limitSize * 1024) {
					ImageMarkLogoByIcon.markImageByIcon(ImageMarkLogoByIcon.ICON_PATH, newImg.getAbsolutePath(), targetPath);
					newImg = new File(targetPath);
					if(newImg.length() > limitSize * 1024){
						int time = 0;
						float quality = 1.0f;
						String targetPathBak = dirPath + File.separator + fileName + " - bak.jpg";
						FileUtils.copyFile(newImg, new File(targetPathBak));
						while(new File(targetPathBak).length() > limitSize * 1024 && time < 10){
							resizeImage(newImg, new File(targetPathBak), quality);
							quality = quality - 0.1f;
							time++;
						}
						newImg.delete();
						return new File(targetPathBak);
					}else {
						return newImg;
					}
				}
			}
		} else {
			throw new Exception("图片不能为空");
		}
		return newImg;
	}
	
	public static File handleImageByLib(File srcImg, String suffixes, String dpi, int imgWidth, int imgHeight, Integer limitWidth, Integer limitHeight, Integer limitSize) throws Exception {
		String dirPath = File.separator + "upload_pic_temp";
		File dirFile = new File(dirPath);
		if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
			dirFile.mkdirs();
		}
		File newImg = null;
		if (null != srcImg) {
			String fileName = UUID.randomUUID().toString();
			String targetPath = dirPath + File.separator + fileName + "_" + dpi + "." + suffixes;
			newImg = new File(targetPath);
			if(null != limitWidth || null != limitHeight){
				if(limitWidth.intValue() >= imgWidth && limitHeight.intValue() >= imgHeight){
					FileUtils.copyFile(srcImg, newImg);
				} else {
					resizeImageByLib(srcImg, targetPath, limitWidth, limitHeight, suffixes, 1, false);
				}
			}else {
				FileUtils.copyFile(srcImg, newImg);
			}
			if (null != limitSize) {
//				if(null == newImg){
//					newImg = srcImg;
//				}
				if (newImg.length() > limitSize * 1024) {
					newImg = new File(targetPath);
					if(newImg.length() > limitSize * 1024){
						int time = 0;
						float quality = 1.0f;
						String targetPathBak = dirPath + File.separator + fileName + " - bak." + suffixes;
						FileUtils.copyFile(newImg, new File(targetPathBak));
						while(new File(targetPathBak).length() > limitSize * 1024 && time < 10){
							resizeImage(newImg, new File(targetPathBak), quality);
							quality = quality - 0.1f;
							time++;
						}
						newImg.delete();
						return new File(targetPathBak);
					}else {
						return newImg;
					}
				}
			}
		} else {
			throw new Exception("图片不能为空");
		}
		return newImg;
	}
	
	public static void resizeImage(File srcFile, File tarFile, Integer newWidth, Integer newHeight, String formatName) throws Exception{
		if(!srcFile.exists()){
			throw new Exception("srcFile is not exist");
		}
		BufferedImage oldImage = ImageIO.read(srcFile);
		// 判断图片格式是否正确
		if(null == oldImage || -1 == oldImage.getWidth(null)){
			throw new Exception("文件格式有误");
		}
		if(null == newWidth){
			newWidth = oldImage.getWidth();
		}
		if(null == newHeight){
			newHeight = oldImage.getHeight();
		}
		BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		//Image image = oldImage.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
		/**
		 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
		 */
		Image image = oldImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		Graphics graphics = newImage.getGraphics();
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		ImageIO.write(newImage, formatName, tarFile);
	}
	
	public static void resizeImageByLib(File srcFile, String tarFile,
			Integer newWidth, Integer newHeight, String formatName,
			float quality, boolean isForceSize) throws Exception {
		if (!srcFile.exists()) {
			throw new Exception("srcFile is not exist");
		}
		if (isForceSize) {
			Thumbnails.of(srcFile).forceSize(newWidth, newHeight)
					.outputFormat(formatName).outputQuality(quality)
					.toFile(tarFile);
		} else {
			Thumbnails.of(srcFile).size(newWidth, newHeight)
					.outputFormat(formatName).outputQuality(quality)
					.toFile(tarFile);
		}
	}
	
	public static void resizeImageByLib(File srcFile, String tarFile,
			double scaleWidth, double scaleHeight, String formatName,
			float quality) throws Exception {
		if (!srcFile.exists()) {
			throw new Exception("srcFile is not exist");
		}
		Thumbnails.of(srcFile).scale(scaleWidth, scaleHeight)
				.outputFormat(formatName).outputQuality(quality)
				.toFile(tarFile);
	}
	
	public static void resizeImage(File srcFile, File tarFile, float quality) throws Exception {
		if(!srcFile.exists()){
			throw new Exception("srcFile is not exist");
		}
		BufferedImage oldImage = ImageIO.read(srcFile);
		// 判断图片格式是否正确
		if(null == oldImage || -1 == oldImage.getWidth(null)){
			throw new Exception("文件格式有误");
		}
		ImageWriter imgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
		ImageWriteParam imgWriteParam = new JPEGImageWriteParam(null);
		// 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
		imgWriteParam.setCompressionMode(imgWriteParam.MODE_EXPLICIT);
		// 这里指定压缩的程度，参数qality是取值0~1范围内
		imgWriteParam.setCompressionQuality(quality);
		imgWriteParam.setProgressiveMode(imgWriteParam.MODE_DISABLED);
		ColorModel colorModel = oldImage.getColorModel();
		// 指定压缩时使用的色彩模式
		imgWriteParam.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
		FileOutputStream out = new FileOutputStream(tarFile);
		imgWriter.reset();
		imgWriter.setOutput(ImageIO.createImageOutputStream(out));
		imgWriter.write(null, new IIOImage(oldImage, null, null), imgWriteParam);
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) {
		try {
			resizeImage(new File("D:\\test\\test3.png"), new File("D:\\test\\resize3.png"), 480, 800, "png");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static int getImageWidth(File file) throws Exception {
		int width = -1;
		try {
			width = ImageIO.read(file).getWidth();
		} catch (IOException e) {
			throw new Exception("getImageWidth error.",e);
		}
		return width;
	}
	
	public static int getImageHeight(File file) throws Exception {
		int height = -1;
		try {
			height = ImageIO.read(file).getHeight();
		} catch (IOException e) {
			throw new Exception("getImageHeight error.",e);
		}
		return height;
	}
	
	public static Map<String, Integer> getImageDimensions(File file) throws Exception {
		int width = -1;
		int height = -1;
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			width = bufferedImage.getWidth();
			height = bufferedImage.getHeight();
			map.put("width", width);
			map.put("height", height);
		} catch (Exception e) {
			throw new Exception("getImageDimensions error.",e);
		}
		return map;
	}
	
	public static Integer[] getImageDimensionsArray(File file) throws Exception {
		Integer[] array = { null, null };
		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			array[0] = bufferedImage.getWidth();
			array[1] = bufferedImage.getHeight();
		} catch (Exception e) {
			throw new Exception("getImageDimensions error.",e);
		}
		return array;
	}
	
	public static int getImageWidth(String imgUrl) throws Exception{
		int width = -1;
		HttpURLConnection connection = null;
		URL url = null;
		try {
			url = new URL(imgUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			width = ImageIO.read(connection.getInputStream()).getWidth();
		} catch (IOException e) {
			throw new Exception("getImageWidth error.", e);
		} finally {
			try {
				if (null != connection) {
					connection.disconnect();
				}
			} catch (Exception e) {
				throw new Exception("close connect error", e);
			}
		}
		return width;
	}
	
	public static int getImageHeight(String imgUrl) throws Exception{
		int height = -1;
		HttpURLConnection connection = null;
		URL url = null;
		try {
			url = new URL(imgUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			height = ImageIO.read(connection.getInputStream()).getHeight();
		} catch (IOException e) {
			throw new Exception("getImageHeight error.", e);
		} finally {
			try {
				if (null != connection) {
					connection.disconnect();
				}
			} catch (Exception e) {
				throw new Exception("close connect error", e);
			}
		}
		return height;
	}
	
	/** 获得图片尺寸 */
	public static Map<String, Integer> getImageDimensions(String imgUrl) throws Exception {
		int height = 0;
		int width = 0;
		HttpURLConnection connection = null;
		URL url = null;
		Map<String, Integer> map = new HashMap<String, Integer>();
		try {
			if (null != imgUrl) {
				url = new URL(imgUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				BufferedImage bufferedImage = ImageIO.read(connection.getInputStream());
				width = bufferedImage.getWidth();
				height = bufferedImage.getHeight();
			}
			map.put("width", width);
			map.put("height", height);
		} catch (IOException e) {
			throw new Exception("getImageHeight error.", e);
		} finally {
			try {
				if (null != connection) {
					connection.disconnect();
				}
			} catch (Exception e) {
				throw new Exception("close connect error", e);
			}
		}
		return map;
	}

	public static final String KEY_WIDTH = "width";
	public static final String KEY_HEIGHT = "height";

	public static int getImageWidthV2(File file) throws Exception {
		String fileName = file.getName();
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
		Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(fileExtName);
		ImageReader reader = readers.next();
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
		reader.setInput(imageInputStream, true);
		return reader.getWidth(0);
	}

	public static int getImageHeightV2(File file) throws Exception {
		String fileName = file.getName();
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
		Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(fileExtName);
		ImageReader reader = readers.next();
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
		reader.setInput(imageInputStream, true);
		return reader.getHeight(0);
	}

	public static Map<String, Integer> getImageDimensionsV2(File file) throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		String fileName = file.getName();
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
		Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix(fileExtName);
		ImageReader reader = readers.next();
		ImageInputStream imageInputStream = ImageIO.createImageInputStream(file);
		reader.setInput(imageInputStream, true);
		map.put(KEY_WIDTH, reader.getWidth(0));
		map.put(KEY_HEIGHT, reader.getHeight(0));
		return map;
	}

	public static void resizeImageByLib(File fileSrc, String fileTar, double scale) throws Exception {
		Thumbnails.of(fileSrc).scale(scale).toFile(fileTar);
	}
}