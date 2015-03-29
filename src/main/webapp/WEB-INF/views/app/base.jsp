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

${custom}

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
  var savePermit = ${demo_mode ? "false" : "true"};
  jsonParse('{ "usd":"60.0000", "eur":"70.0000", "login":"sqshq", "lastVisit":"21/03/2015", "avatar":"default.jpg", "checkedCurr":"usd", "lastCurr":"usd", "checkedPercent":"1.00", "notes":"ТИНЬКОФФ Мой: примерно до 10.2015, сейчас ($) 8 029,63 РАЙФФАЙЗЕН Санин, до 20.10.15, на декабрь 2014 - 620 000 р. == БУФЕР НАЛИЧНЫМИ: 80 000 р. + 10 760 $ + 7 390 EURO == Долларовый вклад ТКС: По окончанию вклада их можно будет перевести на дебетовую карту или в любой банк мира под тоже имя Свифтом, комиссия 15 баксов == Райффайзен акции (при снятии -13% НДФЛ, раньше года - (1% - 4% в зависимости от срока) == 09.14 - cконвертировали рублевый вклад в $ по курсу 41 10.14 - cконвертировали рублевый вклад в $ по курсу 53 22.11.14 купили 2000 евро по курсу 56.85 30.12.14 купили 5200 евро по курсу 72.10 ==", "freeMoney":"39475", "deposit":"1", "capitalization":"0", "percent":"2", "data" :{ "incomes":{ "1":{ "income_id":1, "title":"МОЯ зарплата", "icon":"case", "currency":"rub", "period":"month", "value":"30000", "converted":"500.000" }, "2":{ "income_id":2, "title":"Санькина зарплата", "icon":"wallet", "currency":"rub", "period":"month", "value":"60000", "converted":"1000.000" } }, "expenses":{ "2":{ "expense_id":2, "title":"бензин", "icon":"gas", "currency":"rub", "period":"month", "value":"4000", "converted":"66.667" }, "3":{ "expense_id":3, "title":"коммуналка", "icon":"utilities", "currency":"rub", "period":"month", "value":"2500", "converted":"41.667" }, "4":{ "expense_id":4, "title":"Еда", "icon":"meal", "currency":"rub", "period":"month", "value":"10000", "converted":"166.667" }, "6":{ "expense_id":6, "title":"Отпуск (европа)", "icon":"earth", "currency":"rub", "period":"year", "value":"80000", "converted":"111.111" }, "7":{ "expense_id":7, "title":"в Волгодонск", "icon":"island", "currency":"rub", "period":"year", "value":"18000", "converted":"25.000" }, "8":{ "expense_id":8, "title":"спортзал", "icon":"sport", "currency":"rub", "period":"year", "value":"19000", "converted":"26.389" }, "9":{ "expense_id":9, "title":"прочее", "icon":"clothes", "currency":"rub", "period":"month", "value":"4000", "converted":"66.667" }, "10":{ "expense_id":10, "title":"Аренда", "icon":"home", "currency":"rub", "period":"month", "value":"15000", "converted":"250.000" } } } }');
  <%--${user_data ? user_data : "false"}--%>
</script>

</body>
</html>