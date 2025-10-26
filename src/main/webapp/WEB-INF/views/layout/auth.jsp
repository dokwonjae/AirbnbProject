<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Flash 별칭 -->
<c:set var="joinData"         value="${joinData}" />
<c:set var="joinFieldErrors"  value="${joinFieldErrors}" />
<c:set var="joinErrorMsg"     value="${joinErrorMsg}" />
<c:set var="authTab"          value="${authTab}" />
<c:set var="loginFieldErrors" value="${loginFieldErrors}" />
<c:set var="prevLoginId"      value="${prevLoginId}" />

<link rel="stylesheet" href="/css/auth.css"/>

<div id="authModal" class="auth-modal" role="dialog" aria-modal="true" aria-labelledby="authTitle" hidden>
    <div class="auth-backdrop" data-auth-close="true"></div>

    <div class="auth-dialog" role="document">
        <button type="button" class="auth-close" aria-label="닫기" data-auth-close="true">✕</button>

        <!-- ===== 회원가입 ===== -->
        <section class="auth-panel" data-auth-panel="join" hidden>
            <h2 id="authTitle" class="auth-title">회원가입</h2>

            <c:if test="${not empty joinErrorMsg}">
                <div class="auth-alert" role="alert">${joinErrorMsg}</div>
            </c:if>

            <form method="post" action="/join" class="auth-form" novalidate>
                <!-- ✅ CSRF & next 유지 -->
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <input type="hidden" name="next"  value="${param.next}">

                <div class="auth-field">
                    <label for="join_loginId">아이디</label>
                    <input id="join_loginId" name="loginId"
                           class="auth-input ${not empty joinFieldErrors['loginId'] ? 'is-invalid' : ''}"
                           value="${joinData.loginId}" required>
                    <c:if test="${not empty joinFieldErrors['loginId']}">
                        <small class="auth-error" role="alert">${joinFieldErrors['loginId']}</small>
                    </c:if>
                </div>

                <div class="auth-field">
                    <label for="join_password">비밀번호</label>
                    <input id="join_password" name="password" type="password"
                           class="auth-input ${not empty joinFieldErrors['password'] ? 'is-invalid' : ''}"
                           required>
                    <c:if test="${not empty joinFieldErrors['password']}">
                        <small class="auth-error" role="alert">${joinFieldErrors['password']}</small>
                    </c:if>
                </div>

                <!-- ✅ 비밀번호 확인 -->
                <div class="auth-field">
                    <label for="join_confirmPassword">비밀번호 확인</label>
                    <input id="join_confirmPassword" name="confirmPassword" type="password"
                           class="auth-input ${not empty joinFieldErrors['confirmPassword'] ? 'is-invalid' : ''}"
                           required>
                    <c:if test="${not empty joinFieldErrors['confirmPassword']}">
                        <small class="auth-error" role="alert">${joinFieldErrors['confirmPassword']}</small>
                    </c:if>
                </div>

                <div class="auth-field">
                    <label for="join_email">이메일</label>
                    <input id="join_email" name="email"
                           class="auth-input ${not empty joinFieldErrors['email'] ? 'is-invalid' : ''}"
                           value="${joinData.email}" required>
                    <c:if test="${not empty joinFieldErrors['email']}">
                        <small class="auth-error" role="alert">${joinFieldErrors['email']}</small>
                    </c:if>
                </div>

                <div class="auth-field">
                    <label for="join_tel">전화번호</label>
                    <input id="join_tel" name="tel"
                           class="auth-input ${not empty joinFieldErrors['tel'] ? 'is-invalid' : ''}"
                           value="${joinData.tel}" required>
                    <c:if test="${not empty joinFieldErrors['tel']}">
                        <small class="auth-error" role="alert">${joinFieldErrors['tel']}</small>
                    </c:if>
                </div>

                <button type="submit" class="auth-submit">가입하기</button>
                <div class="auth-footer">
                    이미 계정이 있으신가요? <a href="#" class="auth-link" data-auth-switch="login">로그인</a>
                </div>
            </form>
        </section>

        <!-- ===== 로그인 ===== -->
        <section class="auth-panel" data-auth-panel="login" hidden>
            <h2 class="auth-title">로그인</h2>

            <c:if test="${not empty loginErrorMsg}">
                <div class="auth-alert" role="alert">${loginErrorMsg}</div>
            </c:if>

            <form method="post" action="/login" class="auth-form" novalidate>
                <!-- ✅ CSRF & next 유지 -->
                <input type="hidden" name="_csrf" value="${sessionScope.csrfToken}">
                <input type="hidden" name="next"  value="${param.next}">

                <div class="auth-field">
                    <label for="login_loginId">아이디</label>
                    <input id="login_loginId" name="loginId"
                           class="auth-input ${not empty loginFieldErrors['loginId'] ? 'is-invalid' : ''}"
                           value="${prevLoginId}" required>
                    <c:if test="${not empty loginFieldErrors['loginId']}">
                        <small class="auth-error" role="alert">${loginFieldErrors['loginId']}</small>
                    </c:if>
                </div>

                <div class="auth-field">
                    <label for="login_password">비밀번호</label>
                    <input id="login_password" name="password" type="password"
                           class="auth-input ${not empty loginFieldErrors['password'] ? 'is-invalid' : ''}"
                           required>
                    <c:if test="${not empty loginFieldErrors['password']}">
                        <small class="auth-error" role="alert">${loginFieldErrors['password']}</small>
                    </c:if>
                </div>

                <button type="submit" class="auth-submit">로그인</button>
                <div class="auth-footer">
                    계정이 없으신가요? <a href="#" class="auth-link" data-auth-switch="join">회원가입</a>
                </div>
            </form>
        </section>
    </div>
</div>

<script>
    (function() {
        const modal  = document.getElementById('authModal');
        if (!modal) return;

        const body   = document.body;
        const panels = modal.querySelectorAll('.auth-panel');

        const openTab = (which) => {
            modal.hidden = false;
            body.classList.add('auth-no-scroll');
            panels.forEach(p => p.hidden = (p.dataset.authPanel !== which));
            // URL ?auth= 갱신(선택)
            const url = new URL(location.href);
            url.searchParams.set('auth', which);
            history.replaceState(null, '', url);
        };
        const close = () => {
            modal.hidden = true;
            body.classList.remove('auth-no-scroll');
            // URL ?auth 제거
            const url = new URL(location.href);
            url.searchParams.delete('auth');
            history.replaceState(null, '', url);
        };

        // 닫기
        modal.addEventListener('click', (e) => {
            if (e.target.dataset.authClose === 'true') close();
        });

        // 탭 전환 링크
        modal.querySelectorAll('[data-auth-switch]').forEach(a => {
            a.addEventListener('click', (e) => {
                e.preventDefault();
                openTab(a.dataset.authSwitch);
            });
        });

        // 헤더/페이지 어디서든 .auth-open 링크 클릭 시 모달 오픈
        document.addEventListener('click', (e) => {
            const a = e.target.closest('a.auth-open');
            if (!a) return;
            e.preventDefault();
            openTab(a.dataset.auth || 'login');
        });

        // 초기 오픈: ?auth 또는 flash authTab
        const params = new URLSearchParams(location.search);
        const qAuth  = params.get('auth');    // join | login
        const fAuth  = '${authTab}';          // join | login
        if (qAuth === 'join' || qAuth === 'login') openTab(qAuth);
        else if (fAuth === 'join' || fAuth === 'login') openTab(fAuth);
    })();
</script>
