<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>예약 상세</title>
    <link rel="stylesheet" href="/css/reservation.css"/>
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="container">
    <h1>예약 상세</h1>

    <div>
        <p><strong>숙소:</strong> ${reservation.accommodation.name}</p>
        <p>체크인 날짜: ${reservation.checkIn}</p>
        <p>체크아웃 날짜: ${reservation.checkOut}</p>
        <p><strong>총 가격:</strong>
            <fmt:formatNumber value="${reservation.totalAmount}" type="number"/>원
        </p>
        <p><strong>인원:</strong> ${reservation.guestCount}명</p>
        <p><strong>예약 상태:</strong> ${reservation.status}</p>
        <p><strong>총 결제 금액:</strong>
            <fmt:formatNumber value="${reservation.totalAmount}" type="number"/>원
        </p>
    </div>

    <form method="post" action="/payment/kakao">
        <input type="hidden" name="reservationId" value="${reservation.id}"/>
        <input type="hidden" name="amount" value="${reservation.totalAmount}"/>
        <button type="submit" class="btn">카카오페이로 결제</button>
    </form>
</div>

</body>
</html>
