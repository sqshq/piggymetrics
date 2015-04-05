<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- GREETING PAGE -->

<div id="logo_greeting"></div>
<div id="centerbox">
  <div id="avatarcontainer" class="forward plus">
    <div class="avatar"></div>
  </div>
</div>
<div id="lefttitle"><span class="bluetext"> metrics</span></div>
<div id="righttitle"><span class="bluetext">last seen: </span></div>
<div id="bottombuttons">
  <div id="plus" class="plus">
    <div id="plusborder">
      <div id="plusone"></div>
      <div id="plustwo"></div>
    </div>
    <div id="plustext"><spring:message code="getin"/></div>
  </div>
  <form action="/j_spring_security_logout" id="logout" method="post" autocomplete="off">
    <div id="minus">
      <div id="minusborder">
        <div id="minusone"></div>
      </div>
      <div id="minustext"><spring:message code="exit"/></div>
    </div>
  </form>
</div>