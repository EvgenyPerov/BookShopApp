window.onload = function() {
    let table = document.getElementById('reviewsTable');
    if (table.rows.length <= 1){
        hideButtons();
    } else {
        printCountReviews(table.rows.length - 1);
    }

    var tempStartDatetime = localStorage.getItem("tempStartDatetime");
    var tempEndDatetime = localStorage.getItem("tempEndDatetime");

    if (tempStartDatetime !== null) {
        document.getElementById("startDatetime").value = tempStartDatetime;
        localStorage.removeItem("tempStartDatetime");
    }
    if (tempEndDatetime !== null) {
        document.getElementById("endDatetime").value = tempEndDatetime;
        localStorage.removeItem("tempEndDatetime");
    }
}

function execute(currentRow, id, status) {
    $(currentRow).closest('tr').remove();

    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/admin/review/execute', true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({status, id}));

    let table = document.getElementById('reviewsTable');
    if (table.rows.length <= 1){
        hideButtons();
    }else {
        printCountReviews(table.rows.length - 1);
    }
};

function executeAll(status) {
    let table = document.getElementById('reviewsTable');
    let ids = Array.from(table.getElementsByTagName('tr'))
        .slice(1) // исключаем заголовки
        .map(tr => tr.getAttribute('id'));

    try {
        var rowCount = table.rows.length;
        for (var i = 1; i < rowCount; i++) {
            table.deleteRow(i);
            rowCount--;
            i--;
        }
    } catch (e) {
       console.log(e);
    }
    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/admin/review/executeAll', true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({ids, status}));

    hideButtons();
};

function dateReviews() {
    let startDatetime = document.getElementById('startDatetime').value;
    let endDatetime = document.getElementById('endDatetime').value;
    let status = document.querySelector('input[name="status"]:checked').id;

    localStorage.setItem("tempStartDatetime", startDatetime);
    localStorage.setItem("tempEndDatetime", endDatetime);

    location.href='/admin/review?startDatetime=' + startDatetime +'&endDatetime='+endDatetime+ '&status=' + status;
}

function hideButtons() {
    let btnApprove = document.getElementById('btnApprove');
    let btnReject = document.getElementById('btnReject');
    btnApprove.style.display='none';
    btnReject.style.display='none';
    let message = document.getElementById('message');
    message.style.background="limegreen";
    message.style.color="white";
    message.innerText = '-- Список пуст';
}

function printCountReviews(count) {
    let message = document.getElementById('message');
    message.style.background="none";
    message.style.color="black";
    message.innerText = '-- Найдено записей - ' + count;
}