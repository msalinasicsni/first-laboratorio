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
            },
            baseZ: 1051 // para que se muestre bien en los modales
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

            //Mostrar mensaje de confirmación para anular notificación
            $(".overridenoti").click(function(){
                var id = $(this).data('id');
                if (id != null) {
                    $('#idOverride').val(id);
                    $('#d_confirmacion').modal('show');
                }
            });

            $('#btnOverride').click(function () {
                window.location.href = parametros.overrideUrl + $('#idOverride').val();
            });

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

            //VALIDAR SI YA SE TOMO MUESTRA A LA NOTIFICACION SELECCIONADA
            $(".tomarmx").click(function(){
                getTomaMx($(this).data('id'));
            });

            function getTomaMx (idNotificacion){
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.tomaMxUrl, {
                    idNotificacion: idNotificacion,
                    ajax: 'true'
                }, function (data) {
                    var actionUrl = parametros.addMxUrl + idNotificacion;
                    desbloquearUI();
                    if(data.length > 0){
                        var opcSi = $("#confirm_msg_opc_yes").val();
                        var opcNo = $("#confirm_msg_opc_no").val();
                        $.SmartMessageBox({
                            title: $('#msg_confirm_title').val(),
                            content: $('#msg_confirm_content').val(),
                            buttons: '['+opcSi+']['+opcNo+']'
                        }, function (ButtonPressed) {
                            if (ButtonPressed === opcSi) {
                                window.location.href = actionUrl;   //misma pestaña
                                //link.attr("href",actionUrl);
                                //window.open(actionUrl,'_blank');  //nueva pestaña
                            }
                            if (ButtonPressed === opcNo) {
                                $.smallBox({
                                    title: $('#titleCancel').val(),
                                    content: "<i class='fa fa-clock-o'></i> <i>"+$("#smallBox_content").val()+"</i>",
                                    color: "#C46A69",
                                    iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                    timeout: 4000
                                });
                            }

                        });
                    }else{
                        window.location.href =  actionUrl;
                        //link.attr("href",actionUrl);
                        //window.open(actionUrl,'_blank');
                    }

                });
            }
        }

    }
}();
