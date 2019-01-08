package open;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.image.codec.jpeg.*;

public class Main extends JPanel implements Runnable, ActionListener {

   JButton btn_capture;
   Image img = null;

   // ������. UI ��ġ.
   public Main() {
      this.btn_capture = new JButton("����ĸ��");
      this.btn_capture.addActionListener(this);
      this.setLayout(new BorderLayout());
      this.add(this.btn_capture, BorderLayout.SOUTH);
   }

   public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      if (cmd.equals("����ĸ��")) {
         System.out.println("������ ĸ���մϴ�..");
         this.capture(); // ����ĸó ��ư�� ������ ĸ��.
      }
   }

   private void drawImage(Image img, int x, int y) {
      Graphics g = this.getGraphics();
      g.drawImage(img, 0, 0, x, y, this);
      this.paint(g);
      this.repaint();
   }

   public void paint(Graphics g) {
      if (this.img != null) {
         g.drawImage(this.img, 0, 0, this.img.getWidth(this), this.img.getHeight(this), this);
      }
   }

   public void capture() {
      Robot robot;
      BufferedImage bufImage = null;
      try {
         robot = new Robot();
         Rectangle area = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

         bufImage = robot.createScreenCapture(area); // Robot Ŭ������ �̿��Ͽ� ��ũ�� ĸ��.

         // Graphics2D g2d = bufImage.createGraphics();
         int w = this.getWidth();
         int h = this.getHeight();

         this.img = bufImage.getScaledInstance(w, h - 20, Image.SCALE_DEFAULT);
         // this.repaint();
         this.drawImage(img, w, h);
         saveJPEGfile("cap.jpg", bufImage); // JPG ���Ϸ� ��ȯ�Ͽ� ����.
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static boolean saveJPEGfile(String filename, BufferedImage bi) {
      FileOutputStream out = null;
      try {
         out = new FileOutputStream(filename);
         JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
         JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
         param.setQuality(1.0f, false);
         encoder.setJPEGEncodeParam(param);

         encoder.encode(bi);
         out.close();
      } catch (Exception ex) {
         System.out.println("Error saving JPEG : " + ex.getMessage());
         return false;
      }
      return true;
   }

   public void run() {
      while (true) {
         this.setBackground(Color.RED);
         try {
            Thread.sleep(1000);
         } catch (Exception e) {
         }
         this.setBackground(Color.GREEN);

         try {
            Thread.sleep(1000);
         } catch (Exception e) {
         }
      }

   }

   public static void createFrame() {
      JFrame frame = new JFrame("Jv");
      JFrame.setDefaultLookAndFeelDecorated(true);
      Container cont = frame.getContentPane();
      cont.setLayout(new BorderLayout());
      Main mm = new Main();
      // new Thread(mm).start();
      cont.add(mm, BorderLayout.CENTER);

      frame.setSize(400, 400);
      frame.setVisible(true);
   }

   public static void main(String... v) {
      // new Main();
      JFrame.setDefaultLookAndFeelDecorated(true);
      createFrame();
   }
}