package memberMVC.board;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.commons.io.FileUtils;

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
      PrintWriter out;
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
        	 int articleNo = 0;
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
        	 articleNo = boardService.addArticle(articleVO);
        	 //새글 추가 시 이미지를 첨부한 경우에만 수행
        	 if(imageFileName != null && imageFileName.length() != 0) {
        		 //temp 폴더에 임시로 업로드된 파일 객체를 생성
        		 File srcFile = new File(IMG_REPO + "\\temp\\" + imageFileName);
        		 //새 글 추가된 글번호로 폴더를 생성
        		 File destDir = new File(IMG_REPO + "\\" + articleNo);
        		 destDir.mkdir();
        		 //temp폴더의 파일을 글 번호 폴더로 이동
        		 FileUtils.moveFileToDirectory(srcFile, destDir, true);
        		 srcFile.delete(); 
        	 }
        	 out = response.getWriter();
        	 out.print("<script>");
        	 out.print("alert('새글을 추가했습니다.');");
        	 out.print("location.href='" + request.getContextPath()+"/board/listArticles.do';");
        	 out.print("</script>");
        	 return;
         }else if(action.equals("/viewArticle.do")) {
        	 String articleNo = request.getParameter("articleNo");
        	 articleVO = boardService.viewArticle(Integer.parseInt(articleNo));
        	 request.setAttribute("article", articleVO);
        	 nextPage = "/boardInfo/viewArticle.jsp";
         }else if(action.equals("/modArticle.do")) {
        	 Map<String, String> articleMap = upload(request, response);
        	 int articleNo = Integer.parseInt(articleMap.get("articleNo"));
        	 String title = articleMap.get("title");
        	 String content = articleMap.get("content");
        	 String imageFileName = articleMap.get("imageFileName");
        	 articleVO.setArticleNo(articleNo);
        	 articleVO.setParentNo(0);
        	 articleVO.setId("son");
        	 articleVO.setTitle(title);
        	 articleVO.setContent(content);
        	 articleVO.setImageFileName(imageFileName);
        	 boardService.modArticle(articleVO);
        	 //이미지를 첨부한 경우에만 수행
        	 if(imageFileName != null && imageFileName.length() !=0) {
        		 String originalFileName = articleMap.get("originalFileName");
        		 File srcFile = new File(IMG_REPO +"\\temp\\" + imageFileName);
        		 File destDir = new File(IMG_REPO + "\\" + articleNo);
        		 destDir.mkdir();
        		 FileUtils.moveFileToDirectory(srcFile, destDir, true);
        		 File oldFile = new File(IMG_REPO + "\\" +articleNo+"\\" +originalFileName);
        		 oldFile.delete();
        	 }
        	 out = response.getWriter();
        	 out.print("<script>");
        	 out.print("alert('새글을 수정했습니다.');");
        	 out.print("location.href='" + request.getContextPath()+"/board/viewArticle.do?articleNo=" + articleNo +"';");
        	 out.print("</script>");
        	 return;
         }else if(action.equals("/removeArticle.do")) {
        	 int articleNo = Integer.parseInt(request.getParameter("articleNo"));
        	 List<Integer> articleNoList = boardService.removeArticle(articleNo);
        	 
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
					   File uploadFile = new File(currentDirPath + "\\temp\\" + fileName);
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