var ResultsNotices = function () {
    var bloquearUI = function (mensaje) {
        var loc = window.location;
        var pathName = loc.pathname.substring(0, loc.pathname.indexOf('/', 1) + 1);
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
            }
        });
    };

    var desbloquearUI = function () {
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


            /*********************************
             RESULTADOS NOTIFICACIONES EXISTENTES
             */
            var table2 = $('#fichas_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#fichas_result'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            $('#btnAddNotification').click(function () {
                addNotification();
            });

            function addNotification() {
                var anulacionObj = {};
                var respuestaObj = {};
                respuestaObj['idNotificacion'] = '';
                anulacionObj['mensaje'] = '';
                anulacionObj['idPersona'] = $("#idPersona").val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.addNotificationUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(anulacionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                console.log(parametros.createUrl + data.idNotificacion);
                                window.location.href = parametros.createUrl + data.idNotificacion;
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            /*******************************************************************************/
            /***************************** OTRAS MUESTRAS **********************************/
            /*******************************************************************************/

            $('#btnAddNotiOtherSamples').click(function () {
                addNotiOtherSamples();
            });

            function addNotiOtherSamples() {
                var anulacionObj = {};
                var respuestaObj = {};
                respuestaObj['idNotificacion'] = '';
                anulacionObj['mensaje'] = '';
                anulacionObj['idSolicitante'] = $("#idSolicitante").val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.addNotificationUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(anulacionObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                window.location.href = parametros.createUrl + data.idNotificacion;
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }
        }

    }
}();
