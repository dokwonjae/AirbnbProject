<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>${info.title} - 숙소 상세</title>
    <link rel="stylesheet" type="text/css" href="/css/accommodationInfo.css">
</head>

<script>
    document.querySelector("#reservationForm").addEventListener("submit", function(e) {
        const checkInInput = document.querySelector("input[name='checkIn']");
        const checkOutInput = document.querySelector("input[name='checkOut']");

        const checkIn = new Date(checkInInput.value);
        const checkOut = new Date(checkOutInput.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0); // 시간 제거

        if (checkIn < today) {
            alert("체크인 날짜는 오늘 이후여야 합니다.");
            e.preventDefault();
            return;
        }

        if (checkOut <= checkIn) {
            alert("체크아웃 날짜는 체크인 날짜 이후여야 합니다.");
            e.preventDefault();
            return;
        }

        if (checkOut < today) {
            alert("체크아웃 날짜는 오늘 이후여야 합니다.");
            e.preventDefault();
        }
    });
</script>


<body>
<%@ include file="layout/header.jsp" %>

<c:if test="${param.error == 'infoExists'}">
    <script>alert('이미 등록된 상세정보가 있는 숙소입니다.');</script>
</c:if>

<div class="container">

    <c:if test="${info == null}">
        <c:if test="${sessionScope.user != null
                 && sessionScope.user.id == accommodation.user.id
                 && accommodation.status == 'PENDING'}">
            <a href="/info/register?accommodationId=${accommodation.id}" class="btn">상세정보 등록</a>
        </c:if>
    </c:if>


    <!-- ✅ 수정/삭제 버튼: 로그인한 사용자 == 숙소 등록자일 때만 노출 -->
    <c:if test="${sessionScope.user != null && sessionScope.user.id == accommodation.user.id}">
        <div style="margin-top: 20px;">
            <form method="get" action="/info/edit/${info.id}" style="display:inline;">
                <button type="submit">수정</button>
            </form>
            <form method="post" action="/info/delete/${info.id}" onsubmit="return confirm('정말 삭제하시겠습니까?');"
                  style="display:inline;">
                <button type="submit">삭제</button>
            </form>
        </div>
    </c:if>


    <c:if test="${info != null}">

        <h1>${info.title}</h1>
        <div class="subtitle">${info.location}</div>

        <div class="detail-grid">

                <%-- 🖼️ 이미지 갤러리 --%>
            <div class="image-gallery">
                <c:forEach var="img" items="${imagePaths}">
                    <c:choose>
                        <c:when test="${fn:startsWith(img, 'data:image/')}">
                            <img src="${img}" alt="숙소 이미지"/>
                        </c:when>
                        <c:otherwise>
                            <img src="${img}" alt="숙소 이미지"/>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>

                <%-- 📝 텍스트 정보 --%>
            <div class="accommodation-info">
                <div><span class="info-label">부제목:</span>${info.subTitle}</div>
                <div><span class="info-label">인원:</span>${info.personnel}</div>
                <div><span class="info-label">가격:</span><fmt:formatNumber value="${accommodation.price}" type="number"/>원
                </div>
                <div><span class="info-label">뷰:</span>${accommodation.view}</div>
            </div>
        </div>

        <%-- 🧺 편의시설 섹션 --%>
        <div class="info-section">
            <h3>편의시설</h3>
            <p>${info.amenities}</p>
        </div>

        <%-- 🌍 위치 정보 --%>
        <div class="info-section">
            <h3>위치</h3>
            <p>${info.location}, 제주도, 한국</p>
        </div>

        <!-- ✅ 예약 영역: 숙소 상태 APPROVED이고 로그인한 사용자만 예약 가능 -->
        <c:if test="${sessionScope.user != null && accommodation.status == 'APPROVED'}">
            <div class="reservation-box">
                <form id="reservationForm" action="/reservation" method="post">
                    <input type="hidden" name="accommodationId" value="${accommodation.id}">

                    <label>체크인 날짜</label>
                    <input type="date" name="checkIn" required>

                    <label>체크아웃 날짜</label>
                    <input type="date" name="checkOut" required>

                    <label>인원수</label>
                    <input type="number" name="guestCount" min="1" required>

                    <button type="submit">예약하기</button>
                </form>

            </div>
        </c:if>

    </c:if>

</div>

</body>
</html>
