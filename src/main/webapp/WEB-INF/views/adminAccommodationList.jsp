<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>숙소 승인 관리</title>
    <link rel="stylesheet" href="/css/adminDashboard.css" />
</head>
<body>
<%@ include file="layout/header.jsp" %>

<h1>관리자 숙소 관리</h1>

<table border="1">
    <thead>
    <tr>
        <th>숙소명</th>
        <th>호스트</th>
        <th>상태</th>
        <th>승인</th>
        <th>반려</th>
        <th>삭제 승인</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="ac" items="${accommodationData}">
        <tr>
            <td>${ac.name}</td>
            <td>${ac.user.loginId}</td>
            <td>${ac.status}</td>

            <td>
                <c:if test="${ac.status == 'PENDING'}">
                    <form method="post" action="/admin/accommodations/approve/${ac.id}">
                        <button type="submit">승인</button>
                    </form>
                </c:if>
            </td>

            <td>
                <c:if test="${ac.status == 'PENDING'}">
                    <form method="post" action="/admin/accommodations/reject/${ac.id}">
                        <button type="submit">반려</button>
                    </form>
                </c:if>
            </td>

            <td>
                <c:if test="${ac.status == 'DELETE_REQUESTED'}">
                    <form method="post" action="/admin/accommodation/delete/approve/${ac.id}"
                          onsubmit="return confirm('정말 삭제하시겠습니까?');">
                        <button type="submit" style="color:red;">삭제 승인</button>
                    </form>

                    <form method="post" action="/admin/accommodation/delete/cancel/${ac.id}"
                          onsubmit="return confirm('삭제 요청을 취소하시겠습니까?');"
                          style="display:inline;">
                        <button type="submit">삭제 취소</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
