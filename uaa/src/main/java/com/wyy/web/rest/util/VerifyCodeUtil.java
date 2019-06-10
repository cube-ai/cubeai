package com.wyy.web.rest.util;

import org.apache.commons.lang3.RandomUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;


public final class VerifyCodeUtil {
    private static Random random = new Random();
    private static String charFrom = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static int width = 100; //图片宽度
    private static int height = 40; //图片高度
    private static int codeLength = 4;  //字符的数量
    private static int lineNum = 40;  //干扰线数量

    private VerifyCodeUtil() {
    }

    // 获取随机字符,并返回字符的String格式
    private static String getRandomChar(int index) {
        return String.valueOf(charFrom.charAt(index));
    }

    // 获取随机指定区间的随机数
    private static int getRandomNum(int min, int max) {
        return RandomUtils.nextInt(min, max);
    }

    // 获得颜色
    private static Color getRandColor(int frontColor, int backColor) {
        int red = frontColor + random.nextInt(backColor - frontColor - 16);
        int green = frontColor + random.nextInt(backColor - frontColor - 14);
        int blue = frontColor + random.nextInt(backColor - frontColor - 18);
        return new Color(red, green, blue);
    }

    // 绘制字符
    private static void drawChar(Graphics g, String randomChar, int i) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Fixedsys", Font.CENTER_BASELINE, 25));   // 设置字体
        g2d.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat())); //设置颜色
        int rot = getRandomNum(5, 10);
        g2d.translate(random.nextInt(3), random.nextInt(3));
        g2d.rotate(rot * Math.PI / 180);
        g2d.drawString(randomChar, 15 * i, 25);
        g2d.rotate(-rot * Math.PI / 180);
    }

    // 绘制干扰线
    private static void drawLine(Graphics g) {
        // 起点(x,y)  偏移量x1、y1
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        g.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        g.drawLine(x, y, x + xl, y + yl);
    }

    public static String genRandomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            sb.append(getRandomChar(random.nextInt(charFrom.length())));
        }
        return sb.toString();
    }

    // 生成Base64图片验证码
    public static String drawCodePicture(String randomCode) {

        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics(); // 获得BufferedImage对象的Graphics对象
        g.fillRect(0, 0, width, height); // 填充矩形
        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18)); // 设置字体
        g.setColor(getRandColor(110, 133));//设置颜色

        //绘制干扰线
        for (int i = 0; i < lineNum; i++) {
            drawLine(g);
        }

        //绘制字符
        for (int i = 0; i < codeLength; i++) {
            drawChar(g, randomCode.substring(i, i + 1), i + 1);
        }

        g.dispose();//释放绘图资源

        ByteArrayOutputStream bs = null;
        try {
            bs = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bs); //将绘制得图片输出到流
            bs.close();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bs.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 生成伪图片序列码，将验证码字符插入其中
    public static String genCodePicture(String randomCode) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 512; i++) {
            if (17 == i) {
                sb.append(randomCode.charAt(0));
            } else if (71 == i) {
                sb.append(randomCode.charAt(1));
            } else if (127 == i) {
                sb.append(randomCode.charAt(2));
            } else if (227 == i) {
                sb.append(randomCode.charAt(3));
            } else {
                sb.append(getRandomChar(random.nextInt(charFrom.length())));
            }
        }

        return Base64.getEncoder().encodeToString(sb.toString().getBytes());
    }

}
