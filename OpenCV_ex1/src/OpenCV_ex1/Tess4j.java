package OpenCV_ex1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Tess4j {
	static int num=0;
	public void tess4j() {
		
		File imageFile = new File("./image/doc_2.png");
		Tesseract instance = Tesseract.getInstance(); // JNA Interface Mapping
		instance.setLanguage("kor+eng");
		instance.setDatapath("./tessdata/");
		System.out.println(imageFile.canRead());
		PrintWriter outputStream = null;
		try {
			String result = instance.doOCR(imageFile);
			try
			{
				String x = "./text_example/text"+num+".txt";
				outputStream = new PrintWriter(new FileOutputStream(x)); 
				num++;
			}
			catch (FileNotFoundException e) // text.txt라는 파일이 존재하지 않을 수 있으므로 예외 발생 가능
			{
				System.out.println("Error opening the file stuff.txt.");
				System.exit(0);
			}
			outputStream.println(result);
			outputStream.close();
			System.out.println(result);

		} catch (TesseractException e) {

			System.err.println(e.getMessage());

		}
	}
}
