<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<head>
    <title>에어비앤비</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/auth.css">
    <link rel="stylesheet" href="/css/index.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>
<%@ include file="layout/auth.jsp" %>


<div class="main-container">
    <c:if test="${not empty msg && msg ne '로그인 성공'}">
        <script>alert("${msg}");</script>
    </c:if>

    <%
        // 태그키와 라벨을 나란히 정의
        String[] tagKeys   = {"view","beach","camp","park","countryside","pool","tiny","trending","unique"};
        String[] tagLabels = {"최고의 전망","해변 근처","캠핑장","국립공원","한적한 시골","멋진 수영장","초소형 주택","인기 급상승","기상천외한 숙소"};
        request.setAttribute("tagKeys", tagKeys);
        request.setAttribute("tagLabels", tagLabels);
    %>

    <!-- 카테고리 바 (tags 기반 필터) -->
    <div class="category-bar">
        <c:forEach var="i" begin="0" end="8">
            <c:set var="key"   value="${tagKeys[i]}"/>
            <c:set var="label" value="${tagLabels[i]}"/>
            <c:set var="isActive" value="${selectedTag == key}"/>

            <!-- 선택되어 있으면 다시 클릭 시 전체(=/)로, 아니면 해당 태그로 -->
            <a href="${isActive ? '/' : '/?tag='}${isActive ? '' : key}"
               class="category-item ${isActive ? 'active' : ''}">
                <!-- 아이콘은 네가 쓰던 경로로 유지 -->
                <img src="/images/${i + 1}.jpg" alt="${label}">
                <div>${label}</div>
            </a>
        </c:forEach>
    </div>

    <!-- 숙소 카드 그리드 -->
    <div class="accommodation-grid">
        <c:forEach var="accommodation" items="${accommodationData}">
            <div class="accommodation-card">
                <a href="/accommodation/${accommodation.id}">
                    <img src="${accommodation.image}" alt="${accommodation.name}">
                </a>
                <div class="name">${accommodation.name}</div>
                <div class="price">₩<fmt:formatNumber value="${accommodation.price}" type="number"/>원 <span>/ 1박</span></div>
            </div>
        </c:forEach>

        <c:if test="${empty accommodationData}">
            <div style="grid-column: 1 / -1; text-align:center; color:#717171; padding:40px 0;">
                조건에 맞는 숙소가 없습니다.
            </div>
        </c:if>
    </div>
</div>
</body>
</html>
