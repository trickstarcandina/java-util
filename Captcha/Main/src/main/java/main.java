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