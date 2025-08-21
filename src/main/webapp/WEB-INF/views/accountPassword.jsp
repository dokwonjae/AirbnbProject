<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head><title>비밀번호 변경</title></head>
<body>
<%@ include file="layout/header.jsp" %>

<h1>비밀번호 변경</h1>

<form:form method="post" modelAttribute="passwordChangeRequestDto" action="/account/password">
    <form:errors cssClass="error" element="div"/>

    <div>
        <label>현재 비밀번호</label>
        <form:password path="currentPassword"/>
        <form:errors path="currentPassword" cssClass="error"/>
    </div>

    <div>
        <label>새 비밀번호</label>
        <form:password path="newPassword"/>
        <form:errors path="newPassword" cssClass="error"/>
    </div>

    <div>
        <label>새 비밀번호 확인</label>
        <form:password path="newPasswordConfirm"/>
        <form:errors path="newPasswordConfirm" cssClass="error"/>
    </div>

    <form:errors path="confirmMatches" cssClass="error"/>
    <form:errors path="differentFromCurrent" cssClass="error"/>

    <button type="submit">변경</button>
    <a href="/account">취소</a>
</form:form>
</body>
</html>
