package cn.qihangerp.common.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.RandomUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ImageUtils {

    public static File resizeImageFile(String imageSourceUrl, int width, int height) throws IOException{
        String dir = System.getProperty("user.dir");
        String destFileName = dir + File.separator + "/shein/imgs" ;
        File imgDir = new File(destFileName);
        imgDir.mkdirs();
        // 下载原始图片
        File originalFile = new File(imgDir, "original"+RandomUtils.nextInt()+".jpg");
        downloadImage(imageSourceUrl, originalFile);

        // 创建900x900的图片
        File resizedFile = new File(imgDir, "resized_"+width+"_"+height+"_"+ RandomUtils.nextLong() +".jpg");
        resizeImage(originalFile, resizedFile, width, height);

        log.info("原始图片保存至: {}", originalFile.getAbsolutePath());
        log.info("调整大小后的图片保存至: {}", resizedFile.getAbsolutePath());
        return resizedFile;
    }

    /**
     * 下载图片到本地文件
     */
    private static void downloadImage(String imageUrl, File destFile) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Failed to download image");
            }

            // 将响应内容写入文件
            java.nio.file.Files.copy(
                    response.body().byteStream(),
                    destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    /**
     * 调整图片大小并保持图片质量
     */
    private static void resizeImage(File sourceFile, File destFile, int targetWidth, int targetHeight) throws IOException {
        // 读取原始图片
        BufferedImage originalImage = ImageIO.read(sourceFile);

        // 创建一个新的BufferedImage，使用RGB色彩模式
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        // 获取图形上下文
        Graphics2D g2d = resizedImage.createGraphics();

        // 设置图片平滑度
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算裁剪参数，保持图片居中
        double scale = Math.max(
                (double) targetWidth / originalImage.getWidth(),
                (double) targetHeight / originalImage.getHeight()
        );

        int scaledWidth = (int) (originalImage.getWidth() * scale);
        int scaledHeight = (int) (originalImage.getHeight() * scale);
        int x = (targetWidth - scaledWidth) / 2;
        int y = (targetHeight - scaledHeight) / 2;

        // 绘制图片
        g2d.drawImage(originalImage, x, y, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // 保存图片，使用高质量压缩
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = writers.next();

        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.95f); // 设置较高的图片质量

        try (ImageOutputStream output = ImageIO.createImageOutputStream(destFile)) {
            writer.setOutput(output);
            IIOImage iioImage = new IIOImage(resizedImage, null, null);
            writer.write(null, iioImage, param);
        } finally {
            writer.dispose();
        }
    }
}
