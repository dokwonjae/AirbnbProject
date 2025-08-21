<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <title>숙소 수정</title>
    <link rel="stylesheet" href="/css/accommodationEdit.css"/>
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="container">
    <h1>숙소 정보 수정</h1>

    <c:if test="${not empty msg}">
        <div class="alert">${msg}</div>
    </c:if>

    <form:form method="post"
               action="/accommodation/update"
               modelAttribute="accommodationRequestDto">
        <input type="hidden" name="id" value="${accommodation.id}"/>

        <label>숙소명</label>
        <form:input path="name"/>
        <form:errors path="name" cssClass="error"/>

        <label>가격</label>
        <form:input path="price" type="number"/>
        <form:errors path="price" cssClass="error"/>

        <label>전망 설명</label>
        <form:input path="view"/>
        <form:errors path="view" cssClass="error"/>

        <label>이미지 링크</label>
        <form:input path="image"/>

        <button type="submit" class="submit-btn">수정 완료</button>
    </form:form>
</div>
</body>
</html>
