<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>숙소 등록</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/accommodationRegistration.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="form-page">
    <h1 class="page-title">숙소 등록</h1>

    <div class="form-card">
        <c:if test="${not empty msg}">
            <div class="alert">${msg}</div>
        </c:if>

        <form:form method="post" modelAttribute="accommodationRequestDto" action="/accommodation/register" class="form">
            <div class="form-row">
                <label for="name">숙소명</label>
                <form:input path="name" id="name" cssClass="input"/>
                <form:errors path="name" cssClass="error"/>
            </div>

            <div class="form-row">
                <label for="price">가격</label>
                <form:input path="price" id="price" type="number" cssClass="input"/>
                <form:errors path="price" cssClass="error"/>
            </div>

            <div class="form-row">
                <label for="image">이미지 링크</label>
                <form:input path="image" id="image" cssClass="input"/>
                <form:errors path="image" cssClass="error"/>
            </div>

            <div class="actions">
                <button type="submit" class="btn primary">등록하기</button>
                <a href="/account?tab=listings" class="btn">취소</a>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>
