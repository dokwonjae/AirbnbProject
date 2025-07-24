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
<body>

<%@ include file="layout/header.jsp" %>

<c:if test="${param.error == 'infoExists'}">
    <script>alert('이미 등록된 상세정보가 있는 숙소입니다.');</script>
</c:if>

<div class="container">

    <c:if test="${info == null}">
        <c:set var="accommodationId" value="${accommodation.id}"/>
        <p>accommodation.id: ${accommodation.id}</p>
        <a href="/info/register?accommodationId=${accommodationId}" class="btn">상세정보 등록하기</a>
    </c:if>

    <!-- ✅ 수정/삭제 버튼: 로그인한 사용자 == 숙소 등록자일 때만 노출 -->
    <c:if test="${sessionScope.user != null && sessionScope.user.id == accommodation.user.id}">
        <div style="margin-top: 20px;">
            <form method="get" action="/info/edit/${info.id}" style="display:inline;">
                <button type="submit">수정</button>
            </form>
            <form method="post" action="/info/delete/${info.id}" onsubmit="return confirm('정말 삭제하시겠습니까?');" style="display:inline;">
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
    </c:if>

</div>

</body>
</html>
