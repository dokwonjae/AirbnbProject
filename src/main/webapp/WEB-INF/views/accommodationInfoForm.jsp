<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <title>숙소 상세 정보</title>
</head>
<body>

<div class="container">
    <h1>
        <c:choose>
            <c:when test="${editing}">숙소 상세 수정</c:when>
            <c:otherwise>숙소 상세 등록</c:otherwise>
        </c:choose>
    </h1>

    <!-- ✅ action URL 분기 처리 -->
    <c:set var="formAction" value="/info/register?accommodationId=${accommodationId}" />
    <c:if test="${editing}">
        <c:set var="formAction" value="/info/edit/${infoId}" />
    </c:if>

    <form:form modelAttribute="accommodationInfoRequestDto"
               method="post"
               enctype="multipart/form-data"
               action="${formAction}">

        <div>
            <label>제목</label>
            <form:input path="title"/>
            <form:errors path="title"/>
        </div>

        <div>
            <label>위치</label>
            <form:input path="location"/>
            <form:errors path="location"/>
        </div>

        <div>
            <label>부제목</label>
            <form:input path="subTitle"/>
            <form:errors path="subTitle"/>
        </div>

        <div>
            <label>숙박 인원</label>
            <form:input path="personnel"/>
            <form:errors path="personnel"/>
        </div>

        <div>
            <label>편의시설</label>
            <form:input path="amenities"/>
            <form:errors path="amenities"/>
        </div>

        <div>
            <label>이미지 (최대 5장)</label>
            <input type="file" name="images" multiple/>
        </div>

        <button type="submit">
            <c:choose>
                <c:when test="${editing}">수정하기</c:when>
                <c:otherwise>등록하기</c:otherwise>
            </c:choose>
        </button>

    </form:form>
</div>

</body>
</html>
