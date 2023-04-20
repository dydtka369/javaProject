<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"
    %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<%

   request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>글 상세 보기</title>
<script type="text/javascript">
	function backToList(obj){
		obj.action ="${contextPath}/board/listArticles.do";
		obj.submit();
	}
</script>
</head>
<body>
	<form action="frmArticle" action = "${contextPath}" method="post" enctype="multipart/form-data">
		<table align="center">
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">글번호</td>
				<td><input type="text" value="${article.articleNo}" disabled></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">작성자아이디</td>
				<td><input type="text" value="${article.id}" disabled></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">제목</td>
				<td><input type="text" value="${article.title}" disabled></td>
			</tr>
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">작성자아이디</td>
				<td><textarea rows="10" cols="50" disabled>${article.content}</textarea> </td>
			</tr>
			<c:if test="${!empty article.imageFileName}">
				<tr>
					<td width="150" align="center" bgcolor="#ff9933" rowspan="2">이미지</td>
					<td>
						<input type="hidden" name="originalFileName" value="${article.imageFileName }">
						<img src="${contextPath}/download.do?imageFileName=${article.imageFileName}&articleNo=${article.articleNo}">
					</td>
				</tr>
				<tr>
					<td><input type="file" name="imageFileName"></td>
				</tr>
			</c:if>
			<tr>
				<td width="150" align="center" bgcolor="#ff9933">등록일자</td>
				<td><input type="text" value='<fmt:formatDate value="${article.writeDate}"/>' disabled></td>
			</tr>
			<tr>
				<td align="center" colspan="2">
					<input type="button" value="수정하기">
					<input type="button" value="삭제하기">
					<input type="button" value="리스트로 돌아가기" onclick="backToList(this.form)">
					<input type="button" value="답글쓰기">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>