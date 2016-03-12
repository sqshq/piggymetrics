var user = {},
    savings = {},
    incomes = {},
    expenses = {};

function initAccount(account) {
    user = new User(account.name, account.lastSeen, account.saving.currency, account.note);
    savings = new Savings (account.saving.amount, account.saving.deposit, account.saving.capitalization, account.saving.interest);

    if (account.incomes) {
        for (i = 0; i < account.incomes.length; i++) {
            AddIncome(i + 1, account.incomes[i].title, account.incomes[i].icon, account.incomes[i].currency, account.incomes[i].period, account.incomes[i].amount);
        }
    }

    if (account.expenses) {
        for (j = 0; j < account.expenses.length; j++) {
            AddExpense(j + 1, account.expenses[j].title, account.expenses[j].icon, account.expenses[j].currency, account.expenses[j].period, account.expenses[j].amount);
        }
    }
}

function User(username, lastSeen, currency, note) {

    var seen = new Date(lastSeen);

    this.login = username;
    this.lastSeen = (seen.getMonth() + 1) + "/" + seen.getDate()  + "/" + seen.getFullYear();;
    this.checkedCurr = currency;
    this.lastCurr = currency;
    this.checkedPercent = 1;
    this.notes = note;
}

function Savings(money, deposit, capitalization, interest) {
    this.freeMoney = money;
    this.deposit = deposit;
    this.capitalization = capitalization;
    this.percent = interest;
}

function AddIncome(income_id, title, icon, currency, period, amount){
    incomes[income_id] = {
        income_id: income_id,
        title: title,
        icon: icon,
        currency: currency,
        period: period,
        amount: amount.toString()
    }
}

function AddExpense(expense_id, title, icon, currency, period, amount){
    expenses[expense_id] = {
        expense_id: expense_id,
        title: title,
        icon: icon,
        currency: currency,
        period: period,
        amount: amount.toString()
    }
}

// Log out button
$('#minus').click(function() {
    logout();
});

var entityMap = {
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': '&quot;',
    "'": '&#39;',
    "/": '&#x2F;'
};

function escape(string) {
    return String(string).replace(/[&<>"'\/]/g, function (s) {
        return entityMap[s];
    });
}

function sanitize(obj) {

    $.each( obj, function( index, item ){
        $.each( item, function( key, value ){
            obj[index][key] = escape(value);
        });
    });

    return obj;
}

function initGreetingPage() {

    $("#preloader, #lastlogo").show();
    $("#loginpage").fadeOut(50);
    $(".avatar").css({"background": "url(images/userpic.jpg) center center no-repeat", "background-size": "100% 100%"});
    $("#logo_greeting").fadeIn(0, function() {
        $("#centerbox").show(0,function() {
            setTimeout( function() { showGreetingUnits() } , 300)
        });
    });

    $(".plus").click(function() {
        setTimeout(initSettingsPage, 200);
    });

    // Init statisctic page first time
    expensesSumMonth = 0; incomesSumMonth = 0;
    $("#circle-select-1, #circle-select-2, #circle-select-3").empty();
    getConverted(incomes);
    getConverted(expenses);

    initStatisticPage();
    setTimeout(function() { initSavingsCircles(user.checkedPercent, 0.2, savings.freeMoney, savings.freeMoney, 700) }, 200);
    initSavingsSlider();
    $("#savings-slider").data({"checkedPercent": user.checkedPercent});

    $("#lefttitle").prepend(escape(user.login));
    $("#righttitle").append(user.lastSeen);

    // Fill data on settings page beforehand
    addSavings();
    addItems();
    addNotes();
}

function initSettingsPage() {
    switch (user.checkedCurr) {
        case "RUB": $("#rublesign").css({"background-position": "-150px 0"});
            break;
        case "EUR": $("#rublesign").css({"background-position": "-386px 0"});
            break;
        case "USD": $("#rublesign").css({"background-position": "-354px 0"});
            break;
    }

    $("#settings_hat").show();
    $("#avatarcontainer").addClass("reverse");
    $("#overlay, .modal-content").show(); // Fix transition and visibility: hidden Chrome issue
    $(".modalvalue").autoNumeric("init");
    $(".modalcurrency, .modalperiod, .selectcircles").selectbox();
    hideGreetingUnits();
    setTimeout( function() { $("#logo_greeting").hide(); $("#logo_settings").show(); $("#avatarcontainer").fadeOut(100); drawChartLine(91); } , 290);
    setTimeout( function() { $("#settingspage").fadeIn(100); } , 700);
    setTimeout( function() { $("#expenseslider").fadeIn(100); $("#bubble").fadeIn(500); } , 800);
}
function greetingPageAgain() {
    $("#avatarcontainer").removeClass().addClass("forward plus");
    $(".avatar").css({"background": "url(images/userpic.jpg) center center no-repeat", "background-size": "100% 100%"});
    $("#logo_greeting").fadeIn(0, function() {
        $("#centerbox, #avatarcontainer").show(0,function() {
            setTimeout( function() { showGreetingUnits(); } , 300);
        });
    });

    /* Initiate settings page on press plus or enter
     $(document).on("keyup", function (e) {
     if (e.which == 13) {
     setTimeout(initSettingsPage, 400);
     }
     });
     */

    $(".plus").click(function() {
        setTimeout(initSettingsPage, 200);
    });
    $("#righttitle, #lefttitle").empty();
    $("#righttitle").append('<span class="bluetext">last seen: </span>' + user.lastSeen);
    $("#lefttitle").append(escape(user.login) + '<span class="bluetext"> metrics</span>');
}
function showGreetingUnits() {
    $("#lefttitle").fadeIn(500);
    $("#righttitle").fadeIn(500);
    $("#bottombuttons").fadeIn(500);
}
function hideGreetingUnits() {
    $("#lefttitle").fadeOut(100);
    $("#righttitle").fadeOut(100);
    $("#bottombuttons").fadeOut(100);
}

// Filling user notes
function addNotes() {
    $("textarea#notes").val(user.notes);
}

// Filling Savings column
function addSavings() {
    switch (savings.deposit) {
        case true: $("#deposit").prop('checked', true);
            break;
    }
    switch (savings.capitalization) {
        case true: $("#capitalization").prop('checked', true);
            break;
    }
    $("#savingsvalue").val(savings.freeMoney);
    $("#percentvalue").val(savings.percent);
    $("#savingsvalue").autoNumeric("init");
    $("#percentvalue").autoNumeric("init");
    moveRuble();
}

// Filling Incomes and Expenses columns
function addItems() {
    Object.keys(incomes).forEach(function(key) {
        var value = incomes[key].amount.replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 </span><span class="lightdigit20">');
        $("#incomeslider").append('<div onclick="itemClick(this)" class="incomeitem" id="income-' + incomes[key].income_id + '"><span class="title11museo300">' + incomes[key].title + '</span><p class="title9museo300"><span class="bolddigit20">' + value + ' </span>' + checkCurrency(incomes[key].currency) + checkPeriod(incomes[key].period) + '</span></p><div class="itembackground"></div></div>');
        $("#income-" + incomes[key].income_id).data({"id": incomes[key].income_id, "icon": incomes[key].icon, "amount": incomes[key].amount, "title": incomes[key].title, "currency": incomes[key].currency ,"period": incomes[key].period}).children("div").addClass(incomes[key].icon);
    });
    Object.keys(expenses).forEach(function(key) {
        var value = expenses[key].amount.replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 </span><span class="lightdigit20">');
        $("#expenseslider").append('<div onclick="itemClick(this)" class="expenseitem" id="expense-' + expenses[key].expense_id + '"><span class="title11museo300">' + expenses[key].title + '</span><p class="title9museo300"><span class="bolddigit20">' + value + ' </span>' + checkCurrency(expenses[key].currency) + checkPeriod(expenses[key].period) + '</span></p><div class="itembackground"></div></div>');
        $("#expense-" + expenses[key].expense_id).data({"id": expenses[key].expense_id, "icon": expenses[key].icon, "amount": expenses[key].amount, "title": expenses[key].title, "currency": expenses[key].currency ,"period": expenses[key].period}).children("div").addClass(expenses[key].icon);
    });

    // Show big ADD ITEM button when column is empty
    checkSlidersLength();

    // Markup changes according to number of column items
    itemsPosition("expense");
    itemsPosition("income");
}

// According to number of column items - show/hide up&down buttons and change position:absolute
function itemsPosition(transaction) {
    if ($("div#" + transaction + "slider").children().length <= 4) {
        $("#" + transaction + "slider").css({"position": "relative"});
        $("#" + transaction + "down, #" + transaction + "up").hide();
    }
    else {
        $("#" + transaction + "slider").css({"position": "absolute"});
        $("#" + transaction + "down, #" + transaction + "up").show();
    }
}

function checkSlidersLength() {
    switch ($("div#incomeslider").children().length) {
        case 0: $("#noincomes").show();
            $("#incomesplusitem").hide();
            break;
        default:
            $("#noincomes").hide();
            $("#incomesplusitem").show();
            break;
    }
    switch ($("div#expenseslider").children().length) {
        case 0: $("#noexpenses").show();
            $("#expensesplusitem").hide();
            break;
        default:
            $("#noexpenses").hide();
            $("#expensesplusitem").show();
            break;
    }
}

function checkPeriod(period) {
    var periodText;
    switch (period) {
        case "YEAR":
            periodText = " / per year";
            break;
        case "QUARTER":
            periodText = " / per quater";
            break;
        case "MONTH":
            periodText = " / per month";
            break;
        case "DAY":
            periodText = " / per day";
            break;
        case "HOUR":
            periodText = " / per hour";
            break;
    }
    return periodText
}

function checkCurrency(currency) {
    var currencyText;
    switch (currency) {
        case "RUB": currencyText="rub.";
            break;
        case "USD": currencyText="$";
            break;
        case "EUR": currencyText="&euro;";
            break;
    }
    return currencyText
}

// SLIDE COLUMNS UP AND DOWN

// Eliminate the frequent calling slide functions
function debounce(f, ms) {
    var state = null;
    var cooldown = 1;
    return function() {
        if (state) return;
        f.apply(this, arguments);
        state = cooldown;
        setTimeout(function() { state = null }, ms);
    }
}

Up 	 = debounce(Up, 300);
Down = debounce(Down, 300);

initStatisticPage 	= debounce(initStatisticPage, 900);
launchStatistic 	= debounce(launchStatistic, 2400);
fadeStatistic 		= debounce(fadeStatistic, 1900);
jsonDataSave 		= debounce(jsonDataSave, 1800);
startOfExpenseList 	= debounce(startOfExpenseList, 500);
endOfExpenseList 	= debounce(endOfExpenseList, 500);
startOfIncomeList 	= debounce(startOfIncomeList, 500);
endOfIncomeList 	= debounce(endOfIncomeList, 500);
initGreetingPage 	= debounce(initGreetingPage, 2000);

// Slide up
function Up(transaction) {
    var length = $("#" + transaction + "slider").children().length;
    var itemWidth = 71;
    if (Math.abs($("#" + transaction + "slider").position().top % itemWidth) > 0.01) return;
    else if ($("#" + transaction + "slider").position().top > -(length-4)*itemWidth) {
        var pixels=$("#" + transaction + "slider").position().top + (length-5)*itemWidth;
        var slide = "translateY("+ pixels + "px)";
        $("#" + transaction + "slider").css({
            "-webkit-transform": slide,
            "-moz-transform": slide,
            "-o-transform": slide,
            "-ms-transform": slide,
            "transform": slide
        });
    }
    else {
        if (transaction == "expense") {
            endOfExpenseList(transaction);
            setTimeout(endOfExpenseList, 500);
        }
        else {
            endOfIncomeList(transaction);
            setTimeout(endOfIncomeList, 500);
        }
    }
}
// Slide down
function Down(transaction) {
    var length = $("div#" + transaction + "slider").children().length;
    var itemWidth = 71;
    if (Math.abs($("#" + transaction + "slider").position().top % itemWidth) > 0.01) return;
    else if ($("#" + transaction + "slider").position().top < -20) {
        var pixels=$("#" + transaction + "slider").position().top + (length-3)*itemWidth;
        var slide = "translateY("+ pixels + "px)";
        $("#" + transaction + "slider").css({
            "-webkit-transform": slide,
            "-moz-transform": slide,
            "-o-transform": slide,
            "-ms-transform": slide,
            "transform": slide
        });
    }
    else {
        if (transaction == "expense") {
            startOfExpenseList(transaction);
            setTimeout(startOfExpenseList, 530);
        }
        else {
            startOfIncomeList(transaction);
            setTimeout(startOfIncomeList, 530);
        }
    }
}

// Bounce end of list functions
function startOfExpenseList(transaction) {
    $("#expensewrapper").toggleClass("startoflist");
}
function endOfExpenseList(transaction) {
    $("#expensewrapper").toggleClass("endoflist");
}
function startOfIncomeList(transaction) {
    $("#incomewrapper").toggleClass("startoflist");
}
function endOfIncomeList(transaction) {
    $("#incomewrapper").toggleClass("endoflist");
}

// MODAL WINDOWS FUNCTIONS

// ADD ITEMS
function addNewItem() {
    var itemTitle = $(".modaltitle").val(),
        itemIcon = $(".initicons").data("iconselected"),
        itemCurrency = $(".modalcurrency").val(),
        itemPeriod = $(".modalperiod").val(),
        itemValue = $(".modalvalue").autoNumeric("get"),
        itemId = 0;
    if (checkModalFields(itemValue, itemTitle)) { // If inputs are proper filled, add incomes or expenses
        if ($(".initicons").data ("incomes-expenses") == "incomes") {
            Object.keys(incomes).forEach(function(keys) {	if (incomes[keys].income_id > itemId) {	itemId = (incomes[keys].income_id)	}});
            AddIncome(++itemId, itemTitle, itemIcon, itemCurrency, itemPeriod, itemValue);
            addNewDiv("income", itemId, itemTitle, itemIcon, itemCurrency, itemPeriod, itemValue);
        }
        else {
            Object.keys(expenses).forEach(function(keys) {	if (expenses[keys].expense_id > itemId) {	itemId = (expenses[keys].expense_id)	}});
            AddExpense(++itemId, itemTitle, itemIcon, itemCurrency, itemPeriod, itemValue);
            addNewDiv("expense", itemId, itemTitle, itemIcon, itemCurrency, itemPeriod, itemValue);
        }
        turnOffModal();
    }
    checkSlidersLength();
    itemsPosition("expense");
    itemsPosition("income");
    setTimeout(function() { runConvert() }, 230);
}
function addNewDiv(whichColumn, itemId, itemTitle, itemIcon, itemCurrency, itemPeriod, itemValue) {
    var value = itemValue.replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 </span><span class="lightdigit20">');
    $("#" + whichColumn + "slider").append('<div onclick="itemClick(this)" class="' + whichColumn + 'item" id="' + whichColumn +'-' + itemId + '"><span class="title11museo300">' + itemTitle + '</span><p class="title9museo300"><span class="bolddigit20">' + value + ' </span>' + checkCurrency(itemCurrency) + checkPeriod(itemPeriod) + '</span></p><div class="itembackground"></div></div>');
    $("#" + whichColumn + "-" + itemId).addClass("newitemadded").data({"id": itemId, "icon": itemIcon, "amount": itemValue, "title": itemTitle, "currency": itemCurrency ,"period": itemPeriod}).children("div").addClass(itemIcon);
    setTimeout(function() { $("#" + whichColumn + "-" + itemId).removeClass("newitemadded") }, 4100);
}
// EDIT ITEMS
function itemClick(item) {
    // Add delete-button
    $(".modal-delete").show();
    var itemDiv = $("#" + item.id),
        itemIcon = itemDiv.data("icon"),
        itemCurrency = itemDiv.data("currency"),
        itemPeriod = itemDiv.data("period"),
        itemValue = itemDiv.data("amount"),
        itemTitle = itemDiv.data("title"),
        itemId = itemDiv.data("id"),
        incomesExpenses = "expense",
        whichColumn = expenses;

    if (itemDiv.hasClass("incomeitem")) {
        $(".mainmodaltitle").empty().append("Change income");
        incomesExpenses = "income";
        whichColumn = incomes;
    }
    else {
        $(".mainmodaltitle").empty().append("Change expense");
    }
    $(".initicons").data({"iconselected": itemIcon, "add-edit": "edit", "incomes-expenses": incomesExpenses + "s"});
    $("#chooseicon").removeClass().addClass(itemIcon);
    $(".modalvalue").show().autoNumeric("set", itemValue);
    $(".modaltitle").show().val(itemTitle);
    $(".modalcurrency").val(itemCurrency);
    $(".modalperiod").val(itemPeriod);
    $(".modalcurrency, .modalperiod").trigger("refresh");
    $("#overlay, #add-modal").addClass("modal-show");
    setTimeout(function() { $('.modalvalue').putCursorAtEnd() }, 50);
    saveOldItem = function() {
        if (checkModalFields($(".modalvalue").val(), $(".modaltitle").val())) { // If inputs are proper filled, save changes
            whichColumn[itemId].title = $(".modaltitle").val();
            whichColumn[itemId].amount = $(".modalvalue").autoNumeric("get");
            whichColumn[itemId].icon = $(".initicons").data("iconselected");
            whichColumn[itemId].currency = $(".modalcurrency").val();
            whichColumn[itemId].period = $(".modalperiod").val();
            editOldDiv(incomesExpenses, itemId, $(".modaltitle").val(), $(".initicons").data("iconselected"), $(".modalcurrency").val(), $(".modalperiod").val(), $(".modalvalue").autoNumeric("get"));
            turnOffModal();
        }
        checkSlidersLength();
        itemsPosition("expense");
        itemsPosition("income");
        setTimeout(function() { runConvert() }, 230);
    };
    deleteItem = function() {
        turnOffModal();
        delete whichColumn[itemId];
        itemDiv.css({"height": "0px", "background-color": "#ffe3e3"});
        setTimeout(function() { $("#" + incomesExpenses + "-" + itemId).remove(); checkSlidersLength() }, 300);

        // Way to proper move items list
        var slider = $("#" + incomesExpenses + "slider");
        if (slider.position().top > -71 && slider.children().length > 4) {
            setTimeout(function() { Up(incomesExpenses) }, 300);
        }
        itemsPosition("expense");
        itemsPosition("income");
        setTimeout(function() { runConvert() }, 230);

        // Save changes on server
        jsonDataSave();
    }
}
function editOldDiv(whichColumn, itemId, itemTitle, itemIcon, itemCurrency, itemPeriod, itemValue) {
    var value = itemValue.replace(/(\d)(?=(\d\d\d)+([^\d]|$))/g, '$1 </span><span class="lightdigit20">');
    $("#" + whichColumn + "-" + itemId).replaceWith('<div onclick="itemClick(this)" class="' + whichColumn + 'item" id="' + whichColumn +'-' + itemId + '"><span class="title11museo300">' + itemTitle + '</span><p class="title9museo300"><span class="bolddigit20">' + value + ' </span>' + checkCurrency(itemCurrency) + checkPeriod(itemPeriod) + '</span></p><div class="itembackground"></div></div>');
    $("#" + whichColumn + "-" + itemId).addClass("newitemadded").data({"id": itemId, "icon": itemIcon, "amount": itemValue, "title": itemTitle, "currency": itemCurrency ,"period": itemPeriod}).children("div").addClass(itemIcon);
    setTimeout(function() { $("#" + whichColumn + "-" + itemId).removeClass("newitemadded") }, 4100);
}
// prepare calculations for 4 page
function runConvert() {
    expensesSumMonth = 0; incomesSumMonth = 0;
    $("#circle-select-1, #circle-select-2, #circle-select-3").empty();
    getConverted(incomes);
    getConverted(expenses);
}



// EVENT HANDLERS

// MODAL: Call add items modal window
$("#noincomes, #noexpenses, .zoomplus, .plusitemborder").click(function() {
    $(".mainmodaltitle").empty();
    if ($(this).hasClass("incomebutton")) {
        $(".initicons").data({"iconselected": "wallet", "add-edit": "add", "incomes-expenses": "incomes"});
        $("#chooseicon").removeClass().addClass("wallet");
        $(".mainmodaltitle").append("Add income");
    }
    else {
        $(".initicons").data({"iconselected": "cart", "add-edit": "add", "incomes-expenses": "expenses"});
        $("#chooseicon").removeClass().addClass("cart");
        $(".mainmodaltitle").append("Add expense");
    }
    $(".modalvalue, .modaltitle").show();
    $("#overlay, #add-modal").addClass("modal-show");
    setTimeout(function() { $('.modalvalue').putCursorAtEnd() }, 50);
});

// MODAL: Save button handlers
$(".modal-save").click(addOrSaveItems);
$(".modaltitle, .modalvalue").keyup(function(e) {
    if(e.which == 13) {	addOrSaveItems(); this.blur() }
});

function addOrSaveItems () {
    if($(".initicons").data("add-edit") == "add") {
        addNewItem();
    }
    else {
        saveOldItem();
    }
    jsonDataSave();
}

// MODAL: Delete button handler
$(".modal-delete").click(function() {
    deleteItem();
});

// MODAL: Set choosen icon
$(".imgbox").click(function() {
    $(".modaltable").addClass("modalreverse");
    setTimeout( function() { $(".modalincomessurface, .modalexpensessurface").fadeOut(150); } , 160);
    var icon = $(this).children("div").attr('class').split(' ')[1];
    $(".initicons").data("iconselected", icon);
    $("#chooseicon").removeClass().addClass(icon);
});

// MODAL: Check that fields are not empty
function checkModalFields (itemValue, itemTitle) {
    if (itemValue == 0) { // Check value input
        $(".modalvalue").addClass("modalvalueerror");
        setTimeout(function() { $(".modalvalue").removeClass("modalvalueerror") }, 500);
        return false;
    }
    else if (itemTitle.length == 0) { // Check title input
        $(".modaltitle").addClass("modaltitleerror");
        setTimeout(function() { $(".modaltitle").removeClass("modaltitleerror") }, 500);
        return false;
    }
    else {
        return true;
    }
}

// MODAL: Start icons table 
$(".initicons").click(function() {
    $(".modaltable").removeClass("modalreverse");
    if ($(".initicons").data("incomes-expenses") == "incomes") {
        $(".modalincomessurface").fadeIn(100);
    }
    else {
        $(".modalexpensessurface").fadeIn(100);
    }
});

// MODAL: Turn off modal/notes window and reset all forms
$("#overlay, .modal-close").click(turnOffModal);
function turnOffModal() {
    $("#overlay, #add-modal, #add-notes").removeClass("modal-show");
    $(".modalincomessurface, .modalexpensessurface").fadeOut(150);
    setTimeout(function() { $(".modalvalue").val('0').hide(); $(".modaltitle").val('').hide(); $(".modal-delete").hide(); $("textarea#notes").val(user.notes).hide(); }, 200);
}

// MODAL: Change font size according to input value length
$(".modalvalue").bind("keyup keydown keypress select focus click", modalFontSize);
function modalFontSize() {
    var length=$(".modalvalue").val().length;
    if (length > 10) {
        $(".modalvalue").css({"font-size": "60px"});
    }
    else if (length > 9) {
        $(".modalvalue").css({"font-size": "66px"});
    }
    else if (length > 8 ) {
        $(".modalvalue").css({"font-size": "76px"});
    }
    else {
        $(".modalvalue").css({"font-size": "86px"});
    }
}

// MODAL: Set zero when input is empty
$(".modalvalue").bind("blur", function() {
    if (this.value.length == 0) {
        $(".modalvalue").val("0");
    }
});

// COLUMNS: UP and DOWN buttons
$("#expenseup").click(function() {
    Up("expense");
});
$("#expensedown").click(function() {
    Down("expense");
});
$("#incomeup").click(function() {
    Up("income");
});
$("#incomedown").click(function() {
    Down("income");
});

// COLUMNS: Touch screen swipe
$("#expensewrapper").swipe( {
    swipe:function(event, direction, distance, duration, fingerCount) {
        if (direction == "up") {
            Up("expense");
        }
        if (direction == "down") {
            Down("expense");
        }
    },
    threshold:0
});
$("#incomewrapper").swipe( {
    swipe:function(event, direction, distance, duration, fingerCount) {
        if (direction == "up") {
            Up("income");
        }
        if (direction == "down") {
            Down("income");
        }
    },
    threshold:0
});
// COLUMNS: Mouse wheel
$('#expensewrapper').bind('DOMMouseScroll mousewheel', function (e){
    if(e.originalEvent.wheelDelta > 70) {
        Down("expense");
    }
    if(e.originalEvent.wheelDelta < -70) {
        Up("expense");
    }
    if(e.originalEvent.detail < 0) {
        Down("expense");
    }
    if(e.originalEvent.detail > 0) {
        Up("expense");
    }
    return false;
});

$('#incomewrapper').bind('DOMMouseScroll mousewheel', function (e){
    if(e.originalEvent.wheelDelta > 70) {
        Down("income");
    }
    if(e.originalEvent.wheelDelta < -70) {
        Up("income");
    }
    if(e.originalEvent.detail < 0) {
        Down("income");
    }
    if(e.originalEvent.detail > 0) {
        Up("income");
    }
    return false;
});

// SAVINGS: Click on toggles
$("#deposit").click(function() {
    if ($("#deposit").prop('checked') == false) {
        $("#capitalization").prop('disabled', true);
        $("#percentvalue").prop('disabled', true);
        $("#capitalization").prop('checked', false);
    }
    else {
        $("#capitalization").prop('disabled', false);
        $("#percentvalue").prop('disabled', false);
    }
});
// SAVINGS: Moving ruble sign according to input value length
$("#savingsvalue").bind("keyup keydown keypress select click", function() {savings.freeMoney = $("#savingsvalue").autoNumeric("get"); moveRuble();}).keyup(function(e) { if (e.which == 13) {	this.blur() } });
function moveRuble() {
    var length=$("#savingsvalue").val().length;
    length = (length<2)? 1: length;
    $("#savingsvalue").attr('size', length);
    if (length > 9) {
        $("#savingsvalue").css({"font-size": "31px"});
        $("#rublesign").css({"left": (length*15 + 17) + "px", "top": "115px"});
    }
    else if (length > 7 ) {
        $("#savingsvalue").css({"font-size": "38px"});
        $("#rublesign").css({"left": (length*18 + 18) + "px", "top": "120px"});
    }
    else {
        $("#savingsvalue").css({"font-size": "44px"});
        $("#rublesign").css({"left": (length*22 + 20) + "px", "top": "125px"});
    }
}

//SAVINGS: change currency on sign click
$("#rublesign").on("click", function() {
    switch (user.checkedCurr) {
        case "RUB": user.checkedCurr = "EUR"; $("#rublesign").css({"background-position": "-386px 0"});
            break;
        case "EUR": user.checkedCurr = "USD"; $("#rublesign").css({"background-position": "-354px 0"});
            break;
        case "USD": user.checkedCurr = "RUB"; $("#rublesign").css({"background-position": "-150px 0"});
            break;
    }
    changeCurrency();
    $("#savingsvalue").autoNumeric('set', Math.round (savings.freeMoney) );
    moveRuble();
    // Update savings slider
    $('#savings-slider').noUiSlider({
        start: (incomesSumMonth-expensesSumMonth) * $("#savings-slider").data("checkedPercent"),
        step: (incomesSumMonth-expensesSumMonth) / 20,
        range: {
            'min': [ 0 ],
            'max': [ incomesSumMonth-expensesSumMonth ]
        }
    }, true);
    runConvert();
});

// SAVINGS: Set zero when input is empty
$("#savingsvalue").bind("blur", function() {
    if (this.value.length == 0) {
        $("#savingsvalue").val("0");
    }
});
$("#percentvalue").keyup(function(e) { if(e.which == 13) {	this.blur() } }).bind("blur", function() {
    if (this.value == " %") {
        $("#percentvalue").val("0 %");
    }
});

// NOTES: call window
$("#bubble").click(function() {
    $("#overlay, #add-notes").addClass("modal-show");
    $(".notes-input").show();
});

// NOTES: save button handler
$(".notes-save").click(function() {
    user.notes = $("textarea#notes").val();
    turnOffModal();
    jsonDataSave();
});

// Bubble animation
$("#bubble").bind("hover",  function() {
    $("#indicator").toggleClass("bubble-animation");
});

// ScrollTop
$("input, textarea").bind("blur", function() {
    $('body').animate({ scrollTop: '0' }, 50)
});

// PAGE CHANGING

// Cancel swipe on other fields
$("body").swipe( {
    swipe:function(event, direction, distance, duration, fingerCount) {
        $('body').animate({ scrollTop: '0' }, 50)
    },
    threshold:0
});

// Go back to Greeting Page
$("#logoclickplace").click(function() {
    $("#logo_settings, #overlay, .modal-content, #settingspage, #expenseslider, #bubble").fadeOut(300);
    setTimeout(greetingPageAgain, 250);
});

// All-PAGE SWIPE BETWEEN 3-4 PAGES
$("#swipefield, #savings, #savebutton, #settings_hat").bind('DOMMouseScroll mousewheel', function (e){
    if(e.originalEvent.wheelDelta < -50) {
        launchStatistic();
    }
    return false;
});
$(".bottompage").bind('DOMMouseScroll mousewheel', function (e){
    if(e.originalEvent.wheelDelta > 100) {
        fadeStatistic();
    }
    return false;
});
$("#swipefield, #savings, #savebutton, #settings_hat").swipe( {
    swipe:function(event, direction, distance, duration, fingerCount) {
        if (direction == "up" || direction == "left") {
            if (global.mobileClient) launchStatistic();
        }
    },
    threshold:0
});
$(".bottompage").swipe( {
    swipe:function(event, direction, distance, duration, fingerCount) {
        if (direction == "down" || direction == "right") {
            if (global.mobileClient) fadeStatistic();
        }
    },
    threshold:0
});
$("#savebutton").on("click", function() {
    launchStatistic();
});
$("#logo_statistic").on("click", function() {
    fadeStatistic();
});

// Launch 4 page
function launchStatistic() {
    if ($("#incomeslider").children().length > 0 && $("#expenseslider").children().length > 0) {

        $(".bottompage").css({"display": "block"});

        // Fill objects form Savings fields
        savings.percent = $("#percentvalue").autoNumeric("get");
        savings.deposit = $("#deposit").prop("checked");
        savings.capitalization = $("#capitalization").prop("checked");

        // Launch 4 page
        $("#lastlogoflipper").addClass("flippedcard");
        initStatisticPage();
        setTimeout(function() { initSavingsCircles(user.checkedPercent, 0.2, savings.freeMoney, savings.freeMoney, 700) }, 1600);
        setTimeout(function() { $(".toppage, .bottompage").addClass("sectionDown"); }, 400);

        // Update savings slider
        $('#savings-slider').noUiSlider({
            start: (incomesSumMonth-expensesSumMonth) * $("#savings-slider").data("checkedPercent"),
            step: (incomesSumMonth-expensesSumMonth) / 20,
            range: {
                'min': [ 0 ],
                'max': [Math.abs(incomesSumMonth-expensesSumMonth)]
            }
        }, true);

    }
    else {
        alert("Please, add at least one item for each column")
    }

    jsonDataSave();

}

function jsonDataSave() {
    if (global.savePermit) {
        $.ajax({
            url: 'accounts/current',
            datatype: 'json',
            type: "put",
            contentType: "application/json",
            headers: {'Authorization': 'Bearer ' + getOauthTokenFromStorage()},
            data: JSON.stringify({
                note: user.notes,
                incomes: $.map(incomes, function(value) {return [value]}),
                expenses: $.map(expenses, function(value) {return [value]}),
                saving: {
                    amount: Math.ceil(savings.freeMoney),
                    capitalization: savings.capitalization,
                    deposit: savings.deposit,
                    currency: user.checkedCurr,
                    interest: savings.percent
                }
            }),
            success: function () {
                $("#leftborder, #rightborder, #centerborder").addClass("saveaction");
                setTimeout(function() {
                    $("#leftborder, #rightborder, #centerborder").removeClass("saveaction");
                }, 400);
            },
            error: function () {
                alert("An error during data saving. Please, try again later");
            }
        });
    }
}

function fadeStatistic() {

    switch (user.checkedCurr) {
        case "RUB": $("#rublesign").css({"background-position": "-150px 0"});
            break;
        case "EUR": $("#rublesign").css({"background-position": "-386px 0"});
            break;
        case "USD": $("#rublesign").css({"background-position": "-354px 0"});
            break;
    }
    $("#savingsvalue").autoNumeric('set', savings.freeMoney);
    moveRuble();

    $(".toppage, .bottompage").removeClass("sectionDown");
    setTimeout(function() { $("#lastlogoflipper").removeClass("flippedcard"); }, 220);

    setTimeout(function() { drawChartLine(91); $(".bottompage").css({"display": "none"}); }, 500);
}