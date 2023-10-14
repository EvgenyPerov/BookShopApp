window.onload = function () {
    let passwordInput = document.getElementById('password');
    let passwordReplyInput = document.getElementById('passwordReply');
    let saveButton = document.getElementById('save');
    let warning = document.createElement('span');

    warning.style.color = 'red';

    passwordReplyInput.parentNode.appendChild(warning);

    function checkInputs() {
        if (passwordInput.value !== passwordReplyInput.value) {
            warning.innerText = 'Пароли не совпадают';
            saveButton.style.display = 'none';
        } else {
            warning.innerText = '';
            saveButton.style.display = 'block';
        }
    }

    passwordInput.addEventListener('keyup', checkInputs);
    passwordReplyInput.addEventListener('keyup', checkInputs);

    let message = document.getElementById('message').innerText;
    if (message) {
        setTimeout(function () {
            document.getElementById('message').style.display = 'none';
        }, 2000);
    }

};