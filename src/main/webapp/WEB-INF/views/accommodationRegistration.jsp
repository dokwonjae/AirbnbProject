<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>숙소 등록</title>
    <link rel="stylesheet" href="/css/accommodationRegistration.css"/>
</head>
<body>
<%@ include file="layout/header.jsp" %>


<div class="container">
    <h1>숙소 등록</h1>

    <!-- 등록 실패 메시지 -->
    <c:if test="${not empty msg}">
        <div class="alert">${msg}</div>
    </c:if>


    <form:form method="post" modelAttribute="accommodationRequestDto" action="/accommodation/register">
        <label for="name">숙소명</label>
        <form:input path="name" id="name"/>
        <form:errors path="name" cssClass="error"/>

        <label for="price">가격</label>
        <form:input path="price" id="price" type="number"/>
        <form:errors path="price" cssClass="error"/>

        <label for="view">전망 설명</label>
        <form:input path="view" id="view"/>
        <form:errors path="view" cssClass="error"/>

        <label for="image">이미지</label>
        <form:input path="image" id="image"/>
        <form:errors path="image" cssClass="error"/>

        <button type="submit">등록하기</button>
    </form:form>
</div>

</body>
</html>
