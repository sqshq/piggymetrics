<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>

<html>
<head>
    <title>Test Page</title>
</head>
<body>

<c:if test="${pageContext.request.userPrincipal.name != null}">
    <h3>
        Welcome : ${pageContext.request.userPrincipal}
    </h3>
</c:if>

<div>
    <input type="button" id="loginButton" value="Log in!"/>
</div>

<div>
    <input type="button" id="logoutButton" value="Log out!"/>
</div>


<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
<script type="text/javascript">

    $('#loginButton').click(function(){

        $.ajax({
            type:"POST",
            url: "/auth/login_check",
            data: {
                remember_me: true,
                username: "sqshq",
                password: "121314"
            }
        });
    });

    $('#logoutButton').click(function(){
        $.post(
                "/j_spring_security_logout",
                {
                    "${_csrf.parameterName}": "${_csrf.token}"
                },
                onAjaxSuccess
        );
    });



    function onAjaxSuccess(data)
    {
        console.log(data);
    }
</script>


</body>
</html>