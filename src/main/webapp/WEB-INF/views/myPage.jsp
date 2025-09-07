<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <title>마이페이지</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/myPage.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<c:set var="tab" value="${empty param.tab ? 'profile' : param.tab}"/>

<div class="account-page">
    <h1 class="page-title">마이페이지</h1>

    <div class="account-layout">
        <!-- 좌측 사이드 내비 -->
        <aside class="sidenav" aria-label="마이페이지 메뉴">
            <a href="/account?tab=profile"
               class="nav-item ${tab == 'profile' ? 'active' : ''}"
               data-tab="profile">
                <span class="nav-ico">👤</span><span>개인 정보</span>
            </a>
            <a href="/account?tab=reservations"
               class="nav-item ${tab == 'reservations' ? 'active' : ''}"
               data-tab="reservations">
                <span class="nav-ico">📅</span><span>예약 내역</span>
            </a>
            <a href="/account?tab=listings"
               class="nav-item ${tab == 'listings' ? 'active' : ''}"
               data-tab="listings">
                <span class="nav-ico">🏠</span><span>숙소 관리</span>
            </a>
        </aside>

        <!-- 우측 콘텐츠 패널 -->
        <main class="pane">
            <!-- 개인 정보 -->
            <section class="pane-card" data-panel="profile" ${tab != 'profile' ? 'hidden' : ''}>
                <h2 class="pane-title">개인 정보</h2>

                <div class="info-grid">
                    <div class="info-row">
                        <div class="k">아이디</div>
                        <div class="v">${user.loginId}</div>
                    </div>
                    <div class="info-row">
                        <div class="k">이메일</div>
                        <div class="v">${user.email}</div>
                    </div>
                </div>

                <div class="btn-row">
                    <a class="btn" href="/account/contact">연락처 변경</a>
                    <a class="btn" href="/account/password">비밀번호 변경</a>
                </div>

                <form method="get" action="/account/delete" class="mt-12">
                    <button type="submit" class="btn danger">회원 탈퇴</button>
                </form>
            </section>

            <!-- 예약 내역 -->
            <section class="pane-card" data-panel="reservations" ${tab != 'reservations' ? 'hidden' : ''}>
                <h2 class="pane-title">예약 내역</h2>

                <c:choose>
                    <c:when test="${not empty myReservations}">
                        <div class="table-wrap">
                            <table class="tbl">
                                <thead>
                                <tr>
                                    <th>숙소명</th>
                                    <th>체크인 ~ 체크아웃</th>
                                    <th>결제 상태</th>
                                    <th>상세 보기</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="reservation" items="${myReservations}">
                                    <tr>
                                        <td class="ellipsis">${reservation.accommodationName}</td>
                                        <td>${reservation.checkIn.format(dtf)}
                                            ~ ${reservation.checkOut.format(dtf)}</td>
                                        <td>${reservation.status}</td>
                                        <td><a class="link" href="/reservation/${reservation.id}">열기</a></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="muted">예약 내역이 없습니다.</p>
                    </c:otherwise>
                </c:choose>
            </section>

            <!-- 숙소 관리 -->
            <section class="pane-card" data-panel="listings" ${tab != 'listings' ? 'hidden' : ''}>
                <div class="pane-title-row">
                    <h2 class="pane-title">숙소 관리</h2>
                    <a class="btn primary" href="/accommodation/register">+ 새 숙소 등록</a>
                </div>

                <c:choose>
                    <c:when test="${not empty myAccommodations}">
                        <div class="table-wrap">
                            <table class="tbl">
                                <thead>
                                <tr>
                                    <th>숙소명</th>
                                    <th>가격</th>
                                    <th>수정</th>
                                    <th>삭제</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="ac" items="${myAccommodations}">
                                    <tr>
                                        <td class="ellipsis">${ac.name}</td>
                                        <td>₩<fmt:formatNumber value="${ac.price}" type="number"/>원 / 1박</td>
                                        <td><a class="link" href="/accommodation/edit?id=${ac.id}">수정</a></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${ac.status == 'APPROVED'}">
                                                    <form method="post" action="/accommodation/delete"
                                                          onsubmit="return confirm('숙소 삭제 요청을 하시겠습니까?');">
                                                        <input type="hidden" name="id" value="${ac.id}"/>
                                                        <button type="submit" class="btn tiny">삭제 요청</button>
                                                    </form>
                                                </c:when>
                                                <c:when test="${ac.status == 'DELETE_REQUESTED'}">
                                                    <span class="badge warn">삭제 요청됨 (승인 대기)</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge">삭제 불가</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="muted">등록한 숙소가 없습니다.</p>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>
    </div>
</div>

<script>
    // 탭 전환을 페이지 새로고침 없이 처리 (프로그레시브)
    (function () {
        const ACTIVE = '${tab}';

        function show(which) {
            document.querySelectorAll('[data-panel]').forEach(p => {
                p.hidden = p.dataset.panel !== which;
            });
            document.querySelectorAll('.sidenav .nav-item').forEach(a => {
                a.classList.toggle('active', a.dataset.tab === which);
            });
        }

        // 초기 상태 보정 (서버가 렌더한 탭 그대로)
        show(ACTIVE || 'profile');

        document.querySelectorAll('.sidenav .nav-item').forEach(a => {
            a.addEventListener('click', (e) => {
                e.preventDefault();
                const which = a.dataset.tab;
                show(which);
                const url = new URL(location.href);
                url.searchParams.set('tab', which);
                history.replaceState(null, '', url);
            });
        });
    })();
</script>
</body>
</html>
