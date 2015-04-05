<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<htmL>
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8" />
  <meta name="viewport" content="width=1150, user-scalable=no">
  <title>Piggy Metrics</title>
  <style>
    #bubble, #indicator, #save, #piggyicon, #rublesign, .arrowup, .arrowdown, .incomes-sprite-title, .close-sign, .modal-save-icon, #modaldeletecross, .initicons-arrow, .expenses-sprite-title, .savings-sprite-title, .triangle, .noUi-handle, .noUi-handle:after  {
      background-image: url('/assets/images/<spring:message code="sprites"/>');
      background-size: 538px 84px;
    }
    @media only screen and (-webkit-min-device-pixel-ratio: 1.5),
    only screen and (min-resolution: 144dpi) {
      #bubble, #indicator, #save, #piggyicon, #rublesign, .arrowup, .arrowdown, .incomes-sprite-title, .close-sign, .modal-save-icon, #modaldeletecross, .initicons-arrow, .expenses-sprite-title, .savings-sprite-title, .triangle, .noUi-handle, .noUi-handle:after {
        background-image: url('/assets/images/<spring:message code="sprites@2x"/>');
      }
    }
  </style>
  <link rel="stylesheet" type="text/css" href="/assets/css/launch.css">
  <link rel="stylesheet" type="text/css" href="/assets/css/style.css">
  <link rel="stylesheet" type="text/css" href="/assets/css/animation.css">
</head>
<body>

<div id="settings_hat"></div>

<div id="lastlogo">
  <div id="lastlogoflipper">
    <div id="logo_settings">
      <div id="logoclickplace"></div><div id="bubble"><div id="indicator"></div></div>
    </div>
    <div id="logo_statistic"></div>
  </div>
</div>

<section class="toppage">
  <jsp:include page="greeting.jsp"/>
  <jsp:include page="settings.jsp"/>
</section>

<section class="bottompage">
  <jsp:include page="dashboard.jsp"/>
</section>

<c:if test="${authorized ? false : true}">
  <jsp:include page="launchpage.jsp"/>
</c:if>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
<script src="/assets/js/touchscreens.js"></script>
<script src="/assets/js/extrascripts.js"></script>
<script src="/assets/js/translation.js"></script>

<script src="assets/js/${authorized ? "launch.js" : "unauth_launch.js"}"></script>
<script src="/assets/js/dashboard.js" charset="utf-8"></script>
<script src="/assets/js/base.js" charset="utf-8"></script>

<script type="text/javascript">
  var savePermit = ${demo ? "false" : "true"};
  jsonParse(${user});
</script>

</body>
</html>