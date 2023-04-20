package memberMVC.board;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/download.do")
public class FileDownloadController extends HttpServlet {

	private static String IMG_REPO = "C:\\youngsam\\border\\image_upload";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}
	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String articleNo = request.getParameter("articleNo");
		String imageFileName = request.getParameter("imageFileName");
		OutputStream outs = response.getOutputStream();
		String path =IMG_REPO +"\\" + articleNo+"\\" + imageFileName;
		File imageFile = new File(path);
		response.setHeader("Cache-control", "no-cache");
		//이미지 파일을 내려 받는데 필요한 response 헤더 정보를 설정
		response.addHeader("Content-disposition", "attachment;fileNeme=" + imageFileName);
		FileInputStream fis = new FileInputStream(imageFile);
		byte[] buffer = new byte[1024*8]; //버퍼를 이용해서 한번에 8k씩 전송
		while(true) {
			int count = fis.read(buffer);
			if(count == -1) break;
			outs.write(buffer,0,count);
		}
		fis.close();
		outs.close();
	}

}
