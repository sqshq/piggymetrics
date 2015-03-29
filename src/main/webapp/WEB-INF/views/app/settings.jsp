<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- SETTINGS PAGE -->

<div id="settingspage">
    <div id="swipefield"></div>

    <div id="incomes" class="columns"><div class="incomes-sprite-title zoomplus incomebutton"></div>
        <div id="noincomes" class="incomebutton">
            <div class="hoverplace"></div>
            <div class="majorplusitem">
                <div class="plusitemborder"></div>
                <div class="plusitemone"></div>
                <div class="plusitemtwo"></div>
            </div>
            <span class="plusitemtitle"><spring:message code="addincome"/></span>
            <span class="plusitemitalic"><spring:message code="empty"/></span>
        </div>
        <div class="plusitem" id="incomesplusitem">
            <div class="plusitemborder incomebutton"></div>
            <div class="plusitemone"></div>
            <div class="plusitemtwo"></div>
        </div>
        <div class="blueline"></div>
        <div class="brownline"></div>
        <div id="incomedown"><div class="arrowup"></div><spring:message code="forward"/></div>
        <div id="incomeup"><div class="arrowdown"></div><spring:message code="back"/></div>
        <div class="frame">
            <div id="incomewrapper" class="wrapper">
                <div id="incomeslider"></div>
            </div>
        </div>
    </div>
    <div id="expenses" class="columns"><div class="expenses-sprite-title zoomplus expensebutton"></div>
        <div id="noexpenses" class="expensebutton">
            <div class="hoverplace"></div>
            <div class="majorplusitem">
                <div class="plusitemborder expensebutton"></div>
                <div class="plusitemone"></div>
                <div class="plusitemtwo"></div>
            </div>
            <span class="plusitemtitle"><spring:message code="addexpense"/></span>
            <span class="plusitemitalic"><spring:message code="empty"/></span>
        </div>
        <div class="plusitem" id="expensesplusitem">
            <div class="plusitemborder"></div>
            <div class="plusitemone"></div>
            <div class="plusitemtwo"></div>
        </div>
        <div class="blueline"></div>
        <div class="brownline"></div>
        <div id="expensedown"><div class="arrowup"></div><spring:message code="forward"/></div>
        <div id="expenseup"><div class="arrowdown"></div><spring:message code="back"/></div>
        <div class="frame">
            <div id="expensewrapper" class="wrapper">
                <div id="expenseslider"></div>
            </div>
        </div>
    </div>
    <div id="savings" class="columns"><div class="savings-sprite-title"></div>
        <div class="blueline"></div>
        <div class="brownline"></div>
        <div id="topsavingsline">
            <div id="piggyicon"></div>
            <span class="savingstitle14"><spring:message code="mysparemoney"/></span>
            <span class="savingstitle12"><spring:message code="atthemoment"/></span>
            <div id="rublebox"></div><div id="rublesign"></div>
            <input type="text" name="savingsvalue" id="savingsvalue" data-a-sep=" " data-v-min="0" data-v-max="999999999" data-l-zero="deny" maxlength="12" autocomplete="off">
        </div>
        <div id="bottomsavingsline">
            <input type="checkbox" name="deposit" id="deposit">
            <label for="deposit" class="button"></label>
            <label for="deposit" class="savingsdeposit"><spring:message code="savingsondeposit"/></label>
            <span class="savingspercent"><spring:message code="interest"/></span>
            <input type="text" name="percentvalue" id="percentvalue" data-a-sep=""  data-v-min="0.00" data-v-max="99.99" data-l-zero="deny" data-a-sign=" %" data-w-empty="sign" data-p-sign="s" maxlength="5" data-a-pad="false" size="6" data-a-dec="." autocomplete="off">
            <input type="checkbox" name="capitalization" id="capitalization">
            <label for="capitalization" class="button"></label>
            <label for="capitalization" class="savingscapital"><spring:message code="capitaliztion"/></label>
        </div>
    </div>
    <div id="savebutton"><div id="leftborder"></div><div id="centerborder"></div><div id="rightborder"></div><div id="save"></div></div>
</div>

<form action="../user/save" id="saveoptions" method="post" autocomplete="off"></form>

<!-- NOTES WINDOW -->
<div id="overlay"></div>
<div id="add-notes">
    <div class="modal-content">
        <div class="notes-title"><spring:message code="notes"/></div>
        <div class="modal-close"><div class="close-sign"></div></div>
        <div class="notes-save"><div class="modal-save-icon"></div><div class="modal-save-text"><spring:message code="save"/></div></div>
        <textarea id="notes" class="notes-input" autocomplete="off" maxlength="5000"></textarea>
    </div>
</div>
<!-- MODAL WINDOWS -->

<div id="add-modal">
    <div class="modal-content">
        <div class="modal-title mainmodaltitle"></div>
        <div class="modal-close"><div class="close-sign"></div></div>
        <input type="text" name="modalvalue" class="modalvalue" data-a-sep=" " data-v-min="0" data-v-max="999999999" data-l-zero="deny" maxlength="12" autocomplete="off" value="0">
        <div class="modalselects">
            <select class="modalcurrency">
                <option selected="selected" value="rub"><spring:message code="rub"/></option>
                <option selected="selected" value="doll"><spring:message code="doll"/></option>
                <option value="euro"><spring:message code="euro"/></option>
            </select>

            <select class="modalperiod">
                <option value="year"><spring:message code="peryear"/></option>
                <option value="quarter"><spring:message code="perquarter"/></option>
                <option selected="selected" value="month"><spring:message code="permonth"/></option>
                <option value="day"><spring:message code="perday"/></option>
                <option value="hour"><spring:message code="perhour"/></option>
            </select>
        </div>
        <div class="initicons"><div id="chooseicon" class="cart-icon"></div><div class="initicons-arrow"></div></div>
        <input class="modaltitle" placeholder=<spring:message code="title"/> type="text" autocomplete="off" maxlength="18"/>
        <div class="modal-save"><div class="modal-save-icon"></div><div class="modal-save-text"><spring:message code="save"/></div></div>
        <div class="modal-delete"><div id="modaldeletecross"></div><div class="modal-delete-text"><spring:message code="remove"/></div></div>

        <!-- Expenses table -->
        <div class="modalexpensessurface">
            <div class="modal-title"><spring:message code="chooseicon"/></div>
            <table class="modaltable modalforward" id="modalexpensetable">
                <tr>
                    <td><div class="imgbox"><div class="iconbox auto"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox gas"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox home"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox baby"></div></div></td>
                </tr>
                <tr>
                    <td><div class="imgbox"><div class="iconbox cart"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox clothes"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox phone"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox utilities"></div></div></td>
                </tr>
                <tr>
                    <td><div class="imgbox"><div class="iconbox island"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox earth"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox meal"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox sport"></div></div></td>
                </tr>
                <tr>
                    <td><div class="imgbox"><div class="iconbox medical"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox tv"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox smoking"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox other"></div></div></td>
                </tr>
            </table>
        </div>

        <!-- Incomes table -->
        <div class="modalincomessurface">
            <div class="modal-title"><spring:message code="chooseicon"/></div>
            <table class="modaltable modalforward" id="modalincomestable">
                <tr>
                    <td><div class="imgbox"><div class="iconbox edu"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox graphs"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox wallet"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox case"></div></div></td>
                </tr>
                <tr>
                    <td><div class="imgbox"><div class="iconbox rub"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox euro"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox doll"></div></div></td>
                    <td><div class="imgbox"><div class="iconbox gbp"></div></div></td>
                </tr>
            </table>
        </div>

    </div>
</div>