<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.airbnbproject.domain.User" %>

<%
  User user = (User) session.getAttribute("user");
  String loginId = (user != null) ? user.getLoginId() : "";
%>

<link rel="stylesheet" href="/css/header.css">
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

<div class="header-container">
  <div class="logo">
    <a href="/"><img src="/images/logo.png" alt="로고"></a>
  </div>

  <form class="search-form" action="/search" method="get">
    <input type="text" name="searchName" placeholder="어디든지 | 언제든 일주일 | 게스트추가">
    <button type="submit">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" class="bi bi-search" viewBox="0 0 16 16">
        <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398l3.85 3.85a1 1 0 1 0 1.415-1.414l-3.85-3.85zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
      </svg>
    </button>
  </form>

  <div class="airbnb-msg">당신의 공간을 에어비앤비하세요</div>

  <div class="user-actions">
    <!-- 메뉴 드롭다운 -->
    <div class="menu dropdown-wrap">
      <div class="dropdown-btn" onclick="toggleDropdown('menuDropdown')">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="black" class="bi bi-list" viewBox="0 0 16 16">
          <path d="M2.5 12a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5z"/>
        </svg>
      </div>
      <ul class="dropdown-menu" id="menuDropdown">
        <% if (user != null) { %>
        <li><a href="/myPage">마이페이지</a></li>
        <li><form action="/logout" method="post"><button type="submit">로그아웃</button></form></li>
        <% } else { %>
        <li><a href="/join">회원가입</a></li>
        <li><a href="/login">로그인</a></li>
        <% } %>
      </ul>
    </div>

    <!-- 사용자 드롭다운 -->
    <div class="menu dropdown-wrap">
      <div class="dropdown-btn" onclick="toggleDropdown('userDropdown')">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="black" class="bi bi-person-circle" viewBox="0 0 16 16">
          <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
          <path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"/>
        </svg>
      </div>
      <ul class="dropdown-menu" id="userDropdown">
        <% if (user != null) { %>
        <li><a href="/myPage">마이페이지</a></li>
        <li><form action="/logout" method="post"><button type="submit">로그아웃</button></form></li>
        <% } else { %>
        <li><a href="/join">회원가입</a></li>
        <li><a href="/login">로그인</a></li>
        <% } %>
      </ul>
    </div>
  </div>
</div>

<script>
  function toggleDropdown(id) {
    const target = document.getElementById(id);
    target.classList.toggle('show');

    // 다른 드롭다운 닫기
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
      if (menu.id !== id) menu.classList.remove('show');
    });
  }

  window.addEventListener('click', function(e) {
    if (!e.target.closest('.dropdown-wrap')) {
      document.querySelectorAll('.dropdown-menu').forEach(menu => menu.classList.remove('show'));
    }
  });
</script>
