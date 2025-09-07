<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>예약 상세</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/reservation.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="reservation-page">
    <h1 class="page-title">예약 상세</h1>

    <!-- 플래시 메시지 -->
    <c:if test="${not empty msg}">
        <div class="toast">${msg}</div>
    </c:if>

    <div class="reservation-card">
        <!-- 좌측: 예약 요약 -->
        <div class="reservation-left">
            <!-- 썸네일이 없으므로 심플한 플레이스홀더 -->
            <img class="reservation-img"
                 src="${reservation.image}"
                 alt="숙소 이미지">

            <h2 class="accommodation-name">
                <c:out value="${reservation.accommodationName}"/>
            </h2>

            <div class="price-line">
                <span class="label">총 금액</span>
                <strong class="price">
                    ₩<fmt:formatNumber value="${reservation.totalAmount}" type="number"/>
                </strong>
            </div>
        </div>

        <!-- 우측: 상세 정보 + 액션 -->
        <div class="reservation-right">
            <h3 class="section-title">예약 정보</h3>
            <div class="kv">
                <span class="k">체크인</span>
                <span class="v"><c:out value="${reservation.checkIn}"/></span>
            </div>
            <div class="kv">
                <span class="k">체크아웃</span>
                <span class="v"><c:out value="${reservation.checkOut}"/></span>
            </div>
            <div class="kv">
                <span class="k">인원</span>
                <span class="v"><c:out value="${reservation.guestCount}"/>명</span>
            </div>
            <div class="kv">
                <span class="k">상태</span>
                <span class="v status ${reservation.status}"><c:out value="${reservation.status}"/></span>
            </div>

            <!-- 버튼 영역 -->
            <div class="btn-group">
                <!-- RESERVED: 결제 & 취소 -->
                <c:if test="${reservation.status == 'RESERVED'}">
                    <form action="/payment/kakao" method="post">
                        <input type="hidden" name="reservationId" value="${reservation.id}">
                        <button type="submit" class="btn btn-primary">카카오페이로 결제</button>
                    </form>

                    <form action="/reservation/${reservation.id}/cancel" method="post">
                        <button type="submit" class="btn btn-danger">예약 취소</button>
                    </form>
                </c:if>

                <!-- PAID: 환불(취소)만 -->
                <c:if test="${reservation.status == 'PAID'}">
                    <div class="hint">결제가 완료된 예약입니다.</div>
                    <form action="/reservation/${reservation.id}/cancel" method="post">
                        <button type="submit" class="btn btn-danger">예약 & 결제 취소</button>
                    </form>
                </c:if>

                <!-- CANCELED: 안내만 -->
                <c:if test="${reservation.status == 'CANCELED'}">
                    <div class="hint">이미 취소된 예약입니다.</div>
                </c:if>
            </div>
        </div>
    </div>
</div>

</body>
</html>
