<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- DASHBOARD -->

<div id="mainblock">
    <div id="setcurrency">
        <div id="rubcurr" class="currunchecked">Руб</div>
        <div id="eurcurr" class="currunchecked">Eur</div>
        <div id="usdcurr" class="currunchecked">Usd</div>
    </div>
    <div id="expense-cursor">
        <div class="cursorline"></div>
        <div class="cursorpoint"></div>
    </div>
    <div id="incomes-cursor">
        <div class="cursorline"></div>
        <div class="cursorpoint"></div>
    </div>
    <div id="expenses-lines-container"></div>
    <div id="incomes-lines-container"></div>

    <div id="deltatitle" class="flag"><spring:message code="inc-exp"/><div class="triangle"></div></div>
    <div id="savingstitle" class="flag"><spring:message code="savings"/><div class="triangle"></div></div>

    <div id="outermaindiv"> <div class="linesbackground"></div>
        <div id="outermaincursordiv"> <input class="knob" id="outer-circle-cursor"/> </div>
        <input class="knob" id="outer-circle"/>
        <div class="topmaincircletitle lightcircletitle"></div>
        <div id="outer-circle-value" class="boldcircletitle"></div>
        <div class="bottommaincircletitle"><span class="boldcircletitle curr"></span><span class="lightcircletitle"><spring:message code="/month"/></span></div>
        <div id="innermaindiv"> <input class="knob" id="inner-circle"/> </div>
        <div id="maincircleline"></div>
        <div id="maincircle100percent"><span class="lightcircletitle">100%</span></div>
        <div id="outer-circle-percent"><span class="lightcircletitle"></span></div>
    </div>

    <div id="small-circles-container">
        <div id="firstcirclediv">
            <div id="firstcircledivflipper">
                <div class="frontcircle"> <div class="linesbackground"></div>
                    <input class="knob" id="first-circle" />
                    <div class="cursordiv"> <input class="knob" id="first-circle-cursor"/></div>
                    <div id="first-circle-title" class="circletoptitle lightcircletitle"></div>
                    <div id="first-circle-value" class="circlevalue boldcircletitle"></div></span>
                    <div class="bottomcircletitle"><span class="boldcircletitle curr"></span><span class="lightcircletitle"><spring:message code="/hour"/></span></div>
                    <div id="first-circle-percent"><span class="lightcircletitle"></span></div>
                    <div class="circleline"></div>
                    <div class="circle100percent"><span class="lightcircletitle">100%</span></div>
                </div>
                <div class="backcircle">
                    <canvas id="movingcircle-1" width="200" height="200"></canvas>
                    <div class="circletoptitle lightcircletitle"><spring:message code="chooseanyposition"/></div>
                    <div class="circlesselect">
                        <select id="circle-select-1" class="selectcircles"></select>
                    </div>
                    <div id="circle-select-1-back"></div>
                </div>
            </div>
        </div>

        <div id="secondcirclediv">
            <div id="secondcircledivflipper">
                <div class="frontcircle"><div class="linesbackground"></div>
                    <input class="knob" id="second-circle" />
                    <div class="cursordiv"><input class="knob" id="second-circle-cursor"/></div>
                    <div id="second-circle-title" class="circletoptitle lightcircletitle"></div>
                    <div id="second-circle-value" class="circlevalue boldcircletitle"></div></span>
                    <div class="bottomcircletitle"><span class="boldcircletitle curr"></span><span class="lightcircletitle"><spring:message code="/day"/></span></div>
                    <div id="second-circle-percent"><span class="lightcircletitle"></span></div>
                    <div class="circleline"></div>
                    <div class="circle100percent"><span class="lightcircletitle">100%</span></div>
                </div>
                <div class="backcircle">
                    <canvas id="movingcircle-2" width="200" height="200"></canvas>
                    <div class="circletoptitle lightcircletitle"><spring:message code="chooseanyposition"/></div>
                    <div class="circlesselect">
                        <select id="circle-select-2" class="selectcircles"></select>
                    </div>
                    <div id="circle-select-2-back"></div>
                </div>
            </div>
        </div>

        <div id="thirdcirclediv">
            <div id="thirdcircledivflipper">
                <div class="frontcircle"> <div class="linesbackground"></div>
                    <div class="cursordiv"> <input class="knob" id="third-circle-cursor"/> </div>
                    <div id="third-circle-title" class="circletoptitle lightcircletitle"></div>
                    <div id="third-circle-value" class="circlevalue boldcircletitle"></div></span>
                    <div class="bottomcircletitle"><span class="boldcircletitle curr"></span><span class="lightcircletitle"><spring:message code="/year"/></span></div>
                    <div id="third-circle-percent"><span class="lightcircletitle"></span></div>
                    <div class="circleline"></div>
                    <div class="circle100percent"><span class="lightcircletitle">100%</span></div>
                    <input class="knob" id="third-circle" />
                </div>
                <div class="backcircle">
                    <canvas id="movingcircle-3" width="200" height="200"></canvas>
                    <div class="circletoptitle lightcircletitle"><spring:message code="chooseanyposition"/></div>
                    <div class="circlesselect">
                        <select id="circle-select-3" class="selectcircles"></select>
                    </div>
                    <div id="circle-select-3-back"></div>
                </div>
            </div>
        </div>

        <div id="firstpendant">
            <div class="pendant-circle"></div>
            <span class="lightcircletitle pendantfont"><spring:message code="perhour"/></span>
            <div class="pendantline"></div>
        </div>
        <div id="secondpendant">
            <div class="pendant-circle"></div>
            <span class="lightcircletitle pendantfont"><spring:message code="perday"/></span>
            <div class="pendantline"></div>
        </div>
        <div id="thirdpendant">
            <div class="pendant-circle"></div>
            <span class="lightcircletitle pendantfont"><spring:message code="peryear"/></span>
            <div class="pendantline"></div>
        </div>
    </div>

    <div id="savings-slider-container">
        <span class="savings-slider"><spring:message code="savingspart"/></span>
        <div id="savings-slider"></div>
        <div id="savingsTip0"><spring:message code="leftcorner"/></div>
        <div id="savingsTip100"><spring:message code="rightcorner"/></div>
    </div>

    <div id="savingscircles">
        <div id="after-savings">
            <div class="savings-circle-title lightcircletitle"><spring:message code="after12month"/></div>
            <div id="after-savings-value" class="boldcircletitle"></div></span>
            <div class="savings-circle-currency boldcircletitle"></div>
        </div>
        <div id="before-savings">
            <div class="savings-circle-title lightcircletitle"><spring:message code="now"/></div>
            <div id="before-savings-value" class="boldcircletitle"></div></span>
            <div class="savings-circle-currency boldcircletitle"></div>
        </div>
    </div>

    <div id="savingschart">
        <canvas id="horizontal"></canvas>
        <canvas id="chartline"></canvas>
        <div id="month0"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"></div></div>
        <div class="months"><div id="month1" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month2" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month3" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month4" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month5" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month6" class="in-month"><div class="small-month-circle"></div><div class="large-month-circle"><div class="bluedot"></div></div><div class="month-name"></div><div class="large-month-val"><span class="val"></span> <span class="curr"></span></div><div class="month-val"></div></div></div>
        <div class="months"><div id="month7" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month8" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month9" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month10" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month11" class="in-month"><div class="small-month-circle"></div><div class="month-name"></div><div class="month-val"><span class="val"></span> <span class="curr"></span></div></div></div>
        <div class="months"><div id="month12" class="in-month"><div class="small-month-circle"></div><div class="large-month-circle"><div class="bluedot"></div></div><div class="month-name"></div><div class="large-month-val"><span class="val"></span> <span class="curr"></span></div><div class="month-val"></div></div></div>
    </div>
</div>
