var ChangePerson = function () {

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

    return {
        //main function to initiate the module
        init: function (parametros) {
            var page=0;
            var rowsPage=50;
            $("#prev").prop('disabled',true);
            $("#next").prop('disabled',true);
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet: 1024,
                phone: 480
            };
            var tableP = $('#persons_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "columns": [
                    null, null, null, null, null, null, null, null,
                    {
                        "className": 'select-person',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#persons_result'), breakpointDefinition);
                    }
                },
                "paging": false,
                "info": false,
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback: function () {
                    $('.select-person')
                        .off("click", selectPersonHandler)
                        .on("click", selectPersonHandler);
                }
            });

            $('#search-form').validate({
                // Rules for form validation
                rules: {
                    filtro: {
                        required: true,
                        minlength: 3
                    }
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    page=0;
                    getPersons(page*rowsPage);
                }
            });

            $("#prev").on('click', function (event) {
                if (page>0) {
                    page = page - 1;
                    getPersons(page*rowsPage);
                }
            });
            $("#next").on('click', function (event) {
                page = page+1;
                getPersons(page*rowsPage);

            });

            function selectPersonHandler() {
                var id = $(this.innerHTML).data('id');
                if (id != null) {
                    confirmarCambioPersona(id);
                }
            }

            function hideModalPerson() {
                $('#modalPerson').modal('hide');
            }

            function confirmarCambioPersona(idPersona){
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                $.SmartMessageBox({
                    title: $("#msg_confirm_title").val(),
                    content: $("#msg_confirm_content").val().replace('%s',$("#lblCodigoMx").text()),
                    buttons: '[' + opcSi + '][' + opcNo + ']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {
                        changePersonNoti(idPersona);
                    }
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#msg_action_canceled").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                            color: "#3276B1",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 3000
                        });
                    }
                })
            }

            function changePersonNoti(idPersona) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idNotificacion'] = $('#idNotificacion').val();
                valueObj['idPersona'] = idPersona;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.changePersonUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: unicodeEscape(data.mensaje),
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                var msg = $("#msg_change_successful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                hideModalPerson();
                                $("#filtro").val('');
                                tableP.fnClearTable();
                                $('#orders_result').dataTable().fnClearTable();
                            }
                            setTimeout($.unblockUI, 500);
                        },
                        error: function (jqXHR) {
                            setTimeout($.unblockUI, 500);
                            validateLogin(jqXHR);
                        }
                    });
            }

            function getPersons(pagina) {
                if (page>0) {
                    $("#prev").prop('disabled',false);
                }else{
                    $("#prev").prop('disabled',true);
                }

                tableP.fnClearTable();
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.sPersonUrl, {
                    strFilter: encodeURI($('#filtro').val()),
                    pPaginaActual: pagina,
                    ajax: 'true'
                }, function (data) {
                    var mensaje="";
                    try{
                        mensaje = data.mensaje;
                    }catch (err){
                        mensaje="";
                    }
                    if (mensaje!=undefined && mensaje!="") {
                        setTimeout($.unblockUI, 500);
                        $.smallBox({
                            title: mensaje,
                            content: $("#smallBox_content").val(),
                            color: "#C46A69",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    } else {
                        var len = 0;
                        if (data != null)
                            len = data.length;

                        if (len < rowsPage) {
                            $("#next").prop('disabled', true);
                        } else {
                            $("#next").prop('disabled', false);
                        }
                        if (len > 0) {
                            for (var i = 0; i < len; i++) {
                                var nombreMuniRes = "";

                                if (data[i].municipioResidencia != null) {
                                    nombreMuniRes = data[i].municipioResidencia.nombre;
                                }
                                var edad = getAge(data[i].fechaNacimiento).split(",");
                                tableP.fnAddData(
                                    [
                                        (data[i].identificacion!=null?data[i].identificacion:""),
                                        data[i].primerNombre,
                                        (data[i].segundoNombre != null ? data[i].segundoNombre : ""),
                                        data[i].primerApellido,
                                        (data[i].segundoApellido != null ? data[i].segundoApellido : ""),
                                        data[i].fechaNacimiento,
                                        edad[0],
                                        nombreMuniRes,
                                            '<button title="Seleccionar" type="button" class="btn btn-success btn-xs" data-id="' + data[i].personaId + '"> <i class="fa fa-check-circle"></i></button>']);
                            }
                            setTimeout($.unblockUI, 500);
                        } else {
                            setTimeout($.unblockUI, 500);
                            $.smallBox({
                                title: $("#msg_no_results_found").val(),
                                content: $("#smallBox_content").val(),
                                color: "#C79121",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        }
                    }
                }).fail(function (jqXHR) {
                        setTimeout($.unblockUI, 10);
                        if (jqXHR.status=="200") {
                            $.smallBox({
                                title: $("#msg_no_results_found").val(),
                                content: $("#smallBox_content").val(),
                                color: "#C79121",
                                iconSmall: "fa fa-warning",
                                timeout: 4000
                            });
                        }else {
                            validateLogin(jqXHR);
                        }
                    });
            }

        }
    };

}();