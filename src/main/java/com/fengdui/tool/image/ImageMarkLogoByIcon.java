package com.fengdui.tool.image;

import org.springframework.web.context.ContextLoader;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;


/**
 * 图片水印 http://sjsky.iteye.com
 */
public class ImageMarkLogoByIcon {

	public static final String ICON_PATH = ContextLoader.getCurrentWebApplicationContext()
			.getResource(File.separator + "images")
			+ File.separator + "icons" + File.separator + "logo.png";

	public static void main(String[] args) {
		String srcImgPath = "d:/1.jpg";
		String iconPath = "d:/logo.png";
		String targerPath = "d:/img_mark_icon.jpg";
		// String targerPath2 = "d:/img_mark_icon_rotate.jpg";
		// ��ͼƬ���ˮ�?
		ImageMarkLogoByIcon.markImageByIcon(iconPath, srcImgPath, targerPath);
		// ��ͼƬ���ˮ�?ˮӡ��ת-45
		// ImageMarkLogoByIcon.markImageByIcon(iconPath, srcImgPath,
		// targerPath2,
		// -45);

	}

	/**
	 * 给图片添加水印
	 * 
	 * @param iconPath
	 *            水印图片路径
	 * @param srcImgPath
	 *            源图片路径
	 * @param targerPath
	 *            目标图片路径
	 */
	public static void markImageByIcon(String iconPath, String srcImgPath,
			String targerPath) {
		markImageByIcon(iconPath, srcImgPath, targerPath, 1, null);
	}

	/**
     * 给图片添加水印、可设置水印图片旋转角度
     * 
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     * @param widthPercent
     *            水印图片x坐标位于原图片宽度的百分比0~1
     * @param heightPercent
     *            水印图片y坐标位于原图片高度的百分比0~1
     * @param zoomPercent
     *            水印图片的缩放比例0~1
     */
	public static void markImageByIconByPercent(String iconPath,
			String srcImgPath, String targerPath, double widthPercent,
			double heightPercent, double zoomPercent, Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new File(srcImgPath));
			int srcImgWidth = srcImg.getWidth(null);
			int srcImgHeight = srcImg.getHeight(null);

			BufferedImage buffImg = new BufferedImage(srcImgWidth,
					srcImgHeight, BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			// Graphics g= buffImg.getGraphics();
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(
					srcImg.getScaledInstance(srcImg.getWidth(null),
							srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
					null);

			if (null != degree) {
				// 设置水印旋转 ת
				g.rotate(Math.toRadians(degree),
						(double) buffImg.getWidth() / 2,
						(double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
		    // ImageIcon imgIcon = new ImageIcon(iconPath);

		    // 得到Image对象。
		    // Image img = imgIcon.getImage();
			Image iconImg = ImageIO.read(new File(iconPath));
			int iconImgWidth = iconImg.getWidth(null);
			int iconImgHeight = iconImg.getHeight(null);

			float alpha = 1f; // 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));

			// 表示水印图片的位置
			// g.drawImage(img, 80, 430, null);
			g.drawImage(iconImg, (int) (srcImgWidth * widthPercent),
					(int) (srcImgHeight * heightPercent),
					(int) (iconImgWidth * zoomPercent),
					(int) (iconImgHeight * zoomPercent), null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// 生成图片
			ImageIO.write(buffImg, "JPG", os);

			System.out.println("图片完成添加Icon印章。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
     * 给图片添加水印、可设置水印图片旋转角度
     * 
     * @param iconPath
     *            水印图片路径
     * @param srcImgByte
     *            源图片byte数组
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     * @param widthPercent
     *            水印图片x坐标位于原图片宽度的百分比0~1
     * @param heightPercent
     *            水印图片y坐标位于原图片高度的百分比0~1
     * @param zoomPercent
     *            水印图片的缩放比例0~1
     */
	public static void markImageByIconByByte(String iconPath,
			byte[] srcImgByte, String targerPath, double widthPercent,
			double heightPercent, double zoomPercent, Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new ByteArrayInputStream(srcImgByte));
			int srcImgWidth = srcImg.getWidth(null);
			int srcImgHeight = srcImg.getHeight(null);

			BufferedImage buffImg = new BufferedImage(srcImgWidth,
					srcImgHeight, BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			// Graphics g= buffImg.getGraphics();
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(
					srcImg.getScaledInstance(srcImg.getWidth(null),
							srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
					null);

			if (null != degree) {
				// 设置水印旋转 ת
				g.rotate(Math.toRadians(degree),
						(double) buffImg.getWidth() / 2,
						(double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
		    // ImageIcon imgIcon = new ImageIcon(iconPath);

		    // 得到Image对象。
		    // Image img = imgIcon.getImage();
			// Image img = imgIcon.getImage();
			Image iconImg = ImageIO.read(new File(iconPath));
			int iconImgWidth = iconImg.getWidth(null);
			int iconImgHeight = iconImg.getHeight(null);

			float alpha = 1f; // 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));

			// 表示水印图片的位置
			// g.drawImage(img, 80, 430, null);
			g.drawImage(iconImg, (int) (srcImgWidth * widthPercent),
					(int) (srcImgHeight * heightPercent),
					(int) (iconImgWidth * zoomPercent),
					(int) (iconImgHeight * zoomPercent), null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// 生成图片
			ImageIO.write(buffImg, "JPG", os);

			System.out.println("图片完成添加Icon印章。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
     * 给图片添加水印、可设置水印图片旋转角度
     * 
     * @param iconPath
     *            水印图片路径
     * @param srcImgByte
     *            源图片byte数组
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     * @param zoomPercent
     *            水印图片的缩放比例0~1
     */
	public static void markImageByIconByByte(String iconPath,
			byte[] srcImgByte, String targerPath, double zoomPercent,
			Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new ByteArrayInputStream(srcImgByte));
			int srcImgWidth = srcImg.getWidth(null);
			int srcImgHeight = srcImg.getHeight(null);

			BufferedImage buffImg = new BufferedImage(srcImgWidth,
					srcImgHeight, BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			// Graphics g= buffImg.getGraphics();
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(
					srcImg.getScaledInstance(srcImg.getWidth(null),
							srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
					null);

			if (null != degree) {
				// 设置水印旋转 ת
				g.rotate(Math.toRadians(degree),
						(double) buffImg.getWidth() / 2,
						(double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
		    // ImageIcon imgIcon = new ImageIcon(iconPath);

		    // 得到Image对象。
		    // Image img = imgIcon.getImage();
			Image iconImg = ImageIO.read(new File(iconPath));
			int iconImgWidth = iconImg.getWidth(null);
			int iconImgHeight = iconImg.getHeight(null);

			float alpha = 1f; // 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));

			// 表示水印图片的位置
			// g.drawImage(img, 80, 430, null);
			g.drawImage(iconImg, srcImgWidth - iconImgWidth, srcImgHeight
					- iconImgHeight, (int) (iconImgWidth * zoomPercent),
					(int) (iconImgHeight * zoomPercent), null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// 生成图片
			ImageIO.write(buffImg, "JPG", os);

			System.out.println("图片完成添加Icon印章。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
     * 给图片添加水印、可设置水印图片旋转角度
     * 
     * @param iconPath
     *            水印图片路径
     *            源图片文件
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     * @param zoomPercent
     *            水印图片的缩放比例0~1
     */
	public static void markImageByIconByFile(String iconPath, File srcImgFile,
			String targerPath, double zoomPercent, Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(srcImgFile);
			int srcImgWidth = srcImg.getWidth(null);
			int srcImgHeight = srcImg.getHeight(null);

			BufferedImage buffImg = new BufferedImage(srcImgWidth,
					srcImgHeight, BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			// Graphics g= buffImg.getGraphics();
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(
					srcImg.getScaledInstance(srcImg.getWidth(null),
							srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
					null);

			if (null != degree) {
				// 设置水印旋转 ת
				g.rotate(Math.toRadians(degree),
						(double) buffImg.getWidth() / 2,
						(double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
		    // ImageIcon imgIcon = new ImageIcon(iconPath);

		    // 得到Image对象。
		    // Image img = imgIcon.getImage();
			Image iconImg = ImageIO.read(new File(iconPath));
			int iconImgWidth = iconImg.getWidth(null);
			int iconImgHeight = iconImg.getHeight(null);

			float alpha = 1f; // 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));

			// 表示水印图片的位置
			// g.drawImage(img, 80, 430, null);
			g.drawImage(iconImg, srcImgWidth - iconImgWidth, srcImgHeight
					- iconImgHeight, (int) (iconImgWidth * zoomPercent),
					(int) (iconImgHeight * zoomPercent), null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// 生成图片
			ImageIO.write(buffImg, "JPG", os);

			System.out.println("图片完成添加Icon印章。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
     * 给图片添加水印、可设置水印图片旋转角度
     * 
     * @param iconPath
     *            水印图片路径
     * @param srcImgPath
     *            源图片路径
     * @param targerPath
     *            目标图片路径
     * @param degree
     *            水印图片旋转角度
     * @param zoomPercent
     *            水印图片的缩放比例0~1
     */
	public static void markImageByIcon(String iconPath, String srcImgPath,
			String targerPath, double zoomPercent, Integer degree) {
		OutputStream os = null;
		try {
			Image srcImg = ImageIO.read(new File(srcImgPath));
			int srcImgWidth = srcImg.getWidth(null);
			int srcImgHeight = srcImg.getHeight(null);

			BufferedImage buffImg = new BufferedImage(srcImgWidth,
					srcImgHeight, BufferedImage.TYPE_INT_RGB);

			// 得到画笔对象
			// Graphics g= buffImg.getGraphics();
			Graphics2D g = buffImg.createGraphics();

			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			g.drawImage(
					srcImg.getScaledInstance(srcImg.getWidth(null),
							srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0,
					null);

			if (null != degree) {
				// 设置水印旋转 ת
				g.rotate(Math.toRadians(degree),
						(double) buffImg.getWidth() / 2,
						(double) buffImg.getHeight() / 2);
			}

			// 水印图象的路径 水印一般为gif或者png的，这样可设置透明度
		    // ImageIcon imgIcon = new ImageIcon(iconPath);

		    // 得到Image对象。
		    // Image img = imgIcon.getImage();
			Image iconImg = ImageIO.read(new File(iconPath));
			int iconImgWidth = iconImg.getWidth(null);
			int iconImgHeight = iconImg.getHeight(null);

			float alpha = 1f; // 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));

			// 表示水印图片的位置
			// g.drawImage(img, 80, 430, null);
			g.drawImage(iconImg, srcImgWidth - iconImgWidth, srcImgHeight
					- iconImgHeight, (int) (iconImgWidth * zoomPercent),
					(int) (iconImgHeight * zoomPercent), null);

			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));

			g.dispose();

			os = new FileOutputStream(targerPath);

			// 生成图片
			ImageIO.write(buffImg, "JPG", os);

			System.out.println("图片完成添加Icon印章。。。。。。");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != os)
					os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}