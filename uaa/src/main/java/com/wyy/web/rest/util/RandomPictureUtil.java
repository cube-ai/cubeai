package com.wyy.web.rest.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;


public final class RandomPictureUtil {

    public static String drawRandomPicture(int width, int height) {

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
        g.fillRect(0, 0, width, height); // 填充矩形
        g.dispose(); //释放绘图资源

        ByteArrayOutputStream bs = null;
        try {
            bs = new ByteArrayOutputStream();
            ImageIO.write(image, "jpeg", bs); //将绘制得图片输出到流
            bs.close();
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bs.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
