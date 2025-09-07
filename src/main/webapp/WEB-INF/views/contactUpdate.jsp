<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>연락처 변경</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/contactUpdate.css"><!-- 페이지 전용 -->
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="form-page">
    <h1 class="page-title">연락처 변경</h1>

    <div class="form-card">
        <form:form method="post" modelAttribute="contactUpdateRequestDto" action="/account/contact" cssClass="form">
            <form:errors cssClass="alert" element="div"/>

            <div class="form-row">
                <label for="tel">전화번호</label>
                <form:input path="tel" id="tel" cssClass="input"/>
                <form:errors path="tel" cssClass="error"/>
            </div>

            <div class="form-row">
                <label for="email">이메일</label>
                <form:input path="email" id="email" cssClass="input"/>
                <form:errors path="email" cssClass="error"/>
            </div>

            <c:if test="${not empty _csrf}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </c:if>

            <div class="actions">
                <button type="submit" class="btn primary">저장</button>
                <a href="/account" class="btn">취소</a>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>
