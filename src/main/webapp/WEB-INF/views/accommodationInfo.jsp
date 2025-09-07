<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title><c:out value="${info != null ? info.title : accommodation.name}"/> - 숙소 상세</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/auth.css"><!-- (모달 사용 시) -->
    <link rel="stylesheet" href="/css/accommodationInfo.css">

    <!-- Flatpickr (예약 달력) -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr/dist/plugins/rangePlugin.js"></script>
</head>
<body>
<%@ include file="layout/header.jsp" %>

<c:if test="${param.error == 'infoExists'}">
    <script>alert('이미 등록된 상세정보가 있는 숙소입니다.');</script>
</c:if>

<div class="page">
    <!-- ===== 타이틀 & 위치 ===== -->
    <div class="title-row">
        <div>
            <h1 class="title">
                <c:out value="${info != null ? info.title : accommodation.name}"/>
            </h1>
            <div class="sub">
                <c:out value="${info != null ? info.location : '위치 정보 없음'}"/>
            </div>
        </div>

        <!-- 소유자 도구 -->
        <c:if test="${sessionScope.user != null && sessionScope.user.id == accommodation.user.id}">
            <div class="owner-actions">
                <!-- 상세가 있을 때: 수정/삭제 -->
                <c:if test="${info != null}">
                    <form method="get" action="/info/edit/${info.id}">
                        <button type="submit" class="btn-outline">수정</button>
                    </form>
                    <form method="post" action="/info/delete/${info.id}"
                          onsubmit="return confirm('정말 삭제하시겠습니까?');">
                        <button type="submit" class="btn-outline danger">삭제</button>
                    </form>
                </c:if>
            </div>
        </c:if>
    </div>

    <!-- ===== 히어로 갤러리 ===== -->
    <c:if test="${not empty imagePaths}">
        <div class="hero-grid">
            <div class="hero-left">
                <img src="${imagePaths[0]}" alt="대표 이미지">
            </div>

            <div class="hero-right">
                <c:forEach var="img" items="${imagePaths}" varStatus="st">
                    <c:if test="${st.index >= 1 && st.index <= 4}">
                        <img src="${img}" alt="숙소 이미지 ${st.index+1}">
                    </c:if>
                </c:forEach>

                <!-- 오른쪽 칸이 4장보다 모자를 때 빈칸 맞춤 -->
                <c:forEach begin="${fn:length(imagePaths) < 5 ? fn:length(imagePaths) : 5}" end="4" varStatus="k">
                    <div class="hero-placeholder"></div>
                </c:forEach>
            </div>

            <c:if test="${fn:length(imagePaths) > 5}">
                <a class="hero-more" href="#photos">사진 모두 보기</a>
            </c:if>
        </div>
    </c:if>

    <!-- ===== 본문 2단 ===== -->
    <div class="content-grid">
        <!-- 왼쪽: 상세 -->
        <div class="left-col">
            <!-- 숙소 정보 -->
            <section class="section">
                <h3 class="h3">숙소 정보</h3>
                <c:choose>
                    <c:when test="${info != null}">
                        <ul class="kv">
                            <li><span class="v"><c:out value="${info.subTitle}"/></span></li>
                            <li><span class="k">최대 인원</span><span class="v"><c:out value="${info.personnel}"/>명</span></li>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <div class="muted">등록된 상세정보가 없습니다.</div>
                    </c:otherwise>
                </c:choose>
            </section>

            <!-- 편의시설 -->
            <section class="section">
                <h3 class="h3">숙소 편의시설</h3>
                <c:choose>
                    <c:when test="${info != null && not empty info.amenities}">
                        <ul class="amenity-list">
                            <c:forTokens items="${info.amenities}" delims=",|" var="am0">
                                <c:set var="key" value="${fn:trim(am0)}"/>
                                <li class="amenity">
                                    <img class="amenity-icon"
                                         src="/icons/amenities/${key}.svg"
                                         alt="${key}"
                                         onerror="this.style.display='none'"/>
                                    <span class="amenity-label">
                                        <c:choose>
                                            <c:when test="${key=='wifi'}">와이파이</c:when>
                                            <c:when test="${key=='pool'}">수영장</c:when>
                                            <c:when test="${key=='tv'}">TV</c:when>
                                            <c:when test="${key=='kitchenTool'}">주방 도구</c:when>
                                            <c:when test="${key=='washMachine'}">세탁기</c:when>
                                            <c:when test="${key=='selfCheckIn'}">셀프 체크인</c:when>
                                            <c:when test="${key=='park'}">국립공원/공원 근처</c:when>
                                            <c:otherwise><c:out value="${key}"/></c:otherwise>
                                        </c:choose>
                                    </span>
                                </li>
                            </c:forTokens>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <div class="muted">등록된 편의시설 정보가 없습니다.</div>
                    </c:otherwise>
                </c:choose>
            </section>

            <!-- 호스트 정보 -->
            <section class="section host-block">
                <div class="host-row">
                    <div class="host-meta">
                        <div class="host-name">호스트: <c:out value="${accommodation.user.loginId}"/> 님</div>
                    </div>
                </div>
            </section>

            <!-- 상세 설명 -->
            <c:if test="${info != null && not empty info.description}">
                <section class="section">
                    <h3 class="h3">상세 설명</h3>
                    <div id="descBox" class="desc collapsed">
                            ${fn:escapeXml(info.description)}
                    </div>
                    <button type="button"
                            class="btn-more"
                            data-more-target="#descBox"
                            data-more-collapsed-text="더 보기"
                            data-more-expanded-text="접기">더 보기
                    </button>
                </section>
            </c:if>

            <!-- 위치 -->
            <section class="section">
                <h3 class="h3">위치</h3>
                <p class="muted">
                    <c:out value="${info != null ? info.location : '등록된 위치 정보가 없습니다.'}"/>
                </p>
            </section>

            <!-- 사진 모두 보기 -->
            <c:if test="${not empty imagePaths && fn:length(imagePaths) > 5}">
                <section class="section" id="photos">
                    <h3 class="h3">사진</h3>
                    <div class="photo-grid">
                        <c:forEach var="img" items="${imagePaths}" varStatus="st">
                            <img src="${img}" alt="추가 사진 ${st.count}">
                        </c:forEach>
                    </div>
                </section>
            </c:if>
        </div>

        <!-- 오른쪽: 예약 카드 (승인된 숙소만) -->
        <div class="right-col">
            <c:if test="${accommodation.status == 'APPROVED' && info != null}">
                <div class="booking-card">
                    <div class="booking-price">
                        ₩<fmt:formatNumber value="${accommodation.price}" type="number"/> <span>/ 1박</span>
                    </div>

                    <form:form id="reservationForm"
                               method="post"
                               action="/reservation"
                               modelAttribute="reservationRequestDto"
                               cssClass="booking-form">

                        <!-- 서버로 보낼 값 -->
                        <form:hidden path="accommodationId"/>
                        <form:hidden path="checkIn"  id="checkInHidden"/>
                        <form:hidden path="checkOut" id="checkOutHidden"/>

                        <!-- 날짜 -->
                        <label class="lbl">예약 날짜</label>
                        <div class="date-grid" role="group" aria-label="예약 날짜">
                            <div class="date-cell">
                                <div class="date-label">체크인</div>
                                <input id="checkInInput" type="text" class="ctl date" placeholder="날짜 선택" readonly>
                            </div>
                            <div class="date-cell">
                                <div class="date-label">체크아웃</div>
                                <input id="checkOutInput" type="text" class="ctl date" placeholder="날짜 선택" readonly>
                            </div>
                        </div>
                        <form:errors path="checkIn"  cssClass="error"/>
                        <form:errors path="checkOut" cssClass="error"/>

                        <!-- 인원 -->
                        <label class="lbl">인원수</label>
                        <form:select path="guestCount" cssClass="ctl select">
                            <c:forEach var="n" begin="1" end="${info.personnel}">
                                <form:option value="${n}">${n}명</form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="guestCount" cssClass="error"/>

                        <button type="submit" class="booking-submit">예약하기</button>
                    </form:form>

                    <div class="booking-note muted">예약 확정 전에는 요금이 청구되지 않습니다.</div>
                </div>
            </c:if>
        </div>
    </div>

    <!-- 상세정보가 아예 없을 때: 소유자에게만 작은 등록 버튼 -->
    <c:if test="${info == null && sessionScope.user != null && sessionScope.user.id == accommodation.user.id}">
        <div class="empty-info">
            <a href="/info/register?accommodationId=${accommodation.id}" class="btn-primary">상세정보 등록</a>
        </div>
    </c:if>
</div>

<!-- ===== Flatpickr & 비활성 날짜 갱신 ===== -->
<script>
    (function () {
        // 상세가 있을 때만 달력 로드
        const hasInfo = '${info != null}' === 'true';
        if (!hasInfo) return;

        const ACC_ID = '${accommodation.id}';
        const fp = flatpickr('#checkInInput', {
            plugins: [ new rangePlugin({ input: '#checkOutInput' }) ],
            minDate: 'today',
            dateFormat: 'Y-m-d',
            onChange(selectedDates, _, instance) {
                if (selectedDates.length === 2) {
                    const fmt = d => instance.formatDate(d, 'Y-m-d');
                    document.getElementById('checkInHidden').value  = fmt(selectedDates[0]);
                    document.getElementById('checkOutHidden').value = fmt(selectedDates[1]);
                } else if (selectedDates.length === 1) {
                    document.getElementById('checkOutHidden').value = '';
                }
            },
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
                + '/booked?from=' + toIso(from) + '&to=' + toIso(to);

            try {
                const res = await fetch(url);
                const ranges = await res.json();  // [{from:"YYYY-MM-DD", to:"YYYY-MM-DD"}, ...]

                const disabledDays = [];
                ranges.forEach(r => {
                    let cur = new Date(r.from + 'T00:00:00');
                    const end = new Date(r.to   + 'T00:00:00');
                    while (cur <= end) {
                        const y = cur.getFullYear();
                        const m = String(cur.getMonth()+1).padStart(2,'0');
                        const d = String(cur.getDate()).padStart(2,'0');
                        disabledDays.push(`${y}-${m}-${d}`);
                        cur.setDate(cur.getDate()+1);
                    }
                });

                instance.set('disable', disabledDays);
            } catch (e) {
                console.warn('예약 불가 날짜 불러오기 실패', e);
            }
        }
    })();

    // 상세설명 더보기 토글
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('[data-more-target]');
        if (!btn) return;
        const el = document.querySelector(btn.dataset.moreTarget);
        if (!el) return;
        el.classList.toggle('collapsed');
        const collapsed = el.classList.contains('collapsed');
        btn.textContent = collapsed ? btn.dataset.moreCollapsedText : btn.dataset.moreExpandedText;
    });
</script>
</body>
</html>
