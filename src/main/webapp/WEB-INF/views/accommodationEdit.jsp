<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>숙소 수정</title>
  <link rel="stylesheet" href="/css/accommodationEdit.css" />
</head>
<body>
<div class="container">
  <h1>숙소 정보 수정</h1>

  <c:if test="${not empty msg}">
    <div class="alert">${msg}</div>
  </c:if>

  <form method="post" action="/accommodation/update">
    <input type="hidden" name="id" value="${accommodation.id}" />

    <label>숙소명</label>
    <input type="text" name="name" value="${accommodation.name}" required />

    <label>가격</label>
    <input type="number" name="price" value="${accommodation.price}" required />

    <label>전망 설명</label>
    <input type="text" name="view" value="${accommodation.view}" />

    <label>이미지 링크</label>
    <input type="text" name="image" value="${accommodation.image}" />

    <button type="submit" class="submit-btn">수정 완료</button>
  </form>
</div>
</body>
</html>
