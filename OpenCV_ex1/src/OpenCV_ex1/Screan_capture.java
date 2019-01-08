package OpenCV_ex1;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class Screan_capture implements ActionListener{
	private BufferedImage tmpImage;
	private BufferedImage finalImage;
	private Robot robot;
	private Rectangle rectangle;
	private Button captureAllBtn;
	private Button captureAreaBtn;
	private Button saveCaptureBtn;
	private Button changeBtn;
	private CaptureCanvas drawCanvas;
	private JFrame frame;
	private JPanel topPanel;
	private JScrollPane drawPanel;
	private JPanel leftBtnPanel;
	private JPanel rightBtnPanel;
	private Button colorPickBtn;

	public static void main(String[] args) {
		Screan_capture capture = new Screan_capture();
	}
		
	public Screan_capture() {
		try {
			robot = new Robot();
		} catch (AWTException error) {
			System.out.println("AWTException Error: " + error);
		}

		// Layout part
		frame = new JFrame("Screen Capture");
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		leftBtnPanel = new JPanel();
		captureAllBtn = new Button("All screan");
		captureAreaBtn = new Button("Block capture");
		leftBtnPanel.add(captureAllBtn);
		leftBtnPanel.add(captureAreaBtn);

		rightBtnPanel = new JPanel();
		changeBtn = new Button("chage");
		rightBtnPanel.add(changeBtn);
		saveCaptureBtn = new Button("save");
		rightBtnPanel.add(saveCaptureBtn);


		topPanel.add("West", leftBtnPanel);
		captureAllBtn.addActionListener(this);
		captureAreaBtn.addActionListener(this);
		changeBtn.addActionListener(this);
		saveCaptureBtn.addActionListener(this);
		drawPanel = new JScrollPane();

		frame.setLayout(new BorderLayout());
		frame.add("North", topPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void captureImage() {
		rectangle = new Rectangle(0, 0, 0, 0);

		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			rectangle = rectangle.union(gd.getDefaultConfiguration().getBounds());
		}
		// hide window immediately
		java.awt.Point originalPosition = frame.getLocation();
		frame.setLocation(rectangle.width, rectangle.height);
		frame.dispose();

		tmpImage = robot.createScreenCapture(rectangle);
		frame.setVisible(true);
		frame.setLocation(originalPosition.x, originalPosition.y);
	}

	public void clipImage() {
		ImageClipedEventResponder eventHandler = new ImageClipedEventResponder() {
			@Override
			public void implementClipArea(Rectangle r) {
				tmpImage = tmpImage.getSubimage(r.x, r.y, r.width, r.height);
				editImage();
			}
		};
		SelectImageArea area = new SelectImageArea(tmpImage, eventHandler);
	}

	public void editImage() {
		int preW = frame.getWidth(), preH = frame.getHeight();

		if (drawPanel != null)
			frame.remove(drawPanel);
		if (colorPickBtn != null)
			rightBtnPanel.remove(colorPickBtn);
		topPanel.add("East", rightBtnPanel);

		drawCanvas = new CaptureCanvas(tmpImage);
		drawPanel = new JScrollPane(drawCanvas, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		drawCanvas.setAutoscrolls(true);
		colorPickBtn = drawCanvas.createColorPickBtn();
		rightBtnPanel.add("West", colorPickBtn);
		frame.add("Center", drawPanel);

		frame.pack();
		Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int baseW = frame.getWidth() - drawPanel.getViewport().getWidth(),
				baseH = frame.getHeight() - drawPanel.getViewport().getHeight();
		frame.setSize(Math.max(Math.max(Math.min(tmpImage.getWidth() / 2 + baseW, screenSize.width), preW), 300),
				Math.max(Math.max(Math.min(tmpImage.getHeight() / 2 + baseH, screenSize.height), preH), 200));
		frame.setVisible(true);
	}

	public String saveImage() {
		File dir = new File("./ScreenCapture/");
		if (!dir.exists())
			dir.mkdirs();
		String file_name = "./ScreenCapture/" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".png";
		File file = new File(file_name);
		try {
			ImageIO.write(finalImage, "png", file);

			JLabel message = new JLabel("file:" + file.getCanonicalPath());
			message.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent event) {
					try {
						Desktop.getDesktop().open(file);
					} catch (IOException error) {
						System.out.println("IOException Error: " + error);
					}
				}
			});
			frame.add("South", message);
			// frame.pack();
			// frame.setVisible(true);
			Timer timer = new Timer();
			TimerTask removeMessage = new TimerTask() {
				@Override
				public void run() {
					frame.remove(message);
					frame.pack();
					frame.setVisible(true);
				}
			};
			timer.schedule(removeMessage, 3000);
		} catch (IOException error) {
			System.out.println("IOException Error: " + error);
		}
		return file_name;
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == captureAllBtn) {
			captureImage();
			editImage();
		} else if (event.getSource() == captureAreaBtn) {
			captureImage();
			clipImage();
		} else if (event.getSource() == saveCaptureBtn) {
			finalImage = drawCanvas.tmpImage;
			frame.remove(drawPanel);
			topPanel.remove(rightBtnPanel);
			saveImage();
			frame.pack();
		}
		else if(event.getSource() == changeBtn) {
			finalImage = drawCanvas.tmpImage;
			frame.remove(drawPanel);
			topPanel.remove(rightBtnPanel);
			String file = saveImage();
			frame.pack();
			OpenCV_ex1.text_chage(file);
		}
	}
}
interface ImageClipedEventListener {
	void implementClipArea(Rectangle r);
}

// ImageClipedEvent
class ImageClipedEvent {
	private ImageClipedEventListener eventListener;

	public void addListener(ImageClipedEventListener el) {
		eventListener = el;
	}

	public void implement(Rectangle r) {
		eventListener.implementClipArea(r);
	}
}

// ImageClipedEvent
class ImageClipedEventResponder implements ImageClipedEventListener {
	@Override
	public void implementClipArea(Rectangle r) {
	}
}

// Canvas is too old and it's hard to use, so we use JPanel directly.
class CaptureCanvas extends JPanel implements MouseListener, MouseMotionListener {
	java.awt.Point p1;
	private java.awt.Point p2;
	public BufferedImage tmpImage;
	private Graphics2D g2;
	private Color color = Color.BLACK;

	public CaptureCanvas(BufferedImage _tmpimage) {
		addMouseListener(this);
		addMouseMotionListener(this);
		setSize(_tmpimage.getWidth(), _tmpimage.getHeight());
		setPreferredSize(new Dimension(_tmpimage.getWidth(), _tmpimage.getHeight()));
		tmpImage = _tmpimage;
	}

	public Button createColorPickBtn() {
		Button pickButton = new Button("color");
		pickButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				color = JColorChooser.showDialog(null, "color", color);
			}
		});
		return pickButton;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(tmpImage, 0, 0, null);
	}

	public void mousePressed(MouseEvent event) {
		p1 = event.getPoint();
	}

	public void mouseDragged(MouseEvent event) {
		p2 = event.getPoint();
		g2 = (Graphics2D) tmpImage.getGraphics();
		g2.setColor(color);
		g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2.drawLine(p1.x, p1.y, p2.x, p2.y);
		g2.dispose();
		p1 = p2;
		repaint();
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void mouseClicked(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
	}
}

class SelectImageArea extends JPanel implements MouseListener, MouseMotionListener {
	private JFrame frame = new JFrame();
	private BufferedImage tmpImage;
	private JLabel imageLabel;
	private java.awt.Point p1 = new java.awt.Point(0, 100);
	private java.awt.Point p2 = new java.awt.Point(0, 100);
	private int x = 0, y = 0, w = 0, h = 0;
	private ImageClipedEventResponder eventListener;

	public SelectImageArea(BufferedImage _tmpimage, ImageClipedEventResponder el) {
		tmpImage = _tmpimage;
		eventListener = el;
		frame.add(this);

		setSize(tmpImage.getWidth(), tmpImage.getHeight());
		setPreferredSize(new Dimension(tmpImage.getWidth(), tmpImage.getHeight()));
		frame.setSize(tmpImage.getWidth(), tmpImage.getHeight());
		frame.setPreferredSize(new Dimension(tmpImage.getWidth(), tmpImage.getHeight()));

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gd = ge.getScreenDevices();
		int minX = 0, minY = 0;
		for (int i = 0; i < gd.length; i++) {
			Rectangle bounds = gd[i].getDefaultConfiguration().getBounds();
			if (bounds.x < minX) {
				minX = bounds.x;
			}
			if (bounds.y < minY) {
				minY = bounds.y;
			}
		}

		addMouseListener(this);
		addMouseMotionListener(this);

		frame.setUndecorated(true);
		frame.setLocation(minX, minY);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(tmpImage, 0, 0, null);
		g.setColor(new Color(0f, 0f, 0f, 0.75f));
		x = (int) Math.min(p1.x, p2.x);
		y = (int) Math.min(p1.y, p2.y);
		w = (int) Math.abs(p2.x - p1.x);
		h = (int) Math.abs(p2.y - p1.y);
		g.fillRect(0, 0, tmpImage.getWidth(), y);
		g.fillRect(x + w, y, tmpImage.getWidth() - x - w, h);
		g.fillRect(0, y + h, tmpImage.getWidth(), tmpImage.getHeight() - y - h);
		g.fillRect(0, y, x, h);
		g.setColor(new Color(1f, 1f, 1f, 0.75f));
		g.drawRect(x, y, w, h);

		if (w != 0 || h != 0) {
			Font font = new Font("Arial", Font.BOLD, 16);
			g.setFont(font);
			FontMetrics metrics = g.getFontMetrics(font);
			String size = "X: " + x + " / Y: " + y + " / W: " + w + " / H: " + h;

			g.setColor(new Color(0f, 0f, 0f, 0.25f));
			int textBaseX = Math.max(x + w - metrics.stringWidth(size) - 5, 10),
					textBaseY = Math.min(y + h + metrics.getHeight() / 2 + metrics.getAscent(),
							tmpImage.getHeight() - metrics.getHeight() + metrics.getAscent() - 5);
			g.drawString(size, textBaseX + 1, textBaseY + 1);
			g.drawString(size, textBaseX - 1, textBaseY - 1);
			g.drawString(size, textBaseX + 1, textBaseY - 1);
			g.drawString(size, textBaseX - 1, textBaseY + 1);
			g.drawString(size, textBaseX + 1, textBaseY);
			g.drawString(size, textBaseX, textBaseY + 1);
			g.drawString(size, textBaseX - 1, textBaseY);
			g.drawString(size, textBaseX, textBaseY - 1);

			g.setColor(Color.WHITE);
			g.drawString(size, textBaseX, textBaseY);
		}
	}

	public void mousePressed(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event)) {
			if (w != 0 && h != 0) {
				p1 = p2;
			} else {
				frame.setVisible(false);
				frame.dispose();
			}
			repaint();
		} else if (SwingUtilities.isLeftMouseButton(event)) {
			p1 = event.getPoint();
		}
	}

	public void mouseDragged(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			p2 = event.getPoint();
			repaint();
		}
	}

	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 2 && w > 0 && h > 0) {
			eventListener.implementClipArea(new Rectangle(x, y, w, h));
			frame.setVisible(false);
			frame.dispose();
		}
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseMoved(MouseEvent event) {
	}
}

