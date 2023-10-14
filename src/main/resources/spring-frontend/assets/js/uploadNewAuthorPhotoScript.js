
$('#uploadButton2').on('click', function () {
    localStorage.setItem("tempId", document.getElementById("id").value);
    localStorage.setItem("tempSlug", document.getElementById("slug").value);
    localStorage.setItem("tempName", document.getElementById("name").value);
    localStorage.setItem("tempDescription", document.getElementById("description").value);
    $('#dialog2').trigger('click');
    // alert('id- ' +  localStorage.getItem("tempId") + ' name- ' +  localStorage.getItem("tempName"));

});

$('#dialog2').on('change', function (){
    $('#imgForm2').submit();
});

// При загрузке страницы проверяем, есть ли что-то в localStorage
window.onload = function() {
    var tempId = localStorage.getItem("tempId");
    var tempSlug = localStorage.getItem("tempSlug");
    var tempName = localStorage.getItem("tempName");
    var tempDescription = localStorage.getItem("tempDescription");

    // Если есть сохраненное значение, заполняем поля из памяти
    if (tempId !== null) {
        document.getElementById("id").value = tempId;
        localStorage.removeItem("tempId");
    }
    if (tempSlug !== null) {
        document.getElementById("slug").value = tempSlug;
        localStorage.removeItem("tempSlug");
    }
    if (tempName !== null) {
        document.getElementById("name").value = tempName;
        localStorage.removeItem("tempName");
    }
    if (tempDescription !== null) {
        document.getElementById("description").value = tempDescription;
        localStorage.removeItem("tempDescription");
    }

    let createOkElement = document.getElementById("createOk");

    if (createOkElement !== null && createOkElement.textContent.trim()==="сохранение выполнено!") {
        location.href = '/admin/author';
    }

    let changeOkElement = document.getElementById("updateOk");

    if (changeOkElement !== null && changeOkElement.textContent.trim()==="изменение выполнено!") {
        location.href = '/admin/editAuthor';
    }

    setInitialAuthor();
}