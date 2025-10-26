<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>페이지를 찾을 수 없습니다</title>
    <link rel="stylesheet" href="/css/error.css">
    <link rel="stylesheet" href="/css/404.css">
</head>
<body>

<main class="error-page">
    <section class="error-card">
        <div class="error-emoji">🔎</div>
        <h1 class="error-title">요청하신 페이지를 찾을 수 없습니다</h1>
        <p class="error-desc">주소가 바뀌었거나, 삭제되었을 수 있어요.</p>

        <ul class="error-meta">
            <li><b>요청 경로</b> <span><c:out value="${path}"/></span></li>
            <li><b>상태 코드</b> <span><c:out value="${status}"/></span></li>
            <c:if test="${not empty message}">
                <li><b>메시지</b> <span><c:out value="${message}"/></span></li>
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
