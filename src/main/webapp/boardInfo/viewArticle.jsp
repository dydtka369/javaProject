<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"
isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<%
request.setCharacterEncoding("utf-8");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글 상세 보기</title>
<style type="text/css">
  #tr_button_modify{
      display:none;
  }
  
</style>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
 <script type="text/javascript">
 function backToList(obj){
     obj.action ="${contextPath}/board/listArticles.do";
     obj.submit();
  }
  
  function fn_enable(obj){
     document.getElementById("id_title").disabled=false;
     document.getElementById("id_content").disabled=false;
     let imgName = document.getElementById("id_imgFile");
     if(imgName != null){
        imgName.disabled =false;
     }
     document.getElementById("tr_button_modify").style.display="block";
     document.getElementById("tr_button").style.display="none";
  }
  function fn_modify_article(obj){
     obj.action="${contextPath}/board/modArticle.do";
      obj.submit();
  }
  function readImage(input) {
     if(input.files && input.files[0]) {
        let reader = new FileReader();
        reader.onload = function(event) {
           $('#preview').attr('src',event.target.result);
        }
        reader.readAsDataURL(input.files[0]);
     }
  }
  function toList(obj){
      obj.action ="${contextPath}/board/viewArticle.do?articleNo=${article.articleNo}";
      obj.submit();
  }
</script>
</head>
<body> 
    <form name="frmArticle" action="${contextPath}/board/updateArticle.do" method="post" enctype="multipart/form-data">
    <table align="center">
       <tr>
          <td width="150" align="center" bgcolor="#ffcc00">글번호</td>
          <td><input type="text" name="articleNo" value="${article.articleNo}" disabled></td>
          <input type="hidden" name="articleNo" value="${article.articleNo}">
          </tr>
          <tr>
           <td width="150" align="center" bgcolor="#ffcc00">작성자아이디</td>
           <td><input type="text" name="id" value="${article.id}" disabled></td>
          </tr>
            <tr>
           <td width="150" align="center" bgcolor="#ffcc00">제목</td>
           <td><input type="text" id="id_title" name="title" value="${article.title}" disabled></td>
          </tr>
            <tr>
           <td width="150" align="center" bgcolor="#ffcc00">내용</td>
           <td><textarea  rows="10" cols="50"  name="content" id="id_content" disabled>${article.content}</textarea></td>
          </tr>
          <c:if test="${!empty article.imageFileName}">
            <tr>
            <td width="150" align="center" bgcolor="#ffcc00" rowspan="2">이미지</td>
            <td>
                 <input type="hidden" name="originalFileName" value="${article.imageFileName}">
                 <img id="preview" src="${contextPath}/download.do?imageFileName=${article.imageFileName}&amp;articleNo=${article.articleNo}">
            </td>
            </tr>
            <tr>
                <td><input type="file" id="id_imgFile" name="imageFileName" onchange="readImage(this)" disabled></td>
            </tr>
          </c:if>
          <tr>
            <td width="150" align="center" bgcolor="#ffcc00">등록일자</td>
            <td> <input type="text" value="<fmt:formatDate value='${article.writeDate}'/>" disabled></td>
          </tr>
          <tr id="tr_button_modify">
          <td align="center" colspan="2">
           <input type="button" value="수정 반영하기" onclick="fn_modify_article(frmArticle)">
           <input type="button" value="취소" onclick="toList(frmArticle)">
           </td>
           </tr>
           <tr id="tr_button">
           <td align="center" colspan="2">
           <input type="button" value="수정하기"  onclick="fn_enable(this.form)">
           <input type="button" value="삭제하기"  onclick="fn_remove_article('${contextPath}/board/removeArticle.do',${article.articleNo})">
           <input type="button" value="리스트로 돌아가기" onclick="backToList(this.form)">
           <input type="button" value="답글쓰기"  onclick="fn_reply_form('${contextPath}/board/replyform.do',${article.articleNo})">        
           </td> 
           </tr>         
       </table>
    </form>
</body>
</html>