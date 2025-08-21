<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>마이페이지</title>
    <link rel="stylesheet" href="/css/myPage.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<h1>마이페이지</h1>

<!-- 1. 개인정보 -->
<section class="mypage-section">
    <h2>👤 개인정보</h2>
    <p><strong>아이디:</strong> ${user.loginId}</p>
    <p><strong>이메일:</strong> ${user.email}</p>
</section>

<section class="mypage-section">
    <h2>⚙️ 계정 관리</h2>

    <div style="margin-bottom:12px;">
        <a class="register-btn" href="/account/contact">연락처 변경</a>
        <a class="register-btn" href="/account/password">비밀번호 변경</a>
    </div>

    <form method="get" action="/account/delete">
        <button type="submit" class="danger-btn">회원 탈퇴</button>
    </form>
</section>

<!-- 2. 예약 내역 -->
<section class="mypage-section">
    <h2>📅 예약 내역</h2>
    <table>
        <thead>
        <tr>
            <th>숙소명</th>
            <th>체크인 ~ 체크아웃</th>
            <th>결제 상태</th>
            <th>상세 보기</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="reservation" items="${myReservations}">
            <tr>
                <td>${reservation.accommodationName}</td>
                <td>
                    <c:out value="${reservation.checkIn}" /> ~
                    <c:out value="${reservation.checkOut}" />
                </td>
                <td>${reservation.status}</td>
                <td><a href="/reservation/${reservation.id}">상세 보기</a></td>
            </tr>
        </c:forEach>

        </tbody>
    </table>
</section>

<!-- 3. 숙소 관리 -->
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
                    <c:choose>
                        <c:when test="${ac.status == 'APPROVED'}">
                            <form method="post" action="/accommodation/delete" onsubmit="return confirm('숙소 삭제 요청을 하시겠습니까?');">
                                <input type="hidden" name="id" value="${ac.id}" />
                                <button type="submit">삭제 요청</button>
                            </form>
                        </c:when>
                        <c:when test="${ac.status == 'DELETE_REQUESTED'}">
                            <span style="color: orange;">삭제 요청됨 (관리자 승인 대기)</span>
                        </c:when>
                        <c:otherwise>
                            <span>삭제 불가</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>

</body>
</html>
