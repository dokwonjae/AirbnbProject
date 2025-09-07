<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>

<head>
    <title>연락처 변경</title>
    <link rel="stylesheet" type="text/css" href="/css/header.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<h1>연락처 변경</h1>

<form:form method="post" modelAttribute="contactUpdateRequestDto" action="/account/contact">
    <form:errors cssClass="error" element="div"/>

    <div>
        <label>전화번호</label>
        <form:input path="tel"/>
        <form:errors path="tel" cssClass="error"/>
    </div>

    <div>
        <label>이메일</label>
        <form:input path="email"/>
        <form:errors path="email" cssClass="error"/>
    </div>

    <c:if test="${not empty _csrf}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </c:if>

    <button type="submit">저장</button>
    <a href="/account">취소</a>
</form:form>

</body>
</html>
