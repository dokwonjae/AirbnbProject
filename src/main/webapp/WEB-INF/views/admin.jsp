<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>관리자</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<c:set var="tab" value="${empty param.tab ? 'users' : param.tab}"/>

<div class="admin-page">
    <h1 class="page-title">관리자</h1>

    <div class="admin-layout">
        <!-- 좌측 사이드내비 -->
        <aside class="sidenav" aria-label="관리자 메뉴">
            <a href="/admin?tab=users" class="nav-item ${tab == 'users' ? 'active' : ''}" data-tab="users">
                <span class="nav-ico">👥</span><span>회원 목록 보기</span>
            </a>
            <a href="/admin?tab=stats" class="nav-item ${tab == 'stats' ? 'active' : ''}" data-tab="stats">
                <span class="nav-ico">📊</span><span>예약 통계 보기</span>
            </a>
            <a href="/admin?tab=accommodations" class="nav-item ${tab == 'accommodations' ? 'active' : ''}" data-tab="accommodations">
                <span class="nav-ico">🏠</span><span>숙소 승인/반려 관리</span>
            </a>
        </aside>

        <!-- 우측 콘텐츠 -->
        <main class="pane">
            <!-- 회원 목록 -->
            <section class="pane-card" data-panel="users" ${tab != 'users' ? 'hidden' : ''}>
                <div class="pane-title-row">
                    <h2 class="pane-title">회원 목록</h2>
                    <!-- 필요 시 검색/필터 추가 자리 -->
                </div>

                <c:choose>
                    <c:when test="${not empty userList}">
                        <div class="table-wrap">
                            <table class="tbl">
                                <thead>
                                <tr>
                                    <th>아이디</th>
                                    <th>이메일</th>
                                    <th>가입일</th>
                                    <th class="th-actions">예약내역</th>

                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="u" items="${userList}">
                                    <tr>
                                        <td class="ellipsis">${u.loginId}</td>
                                        <td class="ellipsis">${u.email}</td>
                                        <td class="ellipsis">${u.createdAtText}</td>
                                        <td class="td-actions">
                                            <a class="btn outline" href="/admin/users/${u.id}/reservations">보기</a>
                                        </td>

                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="muted">회원 데이터가 없습니다.</p>
                    </c:otherwise>
                </c:choose>
            </section>

            <!-- 예약 통계 -->
            <section class="pane-card" data-panel="stats" ${tab != 'stats' ? 'hidden' : ''}>
                <div class="pane-title-row">
                    <h2 class="pane-title">예약 통계</h2>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-k">총 회원</div>
                        <div class="stat-v"><c:out value="${stats.totalUsers}"/></div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-k">총 예약 건수</div>
                        <div class="stat-v"><c:out value="${stats.totalReservations}"/></div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-k">결제 완료</div>
                        <div class="stat-v"><c:out value="${stats.paidCount}"/></div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-k">총 매출</div>
                        <div class="stat-v">₩<fmt:formatNumber value="${stats.totalRevenue}" type="number"/></div>
                    </div>
                </div>

                <!-- 필요 시 기간 필터/추세 차트 영역 추가 가능 -->
            </section>

            <!-- 숙소 승인/반려 관리 -->
            <section class="pane-card" data-panel="accommodations" ${tab != 'accommodations' ? 'hidden' : ''}>
                <div class="pane-title-row">
                    <h2 class="pane-title">숙소 승인/반려/보존(삭제요청) 관리</h2>
                </div>

                <div class="table-wrap">
                    <table class="tbl">
                        <thead>
                        <tr>
                            <th>숙소명</th>
                            <th>호스트</th>
                            <th>상태</th>
                            <th class="th-actions">상세</th>
                            <th class="th-actions">승인</th>
                            <th class="th-actions">반려</th>
                            <th class="th-actions">삭제 승인</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="ac" items="${accommodationData}">
                            <tr>
                                <td class="ellipsis">${ac.name}</td>
                                <td>${ac.hostLoginId}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${ac.status.name() == 'APPROVED'}"><span class="badge ok">APPROVED</span></c:when>
                                        <c:when test="${ac.status.name() == 'PENDING'}"><span class="badge pending">PENDING</span></c:when>
                                        <c:when test="${ac.status.name() == 'REJECTED'}"><span class="badge danger">REJECTED</span></c:when>
                                        <c:when test="${ac.status.name() == 'DELETE_REQUESTED'}"><span class="badge warn">DELETE_REQUESTED</span></c:when>
                                        <c:when test="${ac.status.name() == 'ARCHIVED'}"><span class="badge muted">ARCHIVED</span></c:when>
                                        <c:otherwise><span class="badge">${ac.status}</span></c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="td-actions">
                                    <a class="btn outline" href="/accommodation/${ac.id}">보기</a>
                                </td>

                                <!-- 승인 버튼: ARCHIVED이면 비활성 -->
                                <td class="td-actions">
                                    <form method="post" action="/admin/accommodations/approve/${ac.id}">
                                        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                        <button type="submit" class="btn primary"
                                            ${ac.status.name() == 'APPROVED' || ac.status.name() == 'ARCHIVED' ? 'disabled' : ''}>
                                            승인
                                        </button>
                                    </form>
                                </td>

                                <!-- 반려 버튼: ARCHIVED이면 비활성 -->
                                <td class="td-actions">
                                    <form method="post" action="/admin/accommodations/reject/${ac.id}">
                                        <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                        <button type="submit" class="btn outline"
                                            ${ac.status.name() == 'REJECTED' || ac.status.name() == 'ARCHIVED' ? 'disabled' : ''}>
                                            반려
                                        </button>
                                    </form>
                                </td>

                                <!-- 삭제 승인/취소: DELETE_REQUESTED 일 때만 노출, ARCHIVED면 아무 동작 없음 -->
                                <td class="td-actions">
                                    <c:choose>
                                        <c:when test="${ac.status == 'DELETE_REQUESTED'}">
                                            <form method="post" action="/admin/accommodation/delete/approve/${ac.id}" onsubmit="return confirm('해당 숙소를 보존(ARCHIVED) 상태로 전환하시겠습니까?');">
                                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                                <button type="submit" class="btn danger">삭제 승인(보존 전환)</button>
                                            </form>
                                            <form method="post" action="/admin/accommodation/delete/cancel/${ac.id}" onsubmit="return confirm('삭제 요청을 취소하시겠습니까?');">
                                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                                                <button type="submit" class="btn outline">삭제 취소</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="muted">-</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>

        </main>
    </div>
</div>

<script>
    // 탭 전환 (페이지 리로드 없이)
    (function () {
        const ACTIVE = '${tab}' || 'users';
        function show(which) {
            document.querySelectorAll('[data-panel]').forEach(p => p.hidden = (p.dataset.panel !== which));
            document.querySelectorAll('.sidenav .nav-item').forEach(a => a.classList.toggle('active', a.dataset.tab === which));
        }
        show(ACTIVE);
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
