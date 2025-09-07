<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>비밀번호 변경</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/passwordChange.css"><!-- 페이지 전용 -->
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="form-page">
    <h1 class="page-title">비밀번호 변경</h1>

    <div class="form-card">
        <form:form method="post" modelAttribute="passwordChangeRequestDto" action="/account/password" cssClass="form">
            <!-- 폼 상단 공통 에러 -->
            <form:errors cssClass="alert" element="div"/>

            <div class="form-row">
                <label for="curPwd">현재 비밀번호</label>
                <form:password path="currentPassword" id="curPwd" cssClass="input"/>
                <form:errors path="currentPassword" cssClass="error"/>
            </div>

            <div class="form-row">
                <label for="newPwd">새 비밀번호</label>
                <form:password path="newPassword" id="newPwd" cssClass="input"/>
                <form:errors path="newPassword" cssClass="error"/>
            </div>

            <div class="form-row">
                <label for="newPwd2">새 비밀번호 확인</label>
                <form:password path="newPasswordConfirm" id="newPwd2" cssClass="input"/>
                <form:errors path="newPasswordConfirm" cssClass="error"/>
            </div>

            <!-- 커스텀 오브젝트 에러 -->
            <form:errors path="confirmMatches" cssClass="error"/>
            <form:errors path="differentFromCurrent" cssClass="error"/>

            <div class="actions">
                <button type="submit" class="btn primary">변경</button>
                <a href="/account" class="btn">취소</a>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>
