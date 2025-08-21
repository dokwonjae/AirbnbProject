<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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

        <c:if test="${not empty loginError}">
            <div class="msg">${loginError}</div>
        </c:if>

        <c:if test="${not empty msg}">
            <script>alert("${msg}");</script>
        </c:if>

        <form:form modelAttribute="loginRequestDto" method="post" action="/login">
            <div class="login-form-group">
                <label class="login-label" for="loginId">아이디</label>
                <form:input path="loginId" cssClass="login-input" id="loginId"/>
                <form:errors path="loginId" cssClass="error-msg"/>
            </div>
            <div class="login-form-group">
                <label class="login-label" for="password">비밀번호</label>
                <form:password path="password" cssClass="login-input" id="password"/>
                <form:errors path="password" cssClass="error-msg"/>
            </div>
            <button class="login-button" type="submit">로그인</button>
        </form:form>
    </div>
</div>

</body>
</html>
