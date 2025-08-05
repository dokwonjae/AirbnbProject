<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>예약 상세정보</title>
    <link rel="stylesheet" href="/css/myPage.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<h1>예약 상세정보</h1>

<section class="mypage-section">
    <p><strong>숙소명:</strong> ${reservation.accommodation.name}</p>
    <p><strong>체크인:</strong> ${reservation.checkIn}</p>
    <p><strong>체크아웃:</strong> ${reservation.checkOut}</p>
    <p><strong>인원:</strong> ${reservation.guestCount}명</p>
    <p><strong>결제 상태:</strong> ${reservation.paymentStatus}</p>
    <p><strong>결제 금액:</strong> ${reservation.totalPrice}원</p>
</section>

<a href="/mypage">← 마이페이지로 돌아가기</a>

</body>
</html>
