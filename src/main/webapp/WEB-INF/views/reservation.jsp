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
        <p><strong>숙소명:</strong> <c:out value="${reservation.accommodationName}"/></p>
        <p><strong>체크인:</strong> <c:out value="${reservation.checkIn}"/></p>
        <p><strong>체크아웃:</strong> <c:out value="${reservation.checkOut}"/></p>
        <p><strong>인원:</strong> <c:out value="${reservation.guestCount}"/>명</p>
        <p><strong>총 금액:</strong>
            <fmt:formatNumber value="${reservation.totalAmount}" type="number"/>원
        </p>
        <p><strong>상태:</strong> <c:out value="${reservation.status}"/></p>
    </div>

    <c:if test="${reservation.status == 'RESERVED'}">
        <form method="post" action="/payment/kakao">
            <input type="hidden" name="reservationId" value="${reservation.id}"/>
            <button type="submit" class="btn">카카오페이로 결제</button>
        </form>

        <form method="post" action="/reservation/${reservation.id}/cancel">
            <input type="hidden" name="reservationId" value="${reservation.id}"/>
            <button type="submit" class="btn cancel">예약 취소</button>
        </form>
    </c:if>

    <!-- ✅ PAID 상태: 결제 취소 + 예약 취소 버튼 -->
    <c:if test="${reservation.status == 'PAID'}">
        <div class="alert">이미 결제 완료된 예약입니다.</div>
        <form method="post" action="/reservation/${reservation.id}/cancel">
            <input type="hidden" name="reservationId" value="${reservation.id}"/>
            <button type="submit" class="btn cancel">예약 & 결제 취소</button>
        </form>
    </c:if>

    <!-- ✅ CANCELED 상태 -->
    <c:if test="${reservation.status == 'CANCELED'}">
        <div class="alert">이미 취소된 예약입니다.</div>
    </c:if>

</div>

</body>
</html>
