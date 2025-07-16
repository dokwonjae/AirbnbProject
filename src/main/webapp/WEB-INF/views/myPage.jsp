<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>마이페이지</title>
    <link rel="stylesheet" href="/css/myPage.css">
</head>
<body>
<h1>마이페이지</h1>

<!-- 1. 개인정보 -->
<section class="mypage-section">
    <h2>👤 개인정보</h2>
<%--    <p><strong>이름:</strong> ${user.name}</p>--%>
    <p><strong>아이디:</strong> ${user.loginId}</p>
    <p><strong>이메일:</strong> ${user.email}</p>
<%--    <p><strong>전화번호:</strong> ${user.phone}</p>--%>
    <!-- 추후 수정 폼 추가 가능 -->
</section>


<section class="mypage-section">
    <h2>📅 예약 내역</h2>
    <table>
        <thead>
        <tr>
            <th>숙소명</th>
            <th>날짜</th>
            <th>상태</th>
        </tr>
        </thead>
    </table>
</section>


<section class="mypage-section">
    <h2>🏠 숙소 관리</h2>
    <a class="register-btn" href="/accommodation/register">+ 새 숙소 등록</a>
    <table>
        <thead>
        <tr>
            <th>숙소명</th>
            <th>가격</th>
            <th>뷰</th>
            <th>수정</th>
            <th>삭제</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="ac" items="${myAccommodations}">
            <tr>
                <td>${ac.name}</td>
                <td>${ac.price}</td>
                <td>${ac.view}</td>
                <td><a href="/accommodation/edit?id=${ac.id}">수정</a></td>
                <td>
                    <form method="post" action="/accommodation/delete" onsubmit="return confirm('정말 삭제할까요?');">
                        <input type="hidden" name="id" value="${ac.id}" />
                        <button type="submit">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>
</body>
</html>
