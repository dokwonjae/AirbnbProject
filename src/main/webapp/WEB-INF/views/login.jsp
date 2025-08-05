<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>로그인</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/login.css">
</head>
<body>

<%@ include file="layout/header.jsp" %>

<div class="login-container">
    <div class="login-box">
        <h2 class="login-title">로그인</h2>
        <form method="post" action="/login">
            <div class="login-form-group">
                <label class="login-label" for="loginId">아이디</label>
                <input class="login-input" type="text" id="loginId" name="loginId" required>
            </div>
            <div class="login-form-group">
                <label class="login-label" for="password">비밀번호</label>
                <input class="login-input" type="password" id="password" name="password" required>
            </div>
            <button class="login-button" type="submit">로그인</button>
        </form>
    </div>
</div>

</body>
</html>
