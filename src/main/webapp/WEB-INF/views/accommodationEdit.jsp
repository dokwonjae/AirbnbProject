<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>숙소 정보 수정</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/accommodationEdit.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="form-page">
    <h1 class="page-title">숙소 정보 수정</h1>

    <div class="form-card">
        <c:if test="${not empty msg}">
            <div class="alert">${msg}</div>
        </c:if>

        <form:form method="post" action="/accommodation/update" modelAttribute="accommodationRequestDto" class="form">
            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
            <input type="hidden" name="id" value="${accommodation.id}"/>

            <div class="form-row">
                <label>숙소명</label>
                <form:input path="name" cssClass="input"/>
                <form:errors path="name" cssClass="error"/>
            </div>

            <div class="form-row">
                <label>가격</label>
                <form:input path="price" type="number" cssClass="input"/>
                <form:errors path="price" cssClass="error"/>
            </div>

            <div class="form-row">
                <label>이미지 링크</label>
                <form:input path="image" cssClass="input"/>
                <form:errors path="image" cssClass="error"/>
            </div>

            <div class="actions">
                <button type="submit" class="btn primary">수정 완료</button>
                <a href="/account?tab=listings" class="btn">취소</a>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>
