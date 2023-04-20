package memberMVC.board;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import oracle.security.o3logon.a;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {
   BoardService boardService;
   ArticleVO articleVO;
   private static String IMG_REPO = "C:\\youngsam\\border\\image_upload";
   


   @Override
   public void init() throws ServletException {
      boardService = new BoardService();
      articleVO = new ArticleVO();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doHandle(request, response);
   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doHandle(request, response);
   }

   private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      request.setCharacterEncoding("UTF-8");
      response.setContentType("text/html; charset=UTF-8");
      String nextPage = "";
      String action = request.getPathInfo();
      System.out.println("요청 이름 : " + action);
      
      try {
         List<ArticleVO> articleList = new ArrayList<ArticleVO>();
         if (action == null || action.equals("/listArticles.do")) {
            articleList = boardService.listArticles();
            request.setAttribute("articleList", articleList);
            nextPage = "/boardInfo/listArticles.jsp";
         }else if(action.equals("/articleForm.do")) {
        	 nextPage ="/boardInfo/articleForm.jsp";
         }else if(action.equals("/addArticle.do")) {
        	 Map<String, String> articleMap = upload(request, response);
        	 String title = articleMap.get("title");
        	 String content = articleMap.get("content");
        	 String imageFileName = articleMap.get("imageFileName");
        	 articleVO.setParentNo(0); ///부모 글 번호를 0으로 지정(자신=부모글)
        	 articleVO.setId("son");
        	 articleVO.setTitle(title);
        	 articleVO.setContent(content);
        	 articleVO.setImageFileName(imageFileName);
        	 boardService.addArticle(articleVO);
        	 nextPage="/board/listArticles.do";
         }else if(action.equals("/viewArticle.do")) {
        	 String articleNo = request.getParameter("articleNo");
        	 articleVO = boardService.viewArticle(Integer.parseInt(articleNo));
        	 request.setAttribute("article", articleVO);
        	 nextPage = "/boardInfo/viewArticle.jsp";
         }
         RequestDispatcher dispatcher = request.getRequestDispatcher(nextPage);
         dispatcher.forward(request, response);
      } catch (Exception e) {
         System.out.println("요청 처리중 에러");
         e.printStackTrace();
      }
      }
   //이미지 파일 업로드 + 새글 관련 정보 저장
   private Map<String, String> upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	   Map<String, String> articleMap = new HashMap<String, String>();
	   String encoding = "utf-8";
	   File currentDirPath = new File(IMG_REPO);//글 이미지 저장 폴더에 대한 객체를 만든다
	   DiskFileItemFactory factory = new DiskFileItemFactory();
	   factory.setRepository(currentDirPath);
	   factory.setSizeThreshold(1024*1024);
	   ServletFileUpload upload = new ServletFileUpload(factory);
	   try {
		   List items = upload.parseRequest(request);
		   for(int i=0; i<items.size(); i++) { 
			   FileItem fileItem =(FileItem)items.get(i);
			   if(fileItem.isFormField()) {
				   System.out.println(fileItem.getFieldName() + "=" + fileItem.getString(encoding));
				   //파일 업로드로 같이 전송된 제목, 내용을 Map(키, 값)로 저장
				   articleMap.put(fileItem.getFieldName(), fileItem.getString(encoding));
			   }else {
				   System.out.println("파라미터 이름 : " + fileItem.getFieldName());
				   System.out.println("파일 (이미지) 이름 : " + fileItem.getName());
				   System.out.println("파일(이미지) 크기 : " +fileItem.getSize() + "bytes");
				   if(fileItem.getSize() >0 ) {
					   int idx = fileItem.getName().lastIndexOf("\\");
					   if(idx == -1) {
						   idx = fileItem.getName().lastIndexOf("/");
					   }
					   String fileName = fileItem.getName().substring(idx+1);
					   articleMap.put(fileItem.getFieldName(), fileName);
					   File uploadFile = new File(currentDirPath + "\\" + fileName);
					   fileItem.write(uploadFile);
				   }
			   }
		   }
	   }catch (Exception e) {
		   System.out.println("파일 업로드 중 에러");
		   e.printStackTrace();
	}
	   return articleMap;
   }



}