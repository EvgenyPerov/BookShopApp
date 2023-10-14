//-------------------------------------------------------------- авторы
function filterFunctionAuthor(id) {
    let input, filter, ul, li, a, i;
    input = document.getElementById(id);
    filter = input.value.toUpperCase();
    let div = document.getElementById("itemDropdownAuthor");
    a = div.getElementsByTagName("option");
    let txtValue;
    for (i = 0; i < a.length; i++) {
        txtValue = a[i].textContent || a[i].innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            a[i].style.display = "";
        } else {
            a[i].style.display = "none";
        }
    }
    document.getElementById('hiddenInputAuthor').value = input.value;
}

function updateSelectedValueAuthor(value) {
    document.getElementById('selectedValueAuthor').value = value;
}

var resultListAuthors = [];

function displayResultAuthor(name) {
    let selectedValueAuthor = document.getElementById('selectedValueAuthor').value;

    if (selectedValueAuthor) {
        resultListAuthors.push(selectedValueAuthor);
    }

    document.getElementById(name).value = resultListAuthors.join(", ");
    // document.getElementById('selectedValueAuthor').value = '';
    // document.getElementById('hiddenInputAuthor').value = '';
}
//-------------------------------------------------------------- жанры

function filterFunctionGenre(id) {
    let input, filter, ul, li, a, i;
    input = document.getElementById(id);
    filter = input.value.toUpperCase();
    let div = document.getElementById("itemDropdownGenre");
    a = div.getElementsByTagName("option");
    let txtValue;
    for (i = 0; i < a.length; i++) {
        txtValue = a[i].textContent || a[i].innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            a[i].style.display = "";
        } else {
            a[i].style.display = "none";
        }
    }
    document.getElementById('hiddenInputGenre').value = input.value;
}

function updateSelectedValueGenre(value) {
    document.getElementById('selectedValueGenre').value = value;
}

var resultListGenres = [];

function displayResultGenre(name) {
    let selectedValueGenre = document.getElementById('selectedValueGenre').value;

    if (selectedValueGenre) {
        resultListGenres.push(selectedValueGenre);
    }

    document.getElementById(name).value = resultListGenres.join(", ");
}
//-------------------------------------------------------------- тэги

function filterFunctionTag(id) {
    let input, filter, a, i;
    input = document.getElementById(id);
    filter = input.value.toUpperCase();
    let div = document.getElementById("itemDropdownTag");
    a = div.getElementsByTagName("option");
    let txtValue;
    for (i = 0; i < a.length; i++) {
        txtValue = a[i].textContent || a[i].innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            a[i].style.display = "";
        } else {
            a[i].style.display = "none";
        }
    }
    document.getElementById('hiddenInputTag').value = input.value;
}

function updateSelectedValueTag(value) {
    document.getElementById('selectedValueTag').value = value;
}

var resultListTags = [];

function displayResultTag(name) {
    let selectedValueTag = document.getElementById('selectedValueTag').value;

    if (selectedValueTag) {
        resultListTags.push(selectedValueTag);
    }
    document.getElementById(name).value = resultListTags.join(", ");
}
//-------------------------------------------------------------- книги

function filterFunctionBook(id) {
    let input, filter, a, i;
    input = document.getElementById(id);
    filter = input.value.toUpperCase();
    let div = document.getElementById("itemDropdownBook");
    a = div.getElementsByTagName("option");
    let txtValue;
    for (i = 0; i < a.length; i++) {
        txtValue = a[i].textContent || a[i].innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            a[i].style.display = "";
        } else {
            a[i].style.display = "none";
        }
    }
    document.getElementById('hiddenInputBook').value = input.value;
}

function updateSelectedValueBook(value) {
    document.getElementById('selectedValueBook').value = value;
}

function displayResultBookEdit() {
    let bookString = document.getElementById('selectedValueBook').value;

        // Use regular expressions to extract the properties
        let id = bookString.match(/id=(\d+)/)[1];
        let slug = bookString.match(/slug='(.*?)'/)[1];
        let title = bookString.match(/title='(.*?)'/)[1];
        let price = bookString.match(/price=(\d+)/)[1];
        let discount = bookString.match(/discount=(\d+)/)[1];
        let isBestseller = bookString.match(/isBestseller=(\d+)/)[1];
        let image = bookString.match(/image='(.*?)'/)[1];

        document.getElementById('id').value = id;
        document.getElementById('slug').value = slug;
        document.getElementById('title').value = title;
        document.getElementById('pubDate').value = bookString.substring(bookString.indexOf("pubDate=")+8, bookString.indexOf("pubDate=")+18);
        document.getElementById('price').value = price;
        document.getElementById('discount').value = discount;
        document.getElementById('isBestseller').checked = (isBestseller == 1)? true : false;
        document.getElementById('description').value = parseBooksDescription(bookString);
        document.getElementById('file').value = parseBooksFiles(bookString);
        document.getElementById('tag').value = parseBooksTag(bookString);
        document.getElementById('genre').value = parseBooksGenre(bookString);
        document.getElementById('author').value = parseBooksAuthor(bookString);
        document.getElementById('photoImage').src = (image.includes("/book-covers/"))? image : '/assets/img/content/main/defaultImageBook.png';
        document.getElementById('image').value = image;
}

function displayResultAuthorEdit() {
    let authorString = document.getElementById('selectedValueAuthor').value;
    // Use regular expressions to extract the properties
    let id = authorString.match(/id=(\d+)/)[1];
    let photo = authorString.match(/photo='(.*?)'/)[1];
    let slug = authorString.match(/slug='(.*?)'/)[1];
    let name = authorString.match(/nameAuthor='(.*?)'/)[1];
    let description = authorString.substring(authorString.indexOf("description='")+13, authorString.lastIndexOf("'}"));

    document.getElementById('id').value = id;
    document.getElementById('photoImage').src = (photo.includes("/author-photo/"))? photo : '/assets/img/content/main/defaultImageBook.png';
    document.getElementById('photo').value = photo;
    document.getElementById('slug').value = slug;
    document.getElementById('name').value = name;
    document.getElementById('description').value = description;
}
//----------------------------------------------- инициализация экрана

function setInitialAuthor() {
    var selectBox = document.getElementById('itemDropdownAuthor');
    if (selectBox != null) {
        var firstOption = selectBox.options[0].value;
        document.getElementById('selectedValueAuthor').value = firstOption;
    }
}

function setInitialBook() {
    var selectBox = document.getElementById('itemDropdownBook');
    if (selectBox != null) {
        var firstOption = selectBox.options[0].value;
        document.getElementById('selectedValueBook').value = firstOption;
    }
}
//----------------------------------------------- парсим строковые объекты чтобы получить нужные значения

function parseBooksDescription(list) {
    let result = list.split('description=\'').map(item => {
        let elem;
        if (item.includes('price=')) {
            elem = item.substring(item.indexOf("description='"), item.indexOf("',"));
        }
        return elem;
    });
    return result[1];
}

function parseBooksFiles(list) {
    let result = list.split('BookFileEntity{').map(item => {
        let file;
        let type;
        let fullPath;
        if (item.includes('BookFileTypeEntity{')) {
            let element = item.split(',');
            for (var i = 0; i < element.length; i++) {
                if (element[i].includes('path=\'')) {
                    file = element[i].substring(element[i].indexOf("'")+1, element[i].lastIndexOf("'"));
                }
                if (element[i].includes('nameTypeFile=\'')) {
                    type = element[i].substring(element[i].indexOf("'")+1, element[i].lastIndexOf("'"));
                }
            }
            fullPath = file + '.' + type;
        }
        return fullPath;
    }).join(", ");

    return result.substring(2);
}

function parseBooksGenre(list) {
    let result = list.split('Book2GenreEntity{').map(item => {
        let elem;
        if (item.includes('GenreEntity{')) {
            let element = item.split(',');
            for (var i = 0; i < element.length; i++) {
                if (element[i].includes('nameGenre=\'')) {
                    elem = element[i].substring(element[i].indexOf("'")+1, element[i].lastIndexOf("'"));
                }
            }
        }
        return elem;
    }).join(", ");
    return result.substring(2);
}

function parseBooksTag(list) {
    let result = list.split('Tag{').map(item => {
        let elem;
        if (item.includes('nameTag=')) {
            let element = item.split(',');
            for (var i = 0; i < element.length; i++) {
                if (element[i].includes('nameTag=\'')) {
                    elem = element[i].substring(element[i].indexOf("'")+1, element[i].lastIndexOf("'"));
                }
            }
        }
        return elem;
    }).join(", ");
    return result.substring(2);
}

function parseBooksAuthor(list) {
    let result = list.split('Author{').map(item => {
        let elem;
        if (item.includes('nameAuthor=')) {
            let element = item.split(',');
            for (var i = 0; i < element.length; i++) {
                if (element[i].includes('nameAuthor=\'')) {
                    elem = element[i].substring(element[i].indexOf("'")+1, element[i].lastIndexOf("'"));
                }
            }
        }
        return elem;
    }).join(", ");
    return result.substring(2);
}

function deleteAuthor() {
    var id = document.getElementById("id").value;
    var name = document.getElementById("name").value;
    var confirmation = confirm('Вы действительно хотите удалить запись?: \n id=' +id + '\n name=' + name)

    if (confirmation) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", '/admin/editAuthor/delete', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(JSON.stringify({"id": id}));
    }

    xhr.onload = function () {
        if (xhr.status === 200) {
            location.reload();
        }
    };
};

function deleteBook() {
    var id = document.getElementById("id").value;
    var title = document.getElementById("title").value;
    var confirmation = confirm('Вы действительно хотите удалить запись?: \n id=' +id + '\n title=' + title)

    if (confirmation) {
        var xhr = new XMLHttpRequest();
        xhr.open("POST", '/admin/editBook/delete', true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(JSON.stringify({"id": id}));
    }

    xhr.onload = function () {
        if (xhr.status === 200) {
            location.reload();
        }
    };
};