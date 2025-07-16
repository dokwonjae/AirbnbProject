<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>로그인</title>
    <link rel="stylesheet" href="/css/login.css">
</head>
<body>

<%@ include file="layout/header.jsp" %>

<h2 class="title">로그인</h2>
<form method="post" action="/login">
    <div class="form-group">
        <label for="loginId">아이디</label>
        <input type="text" id="loginId" name="loginId" required>
    </div>
    <div class="form-group">
        <label for="password">비밀번호</label>
        <input type="password" id="password" name="password" required>
    </div>
    <button type="submit">로그인</button>
</form>

<c:if test="${not empty msg}">
    <div class="msg">${msg}</div>
</c:if>

</body>
</html>
