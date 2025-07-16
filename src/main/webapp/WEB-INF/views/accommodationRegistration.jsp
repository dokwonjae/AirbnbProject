<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <title>숙소 등록/수정</title>
</head>
<body>

<h1><c:choose>
  <c:when test="${not empty accommodation}">숙소 수정</c:when>
  <c:otherwise>숙소 등록</c:otherwise>
</c:choose></h1>


<c:if test="${not empty msg}">
  <script>alert("${msg}");</script>
</c:if>

<!-- form 시작 -->
<form method="post"
      enctype="multipart/form-data"
      action="<c:choose>
                <c:when test='${not empty accommodation}'>/accommodation/update</c:when>
                <c:otherwise>/accommodation/register</c:otherwise>
              </c:choose>">


  <c:if test="${not empty accommodation}">
    <input type="hidden" name="id" value="${accommodation.id}" />
  </c:if>

  <!-- 숙소명 -->
  <label for="name">숙소 이름:</label><br/>
  <input type="text" name="name" id="name" value="${accommodation.name}" required/><br/><br/>

  <!-- 가격 -->
  <label for="price">숙박 가격:</label><br/>
  <input type="number" name="price" id="price" value="${accommodation.price}" required/><br/><br/>

  <!-- 전망 종류 -->
  <label for="view">전망 종류:</label><br/>
  <input type="text" name="view" id="view" value="${accommodation.view}" required/><br/><br/>


  <label for="image">대표 이미지 (JPG/PNG):</label><br/>
  <input type="text" name="image" id="image" value="${accommodation.image}" required/><br/><br/>


  <button type="submit">
    <c:choose>
      <c:when test="${not empty accommodation}">수정하기</c:when>
      <c:otherwise>등록하기</c:otherwise>
    </c:choose>
  </button>
</form>

</body>
</html>
