import com.google.code.kaptcha.Producer;
import org.apache.commons.codec.binary.Base64;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.SecureRandom;
public class main {
    Producer captchaProducer;

    public String getCaptcha() {
        SecureRandom randChars = new SecureRandom();
        String sImageCode = "" + (randChars.nextInt(89999) + 10000);
        return sImageCode;
    }

    public String drawCaptCha(String captcha) throws Exception {
        BufferedImage originalImage = drawCaptcha(captcha);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        byte[] imageInByte = baos.toByteArray();
        originalImage.flush();
        originalImage.getGraphics().dispose();
        return new Base64().encodeToString(imageInByte);
    }

    public BufferedImage drawCaptcha(String captchaString) {
        int width = 200;
        int height = 80;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
        Font font = new Font("Liberation", Font.BOLD, 30);
        g2d.setFont(font);
//        g2d.setColor(...);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);

        SecureRandom r = new SecureRandom();
        int x = 0;
        int y;
        for (int i = 0; i < captchaString.length(); i++) {
            x += 15 + (Math.abs(r.nextInt()) % 15);
            y = 37 + Math.abs(r.nextInt()) % 21;
            g2d.drawString(captchaString.substring(i, i + 1), x, y);
        }
        return bufferedImage;
    }

    public String generate(String captcha) throws IOException {
        BufferedImage bufferedImage = captchaProducer.createImage(captcha);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        String data = DatatypeConverter.printBase64Binary(baos.toByteArray());
        return data;
    }

    //decode
    public static void main(String[] args) throws IOException {
        String data = "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";
        byte[] bytes = DatatypeConverter.parseBase64Binary(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        baos.write(bytes, 0, bytes.length);
        FileOutputStream fos = null;
        fos = new FileOutputStream(new File("test.png"));
        baos.writeTo(fos);
    }
}