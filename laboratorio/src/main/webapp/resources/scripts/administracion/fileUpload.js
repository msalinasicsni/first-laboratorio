/**
 * Created by FIRSTICT on 12/14/2015.
 */
var Files = function () {
    var bloquearUI = function(mensaje){
        var loc = window.location;
        var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
        var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif>' + mensaje;
        $.blockUI({ message: mess,
            css: {
                border: 'none',
                padding: '15px',
                backgroundColor: '#000',
                '-webkit-border-radius': '10px',
                '-moz-border-radius': '10px',
                opacity: .5,
                color: '#fff'
            },
            baseZ: 1051 // para que se muestre bien en los modales
        });
    };

    var desbloquearUI = function() {
        setTimeout($.unblockUI, 500);
    };

    return {
        //main function to initiate the module
        init: function (parametros) {
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var table1 = $('#uploaded-files').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#uploaded-files'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            function getFiles() {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.filesUrl, {
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    table1.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        table1.fnAddData([data[i].nombre, data[i].descripcion,"<a title='Descargar imagen' class='btn btn-primary btn-xs' href='"+parametros.getFileUrl + data[i].nombre + "'><i class='fa fa-download'></i></a>"]);
                    }
                }).fail(function (jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
                desbloquearUI();
            }

            getFiles();

            $("input[name$='rdTipoImagen']").click(function () {
                var valor = $(this).val();

                if (valor == 'header') {
                    $('#progress .bar').css(
                        'width', '0%'
                    );
                    $('#footerFU').hide();
                    $('#footerFUP').hide();
                    $('#headerFU').show();
                    $('#headerFUP').show();
                }
                else {
                    $('#progress2 .bar').css(
                        'width', '0%'
                    );
                    $('#headerFU').hide();
                    $('#headerFUP').hide();
                    $('#footerFU').show();
                    $('#footerFUP').show();
                }
            });

            $('#fileupload').fileupload({
                dataType: 'json',
                autoUpload: true,
                maxFileSize: 999000,
                // Enable image resizing, except for Android and Opera,
                // which actually support image resizing, but fail to
                // send Blob objects via XHR requests:
                disableImageResize: /Android(?!.*Chrome)|Opera/
                    .test(window.navigator.userAgent),
                previewMaxWidth: 100,
                previewMaxHeight: 100,
                previewCrop: true,
                add: function (e, data) {
                    var uploadFile = data.files[0];
                    if (!(/(\.|\/)(jpe?g|png)$/i.test(uploadFile.name))) {
                        $.smallBox({
                            title: $("#msg_wrongformat").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    } else if (uploadFile.size > 1000000) { // 1mb
                        $.smallBox({
                            title: $("#msg_wrongsize").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    } else {
                        data.submit();
                    }
                },
                done: function (e, data) {
                    $("tr:has(td)").remove();
                    getFiles();
                    /*$.each(data.result, function (index, file) {
                        $("#uploaded-files").append(
                            $('<tr/>')
                                .append($('<td/>').text(file.fileName))
                                .append($('<td/>').text(file.fileSize))
                                .append($('<td/>').text(file.fileType))
                                .append($('<td/>').html("<a href='get/" + index + "'>Click</a>"))
                        )
                    });*/
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $('#progress .bar').css(
                        'width',
                            progress + '%'
                    );
                }
            }).prop('disabled', !$.support.fileInput)
                .parent().addClass($.support.fileInput ? undefined : 'disabled');

            $('#fileupload2').fileupload({
                dataType: 'json',
                autoUpload: true,
                maxFileSize: 999000,
                // Enable image resizing, except for Android and Opera,
                // which actually support image resizing, but fail to
                // send Blob objects via XHR requests:
                disableImageResize: /Android(?!.*Chrome)|Opera/
                    .test(window.navigator.userAgent),
                previewMaxWidth: 100,
                previewMaxHeight: 100,
                previewCrop: true,
                add: function (e, data) {
                    var uploadFile = data.files[0];
                    if (!(/(\.|\/)(jpe?g|png)$/i.test(uploadFile.name))) {
                        $.smallBox({
                            title: $("#msg_wrongformat").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    } else if (uploadFile.size > 1000000) { // 1mb
                        $.smallBox({
                            title: $("#msg_wrongsize").val(),
                            content: $("#smallBox_content").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    } else {
                        data.submit();
                    }
                },
                done: function (e, data) {
                    $("tr:has(td)").remove();
                    getFiles();
                    /*$.each(data.result, function (index, file) {
                        $("#uploaded-files").append(
                            $('<tr/>')
                                .append($('<td/>').text(file.fileName))
                                .append($('<td/>').text(file.fileSize))
                                .append($('<td/>').text(file.fileType))
                                .append($('<td/>').html("<a href='get/" + index + "'>Click</a>"))
                        )
                    });*/
                },
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $('#progress2 .bar').css(
                        'width',
                            progress + '%'
                    );
                }
            }).prop('disabled', !$.support.fileInput)
                .parent().addClass($.support.fileInput ? undefined : 'disabled');
        }
    };
}();