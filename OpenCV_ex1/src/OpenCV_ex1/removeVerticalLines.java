package OpenCV_ex1;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class removeVerticalLines {
	public static void removeVerticalLines(Mat img, int limit) {
		Mat lines = new Mat();
		int threshold = 100; // 선 추출 정확도
		int minLength = 80; // 추출할 선의 길이
		int lineGap = 5; // 5픽셀 이내로 겹치는 선은 제외
		int rho = 1;
		Imgproc.HoughLinesP(img, lines, rho, Math.PI / 180, threshold, minLength, lineGap);
		for (int i = 0; i < lines.total(); i++) {
			double[] vec = lines.get(i, 0);
			Point pt1, pt2;
			pt1 = new Point(vec[0], vec[1]);
			pt2 = new Point(vec[2], vec[3]);
			double gapY = Math.abs(vec[3] - vec[1]);
			double gapX = Math.abs(vec[2] - vec[0]);
			if (gapY > limit && limit > 0) {
				// remove line with black color
				Imgproc.line(img, pt1, pt2, new Scalar(0, 0, 0), 10);
			}
		}
	}
}
