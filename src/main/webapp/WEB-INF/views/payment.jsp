<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>예약 상세</title>
    <link rel="stylesheet" href="/css/reservation.css" />
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="container">
    <h1>예약 상세</h1>

    <div>
        <p><strong>숙소:</strong> ${reservation.accommodation.name}</p>
        <p><strong>체크인:</strong> ${reservation.checkIn}</p>
        <p><strong>체크아웃:</strong> ${reservation.checkOut}</p>
        <p><strong>가격:</strong> <fmt:formatNumber value="${reservation.accommodation.price}" type="number" />원</p>
        <p><strong>상태:</strong> ${reservation.paymentStatus}</p>
    </div>

    <form method="post" action="/payment/kakao">
        <input type="hidden" name="reservationId" value="${reservation.id}" />
        <input type="hidden" name="amount" value="${reservation.accommodation.price}" />
        <button type="submit" class="btn">카카오페이로 결제</button>
    </form>
</div>

</body>
</html>
