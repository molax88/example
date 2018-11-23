package io.hpb.contract.utils;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ImageUtils {
	/**
	 * 图片属性枚举.
	 */
	public static enum ImageFormat {
		JPEG, GIF, PNG, BMP;
	}

	/**
	 * 得到图片属性
	 *
	 * @param file
	 */
	public static ImageFormat getImageFormat(File file) throws Exception {
		ImageInputStream in = null;
		try {
			in = createImageInputStream(file);
			Iterator<ImageReader> itr = ImageIO.getImageReaders(in);
			if (itr.hasNext()) {
				ImageReader reader = itr.next();
				if (StringUtils.equals(reader.getFormatName(), ImageFormat.GIF.name())) {
					return ImageFormat.GIF;
				} else if (StringUtils.equals(reader.getFormatName(), ImageFormat.JPEG.name())) {
					return ImageFormat.JPEG;
				} else if (StringUtils.equals(reader.getFormatName(), ImageFormat.PNG.name())) {
					return ImageFormat.PNG;
				} else if (StringUtils.equals(reader.getFormatName(), ImageFormat.BMP.name())) {
					return ImageFormat.BMP;
				}
			}
			throw new Exception("unknow.image.format");
		} finally {
			closable(in);
		}
	}

	/**
	 * 读取图片
	 *
	 * @param file
	 */
	public static BufferedImage readImage(File file) throws Exception {
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			throw new Exception("image operator exception");
		}
	}

	/**
	 * 读取图片
	 *
	 * @param reader
	 * @param index
	 * @param param
	 */
	public static BufferedImage readImage(ImageReader reader, int index, ImageReadParam param) throws Exception {
		try {
			return reader.read(index, param);
		} catch (IOException e) {
			throw new Exception("image operator exception");
		}
	}

	/**
	 * 输出到文件
	 *
	 * @param image
	 * @param format
	 * @param file
	 */
	public static void write(BufferedImage image, ImageFormat format, File file) throws IOException {
		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			/*
			 * JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output);
			 * encoder.encode(image);
			 */
			ImageIO.write(image, format.name(), output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	/**
	 * 图片裁剪
	 *
	 * @param width
	 *            网页中容器宽
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param old_image
	 * @param new_image
	 * @throws IOException
	 */
	public static void cutImage(int width, int x, int y, int w, int h, File old_image, File new_image) throws IOException {
		BufferedImage photo = ImageIO.read(old_image);
		float b = new BigDecimal(width).divide(new BigDecimal(photo.getWidth()), 3, BigDecimal.ROUND_HALF_UP).floatValue();
		float h1 = Float.parseFloat(photo.getHeight() + "") * b;
		int h2 = (int) h1;

		Image image = photo.getScaledInstance(width, h2, Image.SCALE_DEFAULT);
		BufferedImage oimage = new BufferedImage(width, h2, Image.SCALE_DEFAULT);
		oimage.getGraphics().drawImage(image, 0, 0, null);
		int cw = w;
		int ch = h;
		int pw = width;
		int ph = h2;
		if (pw < x + w) {
			cw = pw - x;
		}
		if (ph < y + h) {
			ch = ph - y;
		}
		BufferedImage bufferedImage = new BufferedImage(cw, ch, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < ch; i++) {
			for (int j = 0; j < cw; j++) {
				bufferedImage.setRGB(j, i, oimage.getRGB(x + j, y + i));
			}
		}
		ImageUtils.write(bufferedImage, ImageFormat.JPEG, new_image);
	}

	/**
	 * 文件输入流
	 *
	 * @param file
	 */
	public static ImageInputStream createImageInputStream(File file) throws Exception {
		try {
			return ImageIO.createImageInputStream(file);
		} catch (IOException e) {
			throw new Exception("image operator exception");
		}
	}

	/**
	 * 关闭 文件流
	 *
	 * @param in
	 */
	public static void closable(ImageInputStream in) throws Exception {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				throw new Exception("image operator exception");
			}
		}
	}

	public static void deleteTempImage(String path) {
		File file = new File(path);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	public static String getEncodeBase64StringFromImg(String filePath, int imgWidth, int imgHeight) throws IOException {
		return Base64.encodeBase64String(io.hpb.contract.utils.ImgCompress.resizeFix(filePath, imgWidth, imgHeight));
	}

	public static final String QRCODE_DEFAULT_CHARSET = "UTF-8";

    public static final int QRCODE_DEFAULT_HEIGHT = 180;

    public static final int QRCODE_DEFAULT_WIDTH = 180;

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    public static void main(String[] args) throws IOException, NotFoundException{
        String data = "http://www.zjfae.com/";
        File logoFile = new File("D:/Personal/Desktop/zhijin.gif");
        BufferedImage image = createQRCodeWithLogo(data, logoFile);
        ImageIO.write(image, "png", new File("D:/Personal/Desktop/result7.png"));
        System.out.println("done");
    }

    /**
     * Create qrcode with default settings
     *
     * @author stefli
     * @param data
     * @return
     */
    public static BufferedImage createQRCode(String data) {
        return createQRCode(data, QRCODE_DEFAULT_WIDTH, QRCODE_DEFAULT_HEIGHT);
    }

    /**
     * Create qrcode with default charset
     *
     * @author stefli
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage createQRCode(String data, int width, int height) {
        return createQRCode(data, QRCODE_DEFAULT_CHARSET, width, height);
    }

    /**
     * Create qrcode with specified charset
     *
     * @author stefli
     * @param data
     * @param charset
     * @param width
     * @param height
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static BufferedImage createQRCode(String data, String charset, int width, int height) {
        Map hint = new HashMap();
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hint.put(EncodeHintType.CHARACTER_SET, charset);

        return createQRCode(data, charset, hint, width, height);
    }

    /**
     * Create qrcode with specified hint
     *
     * @author stefli
     * @param data
     * @param charset
     * @param hint
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage createQRCode(String data, String charset, Map<EncodeHintType, ?> hint, int width,
            int height) {
        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), BarcodeFormat.QR_CODE,
                    width, height, hint);
            return toBufferedImage(matrix);
        } catch (WriterException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }
    /**
     * Create qrcode with default settings and logo
     *
     * @author stefli
     * @param data
     * @param logoFile
     * @return
     */
    public static BufferedImage createQRCodeWithLogo(String data, File logoFile) {
        return createQRCodeWithLogo(data, QRCODE_DEFAULT_WIDTH, QRCODE_DEFAULT_HEIGHT, logoFile);
    }

    /**
     * Create qrcode with default charset and logo
     *
     * @author stefli
     * @param data
     * @param width
     * @param height
     * @param logoFile
     * @return
     */
    public static BufferedImage createQRCodeWithLogo(String data, int width, int height, File logoFile) {
        return createQRCodeWithLogo(data, QRCODE_DEFAULT_CHARSET, width, height, logoFile);
    }

    /**
     * Create qrcode with specified charset and logo
     *
     * @author stefli
     * @param data
     * @param charset
     * @param width
     * @param height
     * @param logoFile
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static BufferedImage createQRCodeWithLogo(String data, String charset, int width, int height, File logoFile) {
        Map hint = new HashMap();
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hint.put(EncodeHintType.CHARACTER_SET, charset);

        return createQRCodeWithLogo(data, charset, hint, width, height, logoFile);
    }

    /**
     * Create qrcode with specified hint and logo
     *
     * @author stefli
     * @param data
     * @param charset
     * @param hint
     * @param width
     * @param height
     * @param logoFile
     * @return
     */
    public static BufferedImage createQRCodeWithLogo(String data, String charset, Map<EncodeHintType, ?> hint,
            int width, int height, File logoFile) {
        try {
            BufferedImage qrcode = createQRCode(data, charset, hint, width, height);
            /*ByteInputStream byteInputStream = new ByteInputStream();
            byte[] resizeFix = ImgCompress.resizeFix(logoFile, 39, 31);
            byteInputStream.setBuf(resizeFix);
			FileUtils.copyInputStreamToFile(byteInputStream, logoFile);*/
            BufferedImage logo = ImageIO.read(logoFile);
            int deltaHeight = height - logo.getHeight();
            int deltaWidth = width - logo.getWidth();

            BufferedImage combined = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) combined.getGraphics();
            g.drawImage(qrcode, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.drawImage(logo, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);

            return combined;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Return base64 for image
     *
     * @author stefli
     * @param image
     * @return
     */
    public static String getImageBase64String(BufferedImage image) {
        String result = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStream b64 = new Base64OutputStream(os);
            ImageIO.write(image, "png", b64);
            result = os.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * Decode the base64Image data to image
     *
     * @author stefli
     * @param base64ImageString
     * @param file
     */
    public static void convertBase64StringToImage(String base64ImageString, File file) {
        FileOutputStream os;
        try {
            Base64 d = new Base64();
            byte[] bs = d.decode(base64ImageString);
            os = new FileOutputStream(file.getAbsolutePath());
            os.write(bs);
            os.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


}
