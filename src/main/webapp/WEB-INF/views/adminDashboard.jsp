<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>관리자 대시보드</title>
  <link rel="stylesheet" type="text/css" href="/css/admin.css">
</head>
<body>
<%@ include file="layout/header.jsp" %>

<div class="admin-container">
  <h1>관리자 대시보드</h1>

  <ul class="admin-menu">
    <li><a href="/admin/accommodations">숙소 승인/반려 관리</a></li>
    <%-- 추후 확장 가능: --%>
    <%-- <li><a href="/user/admin/list">회원 목록 보기</a></li> --%>
    <%-- <li><a href="/stats">예약 통계 보기</a></li> --%>
  </ul>
</div>

</body>
</html>
