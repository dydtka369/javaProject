package memberMVC.board;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
public class BoardDAO {
   private Connection conn;
   private PreparedStatement pstmt;
   private DataSource dataFactory;
   public BoardDAO() {
      try {
         // JNDI에 접근하기 위해 기본 경로(java:/comp/env)를 지정
         Context ctx = new InitialContext();
         Context envContext = (Context) ctx.lookup("java:/comp/env");
         // 톰캣 context.xml에 설정한 name값인 jdbc/oracle을 이용해 톰캣에 미리 연결한 DataSource를 받아옵니다.
         dataFactory = (DataSource) envContext.lookup("jdbc/oracle");
      } catch (Exception e) {
         System.out.println("커넥션풀 연결실패");
         e.printStackTrace();
      }
   }
   // 글 목록
   public List<ArticleVO> selectAllArticles() {
      List<ArticleVO> articleList = new ArrayList<>();
      
      try {
         conn = dataFactory.getConnection();
         String query = "SELECT LEVEL, articleNo, parentNo, title, content, writeDate, id FROM boardtbl " + 
                     "START WITH parentNo=0 CONNECT BY PRIOR articleNo=parentNo ORDER SIBLINGS BY articleNo DESC";
         pstmt = conn.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery();
         while(rs.next()) {
            int level = rs.getInt("level"); // 계층형 글의 깊이(level 속성)
            int articleNo = rs.getInt("articleNo");
            int parentNo = rs.getInt("parentNo");
            String title = rs.getString("title");
            String content = rs.getString("content");
            Date writeDate = rs.getDate("writeDate");
            String id = rs.getString("id");
            ArticleVO articleVO = new ArticleVO();
            articleVO.setLevel(level);
            articleVO.setArticleNo(articleNo);
            articleVO.setParentNo(parentNo);
            articleVO.setTitle(title);
            articleVO.setContent(content);
            articleVO.setWriteDate(writeDate);
            articleVO.setId(id);
            articleList.add(articleVO);
         }
         rs.close();
         pstmt.close();
         conn.close();
      } catch (Exception e) {
         System.out.println("글 목록 처리 중 에러");
         e.printStackTrace();
      }
      return articleList;
   }
   //글번호 생성 메서드
   private int getNewArticleNo() {
	   int aNo=1;
	   try {
		   conn = dataFactory.getConnection();
		   //max 함수를 이용해서 가장 큰 번호를 조회
		   String query = "select max(articleNo) from boardtbl";
		   pstmt = conn.prepareStatement(query);
		   ResultSet rs = pstmt.executeQuery();
		   if(rs.next()) {
			   aNo=rs.getInt(1)+1;
		   }
		   rs.close();
		   pstmt.close();
		   conn.close();
	   }catch (Exception e) {
		   System.out.println("글 번호 생성 중 에러");
		   e.printStackTrace();
	}
	   return aNo;
   }
   	// 새글 추가
   public int insertNewArticle(ArticleVO articleVO) {
	   int articleNo = getNewArticleNo();
	   try {
		   conn = dataFactory.getConnection();
		   int parentNo = articleVO.getParentNo();
		   String title = articleVO.getTitle();
		   String content = articleVO.getContent();
		   String imageFileName = articleVO.getImageFileName();
		   String id = articleVO.getId();
		   String query = "insert into boardtbl (articleNo,parentNo, title, content, imageFileName, id) values(?,?,?,?,?,?)";
		   pstmt = conn.prepareStatement(query);
		   pstmt.setInt(1, articleNo);
		   pstmt.setInt(2, parentNo);
		   pstmt.setString(3, title);
		   pstmt.setString(4, content);
		   pstmt.setString(5, imageFileName);
		   pstmt.setString(6, id);
		   pstmt.executeUpdate();
		   pstmt.close();
		   conn.close();
	   }catch (Exception e) {
		   System.out.println("새글 추가 중 에러");
		   e.printStackTrace();
	}
	   return articleNo;
   }
   //선택한 글 상세 내용
   public ArticleVO selectArticle(int articleNo) {
	   ArticleVO article = new ArticleVO();
	   try {
		   conn = dataFactory.getConnection();
		   String query="select articleNo, parentNo,title, content, NVL(imageFileName,'null') as imageFileName, id, writeDate from boardtbl where articleNo=?";		   
		   pstmt = conn.prepareStatement(query);
		   pstmt.setInt(1, articleNo);
		   ResultSet rs = pstmt.executeQuery();
		   rs.next();
		   int _articleNo = rs.getInt("articleNo");
		   int parentNo = rs.getInt("parentNo");
		   String title = rs.getString("title");
		   String content = rs.getString("content");
		   String imageFileName = rs.getString("imageFileName");
		   if(imageFileName.equals("null")) {
			   imageFileName=null;
		   }
		   String id = rs.getString("id");
		   Date writeDate = rs.getDate("writeDate");
		   article.setArticleNo(_articleNo);
		   article.setParentNo(parentNo);
		   article.setTitle(title);
		   article.setContent(content);
		   article.setImageFileName(imageFileName);
		   article.setWriteDate(writeDate);
		   article.setId(id);
		   rs.close();
		   pstmt.close();
		   conn.close();
	   }catch (Exception e) {
		System.out.println("글 상세 구현 중 에러");
		e.printStackTrace();
	}
	   return article;
   }
   //글 수정하기 메서드
   public void updateArticle(ArticleVO article) {
	   int articleNo = article.getArticleNo();
	   String title = article.getTitle();
	   String content = article.getContent();
	   String imageFileName = article.getImageFileName();
	   try {
		   conn = dataFactory.getConnection();
		   String query = "update boardtbl set title=?, content=?";
		   if(imageFileName != null && imageFileName.length() != 0) {
			   query+=", imageFileName=?";
		   }
		   query+=" where articleNo=?";
		   pstmt = conn.prepareStatement(query);
		   pstmt.setString(1, title);
		   pstmt.setString(2, content);
		   if(imageFileName != null && imageFileName.length() != 0) {
			   pstmt.setString(3, imageFileName); 
			   pstmt.setInt(4, articleNo);
		   }else { //이미지에 따라 ?가 3개일수도 있고 4개가 될수 있음
			   pstmt.setInt(3, articleNo);
		   }
		   pstmt.executeUpdate();
		   pstmt.close();
		   conn.close();
	   }catch (Exception e) {
		   System.out.println("글 수정 중 에러");
		   e.printStackTrace();
	}
   }
   //삭제할 글 번호 목록 가져오기
   public List<Integer> selectRemovedArticles(int articleNo) {
	   List<Integer> articleNoList =new ArrayList<Integer>();
	   try {
		   conn = dataFactory.getConnection();
		   String query = "select aricleNo from boardtbl START WITH articleNo=?" +
		   " CONNECT BY PRIOR articleNo = parentNo";
		   pstmt = conn.prepareStatement(query);
		   pstmt.setInt(1, articleNo);
		   ResultSet rs = pstmt.executeQuery();
		   while(rs.next()) {
			   articleNo = rs.getInt("articleNo");
			   articleNoList.add(articleNo);
		   }
		   pstmt.close();
		   conn.close();
	   }catch (Exception e) {
		 System.out.println("삭제할 글 번호 목록 가져오기 중 에러");
		 e.printStackTrace();
	}
	   return articleNoList;
   }
   //글 삭제 메서드
   public void deleteArticle(int articleNo) {
	   try {
		   conn = dataFactory.getConnection();
		   String query = "delete from boardtbl where articleNo in (select articleNo" +
		   " from boardtbl START WITH articleNo=? CONNECT BY PRIOR articleNo = parentNo";
		   pstmt = conn.prepareStatement(query);
		   pstmt.setInt(1, articleNo);
		   pstmt.executeUpdate();
		   pstmt.close();
		   conn.close();
	   }catch (Exception e) {
		System.out.println("글 삭제 중 에러");
		e.printStackTrace();
	}
   }
}