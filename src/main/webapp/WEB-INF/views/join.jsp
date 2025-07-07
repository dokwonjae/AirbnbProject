<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <title>회원가입</title>
    <link rel="stylesheet" href="/css/join.css">
</head>
<body>

<div class="main-content">
    <h2 class="title">회원가입</h2>

    <form:form method="post" modelAttribute="joinRequestDto" action="/join">
        <div class="form-group">
            <label for="loginId">아이디</label>
            <form:input path="loginId" id="loginId" cssClass="input"/>
            <form:errors path="loginId" cssClass="error"/>
        </div>

        <div class="form-group">
            <label for="password">비밀번호</label>
            <form:password path="password" id="password" cssClass="input"/>
            <form:errors path="password" cssClass="error"/>
        </div>

        <div class="form-group">
            <label for="email">이메일</label>
            <form:input path="email" id="email" cssClass="input"/>
            <form:errors path="email" cssClass="error"/>
        </div>

        <div class="form-group">
            <label for="tel">전화번호</label>
            <form:input path="tel" id="tel" cssClass="input"/>
            <form:errors path="tel" cssClass="error"/>
        </div>

        <button type="submit">가입하기</button>
    </form:form>

    <c:if test="${not empty msg}">
        <div class="msg">${msg}</div>
    </c:if>
</div>

</body>
</html>
