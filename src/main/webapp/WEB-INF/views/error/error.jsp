<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8" />
    <title>요청을 처리할 수 없습니다</title>
    <link rel="stylesheet" href="/css/error.css">
</head>
<body>
<main class="error-page">
    <section class="error-card">
        <div class="error-emoji">🙇</div>
        <h1 class="error-title">요청을 처리할 수 없습니다</h1>
        <p class="error-desc">요청을 처리하는 중 오류가 발생했습니다.</p>

        <ul class="error-meta">
            <li><b>상태 코드</b> <span><c:out value="${status}"/></span></li>
            <c:if test="${not empty message}">
                <li><b>메시지</b> <span><c:out value="${message}"/></span></li>
            </c:if>
            <c:if test="${not empty path}">
                <li><b>경로</b> <span><c:out value="${path}"/></span></li>
            </c:if>
        </ul>

        <div class="error-actions">
            <a class="btn" href="/">메인으로</a>
            <a class="btn outline" href="javascript:history.back()">이전 페이지</a>
        </div>
    </section>
</main>
</body>
</html>
