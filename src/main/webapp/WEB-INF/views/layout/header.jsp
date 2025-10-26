<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="header-container">
    <div class="logo">
        <a href="/"><img src="/images/logo.png" alt="로고"></a>
    </div>

    <form class="search-form" action="/search" method="get">
        <input type="text" name="searchName" placeholder="어디든지 | 언제든 일주일 | 게스트추가">
        <button type="submit" aria-label="검색">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" class="bi bi-search" viewBox="0 0 16 16">
                <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398l3.85 3.85a1 1 0 1 0 1.415-1.414l-3.85-3.85zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/>
            </svg>
        </button>
    </form>

    <div class="airbnb-msg">당신의 공간을 에어비앤비하세요</div>

    <div class="user-actions">
        <!-- 메뉴 드롭다운 -->
        <div class="menu dropdown-wrap">
            <button class="dropdown-btn" type="button" onclick="toggleDropdown('menuDropdown')" aria-haspopup="true"
                    aria-expanded="false" aria-controls="menuDropdown">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="black" class="bi bi-list" viewBox="0 0 16 16">
                    <path d="M2.5 12a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5z"/>
                </svg>
            </button>
            <ul class="dropdown-menu" id="menuDropdown">
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <li><a href="/account">마이페이지</a></li>
                        <li>
                            <a href="#" id="logoutLink">로그아웃</a>
                            <form id="logoutForm" action="/logout" method="post" style="display:none">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            </form>
                        </li>
                        <c:if test="${sessionScope.user.role == 'ADMIN'}">
                            <li><a href="/admin">관리자 메뉴</a></li>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/?auth=join" class="auth-open" data-auth="join">회원가입</a></li>
                        <li><a href="/?auth=login" class="auth-open" data-auth="login">로그인</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>

        <!-- 사용자 드롭다운 -->
        <div class="menu dropdown-wrap">
            <button class="dropdown-btn" type="button" onclick="toggleDropdown('userDropdown')" aria-haspopup="true"
                    aria-expanded="false" aria-controls="userDropdown">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="black" class="bi bi-person-circle" viewBox="0 0 16 16">
                    <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0z"/>
                    <path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8zm8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1z"/>
                </svg>
            </button>
            <ul class="dropdown-menu" id="userDropdown">
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <li><a href="/account">마이페이지</a></li>
                        <li>
                            <a href="#" id="logoutLink2">로그아웃</a>
                            <form id="logoutForm2" action="/logout" method="post" style="display:none">
                                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                            </form>
                        </li>
                        <c:if test="${sessionScope.user.role == 'ADMIN'}">
                            <li><a href="/admin">관리자 메뉴</a></li>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/?auth=join" class="auth-open" data-auth="join">회원가입</a></li>
                        <li><a href="/?auth=login" class="auth-open" data-auth="login">로그인</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</div>

<script>
    // 드롭다운
    function toggleDropdown(id) {
        const target = document.getElementById(id);
        target.classList.toggle('show');
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            if (menu.id !== id) menu.classList.remove('show');
        });
    }
    window.addEventListener('click', function (e) {
        if (!e.target.closest('.dropdown-wrap')) {
            document.querySelectorAll('.dropdown-menu').forEach(menu => menu.classList.remove('show'));
        }
    });

    // 로그아웃
    document.addEventListener('click', e => {
        if (e.target.closest('#logoutLink')) {
            e.preventDefault();
            document.getElementById('logoutForm').submit();
        }
        if (e.target.closest('#logoutLink2')) {
            e.preventDefault();
            document.getElementById('logoutForm2').submit();
        }
    });

    // 인증 모달 오픈(공용)
    document.addEventListener('click', function (e) {
        const a = e.target.closest('a.auth-open');
        if (!a) return;
        e.preventDefault();
        const modal = document.getElementById('authModal');
        if (!modal) {
            location.href = a.getAttribute('href') || '/?auth=login';
            return;
        }
        const which = a.dataset.auth || 'login';
        modal.hidden = false;
        document.body.classList.add('auth-no-scroll');
        modal.querySelectorAll('.auth-panel').forEach(p => {
            p.hidden = (p.dataset.authPanel !== which);
        });
    });
</script>
