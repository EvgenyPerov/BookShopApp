
$('#uploadButton').on('click', function () {
    localStorage.setItem("tempId", document.getElementById("id").value);
    localStorage.setItem("tempSlug", document.getElementById("slug").value);
    localStorage.setItem("tempTitle", document.getElementById("title").value);
    localStorage.setItem("tempPubDate", document.getElementById("pubDate").value);
    localStorage.setItem("tempPrice", document.getElementById("price").value);
    localStorage.setItem("tempDiscount", document.getElementById("discount").value);
    localStorage.setItem("checked", document.getElementById("isBestseller").value);
    localStorage.setItem("tempDescription", document.getElementById("description").value);
    localStorage.setItem("tempAuthor", document.getElementById("author").value);
    localStorage.setItem("tempGenre", document.getElementById("genre").value);
    localStorage.setItem("tempTag", document.getElementById("tag").value);
    localStorage.setItem("tempFile", document.getElementById("file").value);
    $('#dialog').trigger('click');
    // alert('Yes');

});

$('#dialog').on('change', function (){
    $('#imgForm').submit();
});

// При загрузке страницы проверяем, есть ли что-то в localStorage
window.onload = function() {
    var tempId = localStorage.getItem("tempId");
    var tempSlug = localStorage.getItem("tempSlug");
    var tempTitle = localStorage.getItem("tempTitle");
    var tempPubDate = localStorage.getItem("tempPubDate");
    var tempPrice = localStorage.getItem("tempPrice");
    var tempDiscount = localStorage.getItem("tempDiscount");
    var checked = JSON.parse(localStorage.getItem('checked'));
    var tempDescription = localStorage.getItem("tempDescription");
    var tempAuthor = localStorage.getItem("tempAuthor");
    var tempGenre = localStorage.getItem("tempGenre");
    var tempTag = localStorage.getItem("tempTag");
    var tempFile = localStorage.getItem("tempFile");

    // Если есть сохраненное значение, заполняем поля из памяти
    if (tempId !== null) {
        document.getElementById("id").value = tempId;
        localStorage.removeItem("tempId");
    }
    if (tempSlug !== null) {
        document.getElementById("slug").value = tempSlug;
        localStorage.removeItem("tempSlug");
    }
    if (tempTitle !== null) {
        document.getElementById("title").value = tempTitle;
        localStorage.removeItem("tempTitle");
    }
    if (tempPubDate !== null) {
        document.getElementById("pubDate").value = tempPubDate;
        localStorage.removeItem("tempPubDate");
    }
    if (tempPrice !== null) {
        document.getElementById("price").value = tempPrice;
        localStorage.removeItem("tempPrice");
    }
    if (tempDiscount !== null) {
        document.getElementById("discount").value = tempDiscount;
        localStorage.removeItem("tempDiscount");
    }
    if (checked == true){
        document.getElementById("isBestseller").checked = true;
        localStorage.removeItem("checked");
    }
    if (tempDescription !== null) {
        document.getElementById("description").value = tempDescription;
        localStorage.removeItem("tempDescription");
    }
    if (tempAuthor !== null) {
        document.getElementById("author").value = tempAuthor;
        localStorage.removeItem("tempAuthor");
    }
    if (tempGenre !== null) {
        document.getElementById("genre").value = tempGenre;
        localStorage.removeItem("tempGenre");
    }
    if (tempTag !== null) {
        document.getElementById("tag").value = tempTag;
        localStorage.removeItem("tempTag");
    }
    if (tempFile !== null) {
        document.getElementById("file").value = tempFile;
        localStorage.removeItem("tempFile");
    }

    let createOkElement = document.getElementById("createOk");

    if (createOkElement !== null && createOkElement.textContent.trim()==="сохранение выполнено!") {
        location.href = '/admin/book';
    }

    let changeOkElement = document.getElementById("updateOk");

    if (changeOkElement !== null && changeOkElement.textContent.trim()==="изменение выполнено!") {
        location.href = '/admin/editBook';
    }

    setInitialBook();
}