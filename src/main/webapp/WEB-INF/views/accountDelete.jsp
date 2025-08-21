<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<html>
<head><title>회원 탈퇴</title></head>
<body>
<%@ include file="layout/header.jsp" %>

<h1>회원 탈퇴</h1>
<p>탈퇴 시 계정은 복구할 수 없으며, 예약/결제 기록은 법적·회계상 사유로 익명화된 상태로 보존됩니다.</p>

<form:form method="post" modelAttribute="accountDeleteRequestDto" action="/account/delete">
    <form:errors cssClass="error" element="div"/>

    <div>
        <label>현재 비밀번호</label>
        <form:password path="currentPassword"/>
        <form:errors path="currentPassword" cssClass="error"/>
    </div>

    <c:if test="${not empty _csrf}">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </c:if>

    <button type="submit" style="color:#fff;background:#d33;">정말 탈퇴하기</button>
    <a href="/account">취소</a>
</form:form>
</body>
</html>
