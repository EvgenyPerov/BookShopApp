$('#uploadButton').on('click', function () {
    $('#dialog').trigger('click');
    // alert('Yes');
});

$('#dialog').on('change', function (){
    $('#imgForm').submit();
});