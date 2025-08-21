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

    <!-- ✅ RESERVED 상태에서만 결제 버튼 노출 -->
    <c:if test="${reservation.status == 'RESERVED'}">
        <form method="post" action="/payment/kakao">
            <input type="hidden" name="reservationId" value="${reservation.id}"/>
            <button type="submit" class="btn">카카오페이로 결제</button>
        </form>
    </c:if>

    <!-- 이미 결제 완료면 안내 -->
    <c:if test="${reservation.status == 'PAID'}">
        <div class="alert">이미 결제 완료된 예약입니다.</div>
    </c:if>
</div>

</body>
</html>
