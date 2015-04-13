<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- LAUNCHPAGE -->

<div id="launchpage">
  <div id="flipper">
    <div class="FRONTCARD">
      <div id="piggy"></div>
      <div id="logotext"></div>
      <div class="flipinfo" id="info"></div>
      <div id="secondenter"></div>
      <div id="preloader"></div>
      <div id="wrapper">
        <!-- LOG IN FORMS -->
        <div id="cube">
          <form action="/user/login" id="auth" method="post" autocomplete="off">
            <div class="side" id="side1">
              <div id="backlogin"></div>
              <div id="enter"></div>
              <input class="frontforms" id="frontloginform" name="username" placeholder="<spring:message code="login"/>" type="text" autocomplete="off"/>
            </div>
            <div class="side" id="side2">
              <div id="backpassword"></div>
              <input class="frontforms" id="frontpasswordform" name="password" placeholder="<spring:message code="password"/>" type="password" autocomplete="off"/>
            </div>
            <input class="ghostform" name="ghauth" id="ghauth" type="text"/>
            <input type="hidden" name="remember_me" value="true"/>
          </form>
        </div>
      </div>
      <div class="fliptext"><spring:message code="orsignup"/></div>
    </div>

    <div class="BACKCARD">
      <div id="infopage">
        <span class="frominfo" id="infosubtitle"><?<spring:message code="info1"/>?></span>
        <span class="frominfo" id="infotitle"><spring:message code="info2"/></span>
        <div class="infoflipback frominfo"></div>
        <div id="infoleft" class="infocolumn frominfo">
          <div id="leftcolumn"><spring:message code="info3"/><br>
            <div class="columnimage"></div>
            <span class="columnfooter"><spring:message code="info4"/></span>
          </div>
        </div>
        <div id="infocenter"class=" infocolumn frominfo">
          <div id="centercolumn"><spring:message code="info5"/><br>
            <div class="columnimage"></div>
            <span class="columnfooter"><spring:message code="info6"/></span>
          </div>
        </div>
        <div id="inforight"class="infocolumn frominfo">
          <div id="rightcolumn"><spring:message code="info7"/><br>
            <div class="columnimage"></div>
            <span class="columnfooter"><spring:message code="info8"/></span>
          </div>
        </div>
        <div id="infoline"></div>
        <div id="infolinetext" class="frominfo"><spring:message code="info9"/></div>
        <form action="../demo" method="post" autocomplete="off">
          <button class="demobutton"><spring:message code="info10"/></button>
        </form>
        <a id="infofooter" href="https://github.com/sqshq/PiggyMetrics">&copy; 2015 sqshq.com</a>
        <a id="iconsfooter" href="/assets/pages/attribution.html">icons attribution</a>
      </div>
      <div id="regpage">
        <div id="franklin">
          <div class="avatar"></div>
        </div>
        <div id="upload-wrapper"><div id="plusavatar"></div>
          <div align="center">
            <form action="../user/upload" method="post" enctype="multipart/form-data" id="uploadButton" autocomplete="off">
              <%--Чтобы включить загрузку картинок, добавь type="file" в инпут:--%>
              <div class="inputWrapper"><input class="fileInput" name="ImageFile" id="imageInput"/></div>
            </form>
            <div id="output"></div>
          </div>
        </div>
        <div id="createaccount"></div>
        <div class="fliptext"><spring:message code="orlogin"/></div>
        <div id="registrationforms">
          <!-- SIGN UP FORMS -->
          <form action="../user/register" id="signup" method="post" autocomplete="off">
            <input class="backforms" name="username" id="backloginform" placeholder="<spring:message code="chooselogin"/>" type="text"/><br>
            <input class="backforms" name="password" id="backpasswordform" placeholder="<spring:message code="password"/>" type="password"/>
            <input class="ghostform" name="ghreg" type="text"/>
            <button class="regbutton" type="submit" name="registration"><spring:message code="register"/></button>
          </form>
        </div>
        <div id="mailform">
          <form action="../user/save/email" id="mail" method="post" autocomplete="off">
            <input name="email" id="backmailform" placeholder=<spring:message code="email"/> type="text"/><br>
            <button class="mailbutton" type="submit" name="mailbutton"><spring:message code="remind"/></button>
          </form>
					<span class="mailforminfo"><spring:message code="success"/><br>
					<span class="mailforminfosmall"><spring:message code="remind.text"/></span></span>
          <div id="skipmail"><a href="#"><spring:message code="step.skip"/></a></div>
        </div>
      </div>
    </div>
  </div>
</div>