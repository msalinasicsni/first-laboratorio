/**
 * Created by FIRSTICT on 8/4/2015.
 */
var CreateApplicant = function () {

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
            function guardar() {
                var solicitante = {
                    idSolicitante: $("#idSolicitante").val(), // se pasa el id del maestro que se esta editando,
                    nombre: $("#nombre").val(),
                    direccion: $("#direccion").val(),
                    telefono: $("#telefono").val(),
                    contacto: $("#contacto").val(),
                    correoContacto: $("#correoContacto").val(),
                    telefonoContacto: $("#telefonoContacto").val(),
                    habilitado: ($('#checkbox-enable').is(':checked'))
                };
                var esEdicion = ($("#idSolicitante").val() != null && $("#idSolicitante").val().trim().length > 0);
                var solicitanteObj = {};
                solicitanteObj['idSolicitante'] = '';
                solicitanteObj['mensaje'] = '';
                solicitanteObj['solicitante'] = solicitante;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(solicitanteObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#smallBox_content4s").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                var msg;
                                if (esEdicion) {
                                    msg = $("#msg_updated").val();
                                } else {
                                    msg = $("#msg_added").val();
                                }
                                $.smallBox({
                                    title: msg,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 2000
                                });
                                window.location.href = parametros.solicitanteUrl + data.idSolicitante;
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    }
                )
            }

            $('#create-form').validate({
                // Rules for form validation
                rules: {
                    nombre: {
                        required: true
                    },
                    telefono: {
                        required: true
                    },
                    contacto: {
                        required: true
                    },
                    telefonoContacto: {
                        required: true
                    },
                    correoContacto: {
                        email: true
                    }
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //table1.fnClearTable();
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    guardar();
                }
            });
        }
    };

}();