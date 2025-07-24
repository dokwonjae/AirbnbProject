<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>숙소 등록</title>
    <link rel="stylesheet" href="/css/accommodationRegistration.css"/>
</head>
<body>

<div class="container">
    <h1>숙소 등록</h1>

    <!-- 등록 실패 메시지 -->
    <c:if test="${not empty msg}">
        <div class="alert">${msg}</div>
    </c:if>

    <!-- 숙소 등록 폼 -->
    <form:form method="post" modelAttribute="accommodationRequestDto" action="/accommodation/register">

        <label for="name">숙소 이름</label>
        <input type="text" name="name" id="name" required/>

        <label for="price">숙박 가격</label>
        <input type="number" name="price" id="price" required/>

        <label for="view">전망 설명</label>
        <input type="text" name="view" id="view"/>

        <label for="image">대표 이미지 (URL)</label>
        <input type="text" name="image" id="image"/>

        <button type="submit">등록하기</button>
    </form:form>
</div>

</body>
</html>
