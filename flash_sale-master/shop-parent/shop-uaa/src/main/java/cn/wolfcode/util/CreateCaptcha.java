package cn.wolfcode.util;

import java.awt.*;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 生成验证码
 */
public class CreateCaptcha {
    public static final String CAPTCHA = "CAPTCHA";//验证码放在session中的key
    private Random random = new Random();
    //验证码出现的字符
    private static final String randString = "0123456789abcdefghjklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int width = 80;//图片宽
    private int height = 26;//图片高
    private int lineSize = 40;//干扰线数量
    private int stringNum = 6;//随机产生字符数量
    /**
     * 验证码的字体
     */
    private Font getFont(){
        return new Font("Fixedsys",Font.CENTER_BASELINE,18);
    }

    /**
     * 验证码的颜色
     * @param fc 前景色
     * @param bc 背景色
     * @return 生成的颜色
     */
    private Color getRandColor(int fc,int bc){
        if(fc > 255){
            fc = 255;
        }
        if(bc > 255){
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r,g,b);
    }

    /**
     * 生成验证码字符串
     * @return 生成的验证码字符串
     */
    public String getCaptchaCode(){
        StringBuilder stringBuilder = new StringBuilder();
        //随机生成字符进行拼接
        for(int i = 0; i < stringNum; ++i){
           stringBuilder.append(randString.charAt(random.nextInt(randString.length())));
        }
        return stringBuilder.toString();
    }

    public BufferedImage getCaptchaImage(String captchaCode){
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.getGraphics();//这样该对象就可以图像上进行修改操作
        graphics.fillRect(0,0,width,height);
        graphics.setFont(new Font("Times New Roman",Font.ROMAN_BASELINE,18));
        graphics.setColor(getRandColor(110,133));
        //干扰线
        for(int i = 0;i <= lineSize; ++i){
            drowLine(graphics);
        }
        //绘制字符串
        graphics.drawString(captchaCode,10,10);
        graphics.dispose();
        return image;
    }

    /**
     * 用来绘制验证码图片的干扰线
     * @param graphics
     */
    private void drowLine(Graphics graphics){
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        graphics.drawLine(x,y,xl,yl);

    }

}
