<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>관리자 | 회원 예약 내역</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/userReservations.css">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="user-resv">

    <div class="admin-page">
        <h1 class="page-title">회원 예약 내역</h1>

        <div class="pane-card">
            <div class="pane-title-row">
                <h2 class="pane-title">
                    <c:out value="${targetUser.loginId}"/> (<c:out value="${targetUser.email}"/>)
                </h2>
                <a class="btn outline" href="/admin?tab=users">← 회원 목록으로</a>
            </div>

            <c:choose>
                <c:when test="${empty reservations}">
                    <p class="muted">예약 내역이 없습니다.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="tbl">
                            <thead>
                            <tr>
                                <th>예약ID</th>
                                <th>숙소명</th>
                                <th>체크인</th>
                                <th>체크아웃</th>
                                <th>상태</th>
                                <th>상세</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="r" items="${reservations}">
                                <tr>
                                    <td>#<c:out value="${r.id}"/></td>
                                    <td class="ellipsis"><c:out value="${r.accommodationName}"/></td>
                                    <td>${r.checkIn.format(dtf)}</td>
                                    <td>${r.checkOut.format(dtf)}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${r.status == 'PAID'}"><span
                                                    class="badge ok">예약 완료</span></c:when>
                                            <c:when test="${r.status == 'RESERVED'}"><span
                                                    class="badge warn">결제 대기 중</span></c:when>
                                            <c:when test="${r.status == 'CANCELLED'}"><span class="badge danger">취소됨</span></c:when>
                                            <c:otherwise><span class="badge"><c:out
                                                    value="${r.status}"/></span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a class="link" href="/reservation/${r.id}" target="_blank">열기</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</div>

</body>
</html>
