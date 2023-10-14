// В моем spring-boot-starter приложении на HTML странице происходит выбор файла из папки. Используется JavaScript  функция openFileOption().
//     После выбора файла имя файла сохраняется в массиве "resultListFiles" и выводится в поле 'file'.
// Как измениит код скрипта, чтобы в поле 'file' получать не только имя файла, но и его размер в килобайтах в формате "имя файла;размер".
// Мой код:
var resultListFiles = [];
function openFileOption() {
    document.getElementById('fileInput').click();
}

document.getElementById('fileInput').addEventListener('change', function() {
    let file = this.files[0];
    let selectedValue = `${file.name}:${Math.round(file.size / 1024)}`;

    if (selectedValue) {
        resultListFiles.push(selectedValue);
    }

    document.getElementById('file').value = resultListFiles.join(", ");
});