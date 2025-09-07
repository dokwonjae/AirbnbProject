<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>회원 탈퇴</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/accountDelete.css"><!-- 페이지 전용 -->
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="form-page">
    <h1 class="page-title">회원 탈퇴</h1>

    <div class="form-card danger">
        <p class="desc">
            탈퇴 시 계정은 복구할 수 없으며, 예약/결제 기록은 법적·회계상 사유로
            <strong>익명화된 상태</strong>로 보존됩니다.
        </p>

        <form:form method="post" modelAttribute="accountDeleteRequestDto" action="/account/delete" cssClass="form">
            <form:errors cssClass="alert" element="div"/>

            <div class="form-row">
                <label for="pwd">현재 비밀번호</label>
                <form:password path="currentPassword" id="pwd" cssClass="input"/>
                <form:errors path="currentPassword" cssClass="error"/>
            </div>

            <c:if test="${not empty _csrf}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </c:if>

            <div class="actions">
                <button type="submit" class="btn danger">정말 탈퇴하기</button>
                <a href="/account" class="btn">취소</a>
            </div>
        </form:form>
    </div>
</div>
</body>
</html>
