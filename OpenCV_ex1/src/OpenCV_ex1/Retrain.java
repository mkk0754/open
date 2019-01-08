package OpenCV_ex1;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Retrain {
	public static String retrain(String data) {
		String s = null;

		try {
			// print a message
			System.out.println("Executing python code");
			String pythinScriptPath = "./judge_text.exe";
			//String data = "C://tmp//doc_3.png";
			String[] cmd = new String[2];
			cmd[0] = pythinScriptPath;
			cmd[1] = data;
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec(cmd);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			// read the output from the command

			System.out.println("python should be run.");
			PrintWriter outputStream = null;
			s=stdInput.readLine();
			System.out.println(s);
			//System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return s;
	}
}
