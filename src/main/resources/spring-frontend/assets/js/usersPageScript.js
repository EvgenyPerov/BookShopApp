function functionClearTableAndInputBook(){
    document.getElementById("idBook").value = '';
    document.getElementById("slug").value = '';
    document.getElementById("tableIdBook").innerText = '';
    document.getElementById("tableImageBook").src = '';
    document.getElementById("tableTitleBook").innerText = '';
    document.getElementById("tableSlugBook").innerText = '';
    document.getElementById("tablePriceBook").innerText = '';
    document.getElementById("tableGiftButton").style.display = 'none';
    document.getElementById("tableCancelButton").style.display = 'none';
}

document.getElementById('save').onclick = function() {
    let id = document.getElementById("idUser").innerText;
    let pass = document.getElementById("password").value;
    let pass2 = document.getElementById("passwordReply").value;

    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/admin/users/password', true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({id, pass, pass2}));

    xhr.onload = function () {
        if (xhr.status === 200) {
            document.getElementById("password").value = '';
            document.getElementById("passwordReply").value = '';

            message('-- Пароль изменен');

            let passwordWindow = document.getElementById('changePassword');
            passwordWindow.style.display = 'none';
        }
    };

}

document.getElementById('cancel').onclick = function() {
    document.getElementById("password").value = '';
    document.getElementById("passwordReply").value = '';

    let passwordWindow = document.getElementById('changePassword');
    passwordWindow.style.display = 'none';
}

document.getElementById('cancelSelectBook').onclick = function() {
    functionClearTableAndInputBook();

    let book = document.getElementById('selectBook');
    book.style.display = 'none';
}

function message(text) {
    let message = document.getElementById('message');
    message.innerText = text;
    setTimeout(function () {
        message.innerText = '';
    }, 3000);
}

function execute(element, name, action) {
    if (action == 'password') {
        functionClearTableAndInputBook();

        let book = document.getElementById('selectBook');
        book.style.display = 'none';

        let pass = document.getElementById('changePassword');
        pass.style.display = 'block';
    }
    if (action == 'gift') {
        let pass = document.getElementById('changePassword');
        pass.style.display = 'none';

        let book = document.getElementById('selectBook');
        book.style.display = 'block';
    }
    if (action == 'block') {
        functionClearTableAndInputBook();

        let pass = document.getElementById('changePassword');
        pass.style.display = 'none';

        let book = document.getElementById('selectBook');
        book.style.display = 'none';

        changeStatusUserFunction(name, action);
    }
    if (action == 'active') {
        functionClearTableAndInputBook();

        let pass = document.getElementById('changePassword');
        pass.style.display = 'none';

        let book = document.getElementById('selectBook');
        book.style.display = 'none';

        changeStatusUserFunction(name, action);
    }
}

function sendGiftFunction() {
    let idBook = document.getElementById("tableIdBook").innerText;
    let idUser = document.getElementById("idUser").innerText;

    functionClearTableAndInputBook();

    let book = document.getElementById('selectBook');
    book.style.display = 'none';

    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/admin/users/gift/send', true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({idUser, idBook}));

    xhr.onload = function () {
        if (xhr.status === 200) {
            message('-- Подарок добавлен пользователю');
        }
        if (xhr.status === 204) {
            message('-- Выбранная книга уже куплена пользователем');
        }
    };
}

function cancelGiftFunction() {
    functionClearTableAndInputBook();

    let book = document.getElementById('selectBook');
    book.style.display = 'none';
}


function changeStatusUserFunction(id, status) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", '/admin/users/execute', true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({id, status}));

    xhr.onload = function () {
        if (xhr.status === 200) {
            if (status == 'block') {
                message('-- Пользователь заблокирован');
                document.getElementById("idUser").style.background = 'red';
            }
            if (status == 'active') {
                message('-- Пользователь активирован');
                document.getElementById("idUser").style.background = '#87d256';
            }
        }
    };
}

function functionSelectBook() {
    let id = document.getElementById("idBook").value;
    let slug = document.getElementById("slug").value;
    var bookTable = document.getElementById('bookTable');

    functionClearTableAndInputBook();

    fetch("/admin/users/gift", {
        method: "POST",
        body: JSON.stringify({id, slug}),
        headers: {
            "Content-type": "application/json; charset=UTF-8"
        }
    })
        .then((response) => {
                if (!response.ok || response.status !== 200 || !response.body) {
                    throw new Error('Server response is empty');
                }
                return response.json();
            }
        )
        .then(book => {
            if (!book) {
                console.log("Book is null or undefined");
                return;
            }
            document.getElementById("tableIdBook").innerText = book.id;
            document.getElementById("tableImageBook").src = book.image;
            document.getElementById("tableImageBook").style.width = '100';
            document.getElementById("tableImageBook").style.height = '100';
            document.getElementById("tableTitleBook").innerText = book.title;
            document.getElementById("tableSlugBook").innerText = book.slug;
            document.getElementById("tablePriceBook").innerText = book.price;

            if (bookTable.rows.length > 1) {
                document.getElementById("tableGiftButton").style.display = 'block';
                document.getElementById("tableCancelButton").style.display = 'block';
            }
        })
        .catch((error) => {
            console.error('Error Null element:', error);
        });
}