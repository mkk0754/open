package OpenCV_ex1;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class testCountour {
	private void testContour(Mat imageMat) {
		Mat rgb = new Mat(); // rgb color matrix
		rgb = imageMat.clone();
		Mat grayImage = new Mat(); // grey color matrix
		Mat erorsion_Ex = new Mat();// 침식
		Mat mask_erorsion = Mat.eye(2, 2, CvType.CV_8UC1);
		Mat dilation_Ex = new Mat();// 팽창
		Mat mask_dilation = Mat.eye(9, 5, CvType.CV_8UC1);

		Imgproc.cvtColor(rgb, grayImage, Imgproc.COLOR_RGB2GRAY);

		Mat gradThresh = new Mat(); // matrix for threshold
		Mat hierarchy = new Mat(); // matrix for contour hierachy
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.morphologyEx(grayImage, erorsion_Ex, Imgproc.MORPH_GRADIENT, mask_erorsion);
		Imgproc.adaptiveThreshold(erorsion_Ex, gradThresh, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,
				Imgproc.THRESH_BINARY_INV, 3, 12); // block size 3

		Imgproc.morphologyEx(gradThresh, dilation_Ex, Imgproc.MORPH_CLOSE, mask_dilation);
		removeVerticalLines remove = new removeVerticalLines();
		remove.removeVerticalLines(dilation_Ex, 30);
		Imgproc.findContours(dilation_Ex, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,
				new Point(0, 0));
		Tess4j tess = new Tess4j();
		if (contours.size() > 0) {
			for (int idx = 0; idx < contours.size(); idx++) {
				Rect rect = Imgproc.boundingRect(contours.get(idx));
				if (rect.height > 10 && rect.width > 40 && !(rect.width >= 512 - 5 && rect.height >= 512 - 5)) {
					Rect rectCrop = new Rect(new Point(rect.br().x - rect.width, rect.br().y - rect.height), rect.br());
					Mat dstMat = new Mat(gradThresh, rectCrop);
					String st = new String();
					st = "./image/doc_dd.png";
					Imgcodecs.imwrite(st, dstMat);
					// 저장한 값을 retrain을 해서 text인지 아닌지 구분
					Retrain retrain = new Retrain();
					String s = retrain.retrain(st);
					if (s.equals("text")) {
						Mat copy = new Mat();
						copy = rgb.clone();
						dstMat = new Mat(copy, rectCrop);
						st = new String();
						st = "./image/doc_2.png";
						Imgcodecs.imwrite(st, dstMat);
						tess.tess4j();
						Imgproc.rectangle(imageMat, new Point(rect.br().x - rect.width, rect.br().y - rect.height),
								rect.br(), new Scalar(0, 255, 255), 3);
					}
				}
			}

		}
	}

	public void input(Mat imageMat) {
		testContour(imageMat);
	}
}
