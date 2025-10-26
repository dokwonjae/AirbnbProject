<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <title>숙소 상세 정보</title>
    <link rel="stylesheet" href="/css/header.css">
    <link rel="stylesheet" href="/css/accommodationInfoForm.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="form-page">
    <h1 class="page-title">
        <c:choose>
            <c:when test="${editing}">숙소 상세 수정</c:when>
            <c:otherwise>숙소 상세 등록</c:otherwise>
        </c:choose>
    </h1>

    <div class="form-card">
        <c:set var="formAction" value="/info/register?accommodationId=${accommodationId}"/>
        <c:if test="${editing}"><c:set var="formAction" value="/info/edit/${infoId}"/></c:if>

        <form:form modelAttribute="accommodationInfoRequestDto"
                   method="post" enctype="multipart/form-data"
                   action="${formAction}" class="form">
            <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">

            <div class="form-row">
                <label>제목</label>
                <form:input path="title" cssClass="input"/>
                <form:errors path="title" cssClass="error"/>
            </div>

            <div class="form-row">
                <label>위치</label>
                <form:input path="location" cssClass="input"/>
                <form:errors path="location" cssClass="error"/>
            </div>

            <div class="form-row">
                <label>부제목</label>
                <form:input path="subTitle" cssClass="input"/>
                <form:errors path="subTitle" cssClass="error"/>
            </div>

            <div class="form-row">
                <label>숙박 인원</label>
                <form:input path="personnel" cssClass="input"/>
                <form:errors path="personnel" cssClass="error"/>
            </div>

            <div class="form-row">
                <label>편의시설</label>
                <form:input path="amenities" cssClass="input"/>
                <form:errors path="amenities" cssClass="error"/>
            </div>

            <div class="form-row">
                <label>이미지 (최대 5장)</label>
                <input type="file" name="images" multiple class="input file-input"/>
            </div>

            <div class="actions">
                <button type="submit" class="btn primary">
                    <c:choose>
                        <c:when test="${editing}">수정하기</c:when>
                        <c:otherwise>등록하기</c:otherwise>
                    </c:choose>
                </button>
                <a href="/account?tab=listings" class="btn">취소</a>
            </div>

        </form:form>
    </div>
</div>
</body>
</html>
