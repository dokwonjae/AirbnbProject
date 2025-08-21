<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>${info.title} - 숙소 상세</title>
    <link rel="stylesheet" type="text/css" href="/css/accommodationInfo.css">

    <!-- 달력(Flatpickr) -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
</head>

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

    <!-- 수정/삭제 버튼 -->
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
            <div class="image-gallery">
                <c:forEach var="img" items="${imagePaths}">
                    <img src="${img}" alt="숙소 이미지" loading="lazy"/>
                </c:forEach>
            </div>

                <%-- 텍스트 정보 --%>
            <div class="accommodation-info">
                <div><span class="info-label">부제목:</span>${info.subTitle}</div>
                <div><span class="info-label">인원:</span>${info.personnel}</div>
                <div><span class="info-label">가격:</span><fmt:formatNumber value="${accommodation.price}" type="number"/>원</div>
                <div><span class="info-label">뷰:</span>${accommodation.view}</div>
            </div>
        </div>

        <%-- 편의시설 --%>
        <div class="info-section">
            <h3>편의시설</h3>
            <p>${info.amenities}</p>
        </div>

        <%-- 위치 --%>
        <div class="info-section">
            <h3>위치</h3>
            <p>${info.location}, 제주도, 한국</p>
        </div>

        <!-- 예약 영역: 승인된 숙소만 -->
        <c:if test="${accommodation.status == 'APPROVED'}">
            <div class="reservation-box">

                <!-- ✅ 글로벌 에러(서비스/컨트롤러에서 reject(...)한 것) -->

                <!-- ✅ Spring Form으로 변경 -->
                <form:form id="reservationForm" method="post" action="/reservation" modelAttribute="reservationRequestDto">

                    <!-- 글로벌/객체 에러 -->
                    <form:errors cssClass="error" element="div"/>
                    <form:errors path="dateRangeValid" cssClass="error"/>

                    <form:hidden path="accommodationId"/>
                    <form:hidden path="checkIn"  id="checkInHidden"/>
                    <form:hidden path="checkOut" id="checkOutHidden"/>

                    <label>예약 날짜</label>
                    <input id="dateRange" type="text" placeholder="체크인 ~ 체크아웃"/>

                    <!-- 날짜 필드 에러 -->
                    <form:errors path="checkIn"  cssClass="error"/>
                    <form:errors path="checkOut" cssClass="error"/>

                    <label>인원수</label>
                    <form:input path="guestCount" type="number" min="1"/>
                    <form:errors path="guestCount" cssClass="error"/>

                    <button type="submit">예약하기</button>
                </form:form>

            </div>
        </c:if>
    </c:if>
</div>

<script>
    const ACC_ID = "${accommodation.id}";

    const fp = flatpickr("#dateRange", {
        mode: "range",
        minDate: "today",
        dateFormat: "Y-m-d",
        onChange(selectedDates, dateStr, instance) {
            if (selectedDates.length === 2) {
                const fmt = d => instance.formatDate(d, "Y-m-d");
                document.getElementById("checkInHidden").value  = fmt(selectedDates[0]);
                document.getElementById("checkOutHidden").value = fmt(selectedDates[1]);
            }
        },
        // ✅ 반드시 instance를 넘겨 호출해야 함
        onReady(_, __, instance)       { refreshDisabled(instance); },
        onMonthChange(_, __, instance) { refreshDisabled(instance); },
        onYearChange(_, __, instance)  { refreshDisabled(instance); }
    });

    async function refreshDisabled(instance){
        const y = instance.currentYear;
        const m = instance.currentMonth; // 0~11
        const from = new Date(y, m, 1);
        const to   = new Date(y, m + 2, 0); // 다음 달 말일

        const toIso = d => new Date(d.getTime() - d.getTimezoneOffset()*60000)
            .toISOString().slice(0,10);

        const url = '/reservation/accommodation/' + ACC_ID
            + '/booked?from=' + toIso(from)
            + '&to=' + toIso(to);

        try {
            const res = await fetch(url);
            const ranges = await res.json();  // [{from:"2025-08-11",to:"2025-08-12"}, ...]

            // 👉 범위를 하루 단위 배열로 풀어서 확실히 비활성화
            const disabledDays = [];
            ranges.forEach(r => {
                let cur = new Date(r.from + 'T00:00:00');
                const end = new Date(r.to   + 'T00:00:00');
                while (cur <= end) {
                    const y = cur.getFullYear();
                    const m = String(cur.getMonth()+1).padStart(2,'0');
                    const d = String(cur.getDate()).padStart(2,'0');
                    disabledDays.push(y + '-' + m + '-' + d); // ✅ 혹은 [''+y, m, d].join('-')
                    cur.setDate(cur.getDate()+1);
                }
            });

            instance.set('disable', disabledDays);
            console.log('disabledDays', disabledDays);
        } catch (e) {
            console.warn('예약 불가 날짜 불러오기 실패', e);
        }
    }
</script>



</body>
</html>
