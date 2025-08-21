<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>에어비앤비</title>
    <link rel="stylesheet" type="text/css" href="css/index.css">
    <link rel="stylesheet" type="text/css" href="/css/header.css">

</head>
<body>
<%@ include file="layout/header.jsp" %>

<c:if test="${not empty msg and msg ne '로그인 성공'}">
    <script>alert("${msg}");</script>
</c:if>

<%
    String[] categoryNames = {
            "최고의 전망", "해변 근처", "캠핑장", "국립공원", "한적한 시골",
            "멋진 수영장", "초소형 주택", "인기 급상승", "기상천외한 숙소"
    };
    request.setAttribute("categoryNames", categoryNames);
%>

<div class="category-bar">
    <c:forEach var="i" begin="0" end="8">
        <a href="/" class="category-item">
                <img src="/images/${i + 1}.jpg" alt="category">
            <div>${categoryNames[i]}</div>
        </a>
    </c:forEach>
</div>


<div class="accommodation" style="display: flex; flex-wrap: wrap; margin: 0 100px;">
    <c:forEach var="accommodation" items="${accommodationData}">
        <div style="margin: 50px;">
            <a href="/accommodation/${accommodation.id}">
                <img src="${accommodation.image}" style="border-radius: 20px; width: 450px; height: 450px;">
            </a>
            <div>${accommodation.name}</div>
            <div>${accommodation.view}</div>
            <div><fmt:formatNumber value="${accommodation.price}" type="number"/>원</div>
        </div>
    </c:forEach>
</div>

</body>
</html>
