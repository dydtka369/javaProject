<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"
    %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<%

   request.setCharacterEncoding("UTF-8");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>답글 쓰기 창</title>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
//이미지 미리보기 구현
	function readImage(input) {
		if(input.files && input.files[0]) {
			let reader = new FileReader();
			reader.onload = function(event) {
				$('#preview').attr('src',event.target.result);
			}
			reader.readAsDataURL(input.files[0]);
		}
	}
	//다른 액션을 submit
	function toList(obj) {
		obj.action="${contextPath}/board/listArticles.do";
		obj.submit();
	} 
</script>
</head>
<body>
	<h2 align ="center">답글쓰기</h2>
	<form name="frmReply" action="${contextPath}/board/addReply.do" method="post" enctype="multipart/form-data">
		<table align ="center">
			<tr>
				<td align="right">글쓴이 :</td>
				<td><input type="text" value="chulsu" disabled="disabled"></td>
			</tr>
			<tr>
				<td align="right">글제목 : </td>
				<td colspan="2"><input type="text" size="50" name="title"></td>
			</tr>
			<tr>
				<td align="right">글내용 : </td>
				<td colspan="2"><textarea rows="10" cols="50" name="content" maxlength="4000"></textarea></td>
			</tr>
			<tr>
				<td align="right">이미지파일첨부 : </td>
				<td><input type="file" name="imageFileName" onchange="readImage(this)"></td>
				<td><img id ="preview" src="#" width="200" height="200"></td>
			</tr>
			<tr>
				<td>
					<input align="right" type="submit" value="답글반영하기">
				</td>
				<td>
					<input aling="left" type="button" value="취소" onclick="toList(this.form)">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>