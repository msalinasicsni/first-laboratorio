var Users = function () {

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
            var table1 = $('#users-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "aoColumns" : [
                    {sClass: "aw-left" },{sClass: "aw-left" },{sClass: "aw-left" },{sClass: "centrado" },{sClass: "centrado" },{sClass: "aw-left" },{sClass: "aw-left" },{sClass: "centrado" }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#users-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            getAllUsers();

            /****************************************************/
            /******************EDITAR USUARIO*********************/
            /****************************************************/
            var $validator = $("#edit-user-form").validate({
                rules: {
                    completeName: {required: true },
                    correoe: {
                        email: true
                    },
                    laboratorio: {
                        required: true}
                },

                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    updateUser();
                }
            });

            function getAllUsers() {
                bloquearUI(parametros.blockMess);
                $.getJSON(parametros.getAllUsers, {
                    ajax: 'true'
                }, function (data) {
                    table1.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {
                        var urlVer = parametros.usuarioUrl.replaceAll("{username}", data[i].username);
                        var urlEditar = parametros.editUrl.replaceAll("{username}", data[i].username);
                        var urlChanPass = parametros.chgpassUrl.replaceAll("{username}", data[i].username);

                        var aUsername = '<a href="'+urlVer+'">'+data[i].username+'</a>';
                        var aDescripcion = '<a href="'+urlVer+'">'+data[i].descripcion+'</a>';

                        var btnVer = '<a title="Ver" class="btn btn-xs btn-primary" href="'+urlVer+'"> <i class="fa fa-search"></i></a> '+
                            ' <a title="Editar" class="btn btn-xs btn-primary" href="'+urlEditar+'"> <i class="fa fa-edit"></i></a> '+
                            ' <a title="Cambiar Contrase\u00F1a" class="btn btn-xs btn-warning" href="'+urlChanPass+'"> <i class="fa fa-lock"></i></a> ';

                        var habilitado = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        var nivelCentral = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        if (data[i].enabled == false){
                            habilitado = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                        }
                        if (data[i].nivelCentral == false){
                            nivelCentral = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                        }
                        console.log(btnVer);
                        table1.fnAddData(
                            [aUsername, aDescripcion, data[i].email, habilitado, nivelCentral, data[i].roles, data[i].laboratorio ,btnVer]);
                    }
                    desbloquearUI();
                }).fail(function(jqXHR) {
                    desbloquearUI();
                    validateLogin(jqXHR);
                });
            }

            function updateUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['habilitado'] = ($('#checkbox-enable').is(':checked'));
                valueObj['userName'] = $('#username').val();
                valueObj['nombreCompleto'] = $('#completeName').val();
                valueObj['email'] = $('#correoe').val();
                valueObj['labAsignado'] = $('#laboratorio').find('option:selected').val();
                valueObj['nivelCentral'] = ($('#checkbox-nc').is(':checked'));
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sUpdateUserUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                setTimeout(function () {
                                    window.location.href = parametros.sAdminUserUrl + $('#username').val();
                                }, 4000);
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }

                    });
            }

            /****************************************************/
            /******************CAMBIAR CONTRASEÑA*********************/
            /****************************************************/
            jQuery.validator.addMethod("noSpace", function (value, element) {
                return value.indexOf(" ") < 0 && value != "";
            }, "No se permite espacio en blanco");

            var $validator2 = $("#cpass-user-form").validate({
                rules: {
                    password: {
                        required: true,
                        noSpace: true,
                        minlength: 4
                    },
                    confirm_password: {
                        required: true,
                        noSpace: true,
                        minlength: 4,
                        equalTo: "#password"
                    }
                },

                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    changePassword();
                }
            });

            function changePassword() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['password'] = $('#password').val();
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sChangePassUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                setTimeout(function () {
                                    window.location.href = parametros.sUsuariosUrl;
                                }, 4000);
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            /****************************************************/
            /******************AGREGAR USUARIO*********************/
            /****************************************************/
            jQuery.validator.addMethod("noSpace", function (value, element) {
                return value.indexOf(" ") < 0 && value != "";
            }, "No se permite espacio en blanco");

            var $validator3 = $("#add-user-form").validate({
                rules: {
                    username: {
                        required: true,
                        noSpace: true,
                        minlength: 5,
                        maxlength: 50
                    },
                    completeName: {
                        required: true,
                        minlength: 5,
                        maxlength: 250
                    },
                    email: {
                        email: true
                    },
                    password: {
                        required: true,
                        noSpace: true,
                        minlength: 4
                    },
                    confirm_password: {
                        required: true,
                        noSpace: true,
                        minlength: 4,
                        equalTo: "#password"
                    },
                    laboratorio: {
                        required: true}
                },

                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    addUser();
                }
            });

            function addUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                valueObj['nombreCompleto'] = $('#completeName').val();
                valueObj['email'] = $('#correoe').val();
                valueObj['habilitado'] = ($('#checkbox-enable').is(':checked'));
                valueObj['password'] = $('#password').val();
                valueObj['labAsignado'] = $('#laboratorio').find('option:selected').val();
                valueObj['nivelCentral'] = ($('#checkbox-nc').is(':checked'));
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sAddUserUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            } else {
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                setTimeout(function () {
                                    window.location.href = parametros.sAdminUserUrl + $('#username').val();
                                }, 4000);
                            }
                            desbloquearUI();
                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            /****************************************************/
            /******************ROLE ADMIN*********************/
            /****************************************************/
            $("#btn-mkAdmin").click(function () {
                mkAdminUser();
            });
            function mkAdminUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sMkAdminUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkAdmin").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + $('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            $("#btn-mkNoAdmin").click(function () {
                mkNoAdminUser();
            });
            function mkNoAdminUser() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.sMkNoAdminUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkNoAdmin").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + username;
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }


            /****************************************************/
            /******************ROLE RECEPCION*********************/
            /****************************************************/
            $("#btn-mkRecep").click(function () {
                mkReceptionistUser();
            });
            function mkReceptionistUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkReceptUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkRecept").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + $('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            $("#btn-mkNoRecep").click(function () {
                mkNoReceptionistUser();
            });
            function mkNoReceptionistUser() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkNoReceptUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkNoRecept").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + username;
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            /****************************************************/
            /******************ROLE ANALISTA*********************/
            /****************************************************/
            $("#btn-mkAnalyst").click(function () {
                mkAnalystUser();
            });
            function mkAnalystUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkAnalystUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkAnalyst").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + $('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            $("#btn-mkNoAnalyst").click(function () {
                mkNoAnalystUser();
            });
            function mkNoAnalystUser() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkNoAnalystUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkNoAnalyst").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + username;
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            var areasTable = $('#areas-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "pageLength": 4,
                "columns": [
                    null,
                    {
                        "className": 'overrideValue',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#areas-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },

                fnDrawCallback: function () {
                    $('.overrideValue')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                }
            });

            var examenesTable = $('#examenes-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "pageLength": 4,
                "columns": [
                    null, null,
                    {
                        "className": 'overrideValue',
                        "orderable": false
                    }
                ],

                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#examenes-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback: function () {
                    $('.overrideValue')
                        .off("click", overrideHandlerEx)
                        .on("click", overrideHandlerEx);
                }
            });

            function showModalArea() {
                $("#modalArea").modal({
                    show: true
                });
            }

            function showModalExamen() {
                $("#modalExamen").modal({
                    show: true
                });
            }

            function cargarAutoridadArea() {
                $.getJSON(parametros.autoridadAreaUrl, {
                    userName: $('#username').val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    areasTable.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverrideC = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id=' + data[i].idAutoridadArea + ' ' +
                            '> <i class="fa fa-times"></i>';
                        areasTable.fnAddData(
                            [data[i].area.nombre, btnOverrideC ]);
                    }

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function cargarAreasDisponibles() {
                $.getJSON(parametros.areasUrl, {
                    userName: $("#username").val(),
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].idArea + '">'
                            + data[i].nombre
                            + '</option>';
                    }
                    $('#idArea').html(html).val("").change();
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function cargarAutoridadExamen() {
                $.getJSON(parametros.autoridadExamenUrl, {
                    userName: $('#username').val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    examenesTable.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverrideC = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id=' + data[i].idAutoridadExamen + ' ' +
                            '> <i class="fa fa-times"></i>';
                        examenesTable.fnAddData([data[i].examen.nombre, data[i].autoridadArea.area.nombre, btnOverrideC ]);
                    }

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function cargarExamenesDisponibles() {
                $.getJSON(parametros.examenesUrl, {
                    userName: $("#username").val(),
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].idExamen + '">'
                            + data[i].nombre
                            + '</option>';
                    }
                    $('#idExamen').html(html);
                    $('#idExamen').val("").change();
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            var $validator4 = $("#areas-form").validate({
                rules: {
                    idArea: {
                        required: true}
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                }
            });

            var $validator5 = $("#examenes-form").validate({
                rules: {
                    idExamen: {
                        required: true}
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                }
            });

            $("#btn-Areas").click(function () {
                showModalArea();
                cargarAreasDisponibles();
                cargarAutoridadArea();
            });

            $("#btn-Examenes").click(function () {
                showModalExamen();
                cargarExamenesDisponibles();
                cargarAutoridadExamen();
            });

            function agregarAreaUsuario() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                valueObj['idArea'] = $('#idArea').find('option:selected').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.areaUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful1").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarAreasDisponibles();
                                cargarAutoridadArea();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function deshabilitarAreaUsuario(idAutoridadArea) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idAutoridadArea'] = idAutoridadArea;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideAreaUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful2").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarAreasDisponibles();
                                cargarAutoridadArea();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function agregarExamenUsuario() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                valueObj['idExamen'] = $('#idExamen').find('option:selected').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.examenUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful3").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarExamenesDisponibles();
                                cargarAutoridadExamen();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function deshabilitarExamenUsuario(idAutoridadExamen) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idAutoridadExamen'] = idAutoridadExamen;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideExamenUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful4").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarExamenesDisponibles();
                                cargarAutoridadExamen();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideHandler() {
                var idAutoridadArea = $(this.innerHTML).data('id');
                if (idAutoridadArea != null) {
                    var opcSi = $("#confirm_msg_opc_yes").val();
                    var opcNo = $("#confirm_msg_opc_no").val();
                    $.SmartMessageBox({
                        title: $("#msjConfirm").val(),
                        content: $("#msjOverride").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            deshabilitarAreaUsuario(idAutoridadArea);
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msjOverrideC").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                color: "#3276B1",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 3000
                            });
                        }
                    })
                }
            }

            function overrideHandlerEx() {
                var idAutoridadExamen = $(this.innerHTML).data('id');
                if (idAutoridadExamen != null) {
                    var opcSi = $("#confirm_msg_opc_yes").val();
                    var opcNo = $("#confirm_msg_opc_no").val();
                    $.SmartMessageBox({
                        title: $("#msjConfirm").val(),
                        content: $("#msjOverride").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            deshabilitarExamenUsuario(idAutoridadExamen);
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msjOverrideC").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                color: "#3276B1",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 3000
                            });
                        }
                    })
                }
            }

            $('#btnAddArea').click(function () {
                var $validarForm = $("#areas-form").valid();
                if (!$validarForm) {
                    $validator4.focusInvalid();
                    return false;
                } else {
                    agregarAreaUsuario();
                }
            });

            $('#btnAddExamen').click(function () {
                var $validarForm = $("#examenes-form").valid();
                if (!$validarForm) {
                    $validator5.focusInvalid();
                    return false;
                } else {
                    agregarExamenUsuario();
                }
            });

            /****************************************************/
            /******************ROLE DIR*********************/
            /****************************************************/
            $("#btn-mkDir").click(function () {
                mkDirectorUser();
            });
            function mkDirectorUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkDirectorUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkDirector").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + $('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            $("#btn-mkNoDir").click(function () {
                mkNoDirectorUser();
            });
            function mkNoDirectorUser() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkNoDirectorUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkNoDirector").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + username;
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            var direcTable = $('#direccion-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "pageLength": 4,
                "columns": [
                    null,
                    {
                        "className": 'overrideValue',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#direccion-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },

                fnDrawCallback: function () {
                    $('.overrideValue')
                        .off("click", overrideHandlerDir)
                        .on("click", overrideHandlerDir);
                }
            });

            function cargarAutoridadDireccion() {
                $.getJSON(parametros.autoridadDireccionUrl, {
                    userName: $('#username').val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    direcTable.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverrideC = '<button type="button" class="btn btn-default btn-xs btn-danger" data-id=' + data[i].idAutoridadDirec + ' ' +
                            '> <i class="fa fa-times"></i>';
                        direcTable.fnAddData(
                            [data[i].direccion.nombre, btnOverrideC ]);
                    }

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            var $validator6 = $("#direccion-form").validate({
                rules: {
                    idDireccion: {
                        required: true}
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                }
            });

            function showModalDireccion() {
                $("#modalDireccion").modal({
                    show: true
                });
            }

            $("#btn-direction").click(function () {
                showModalDireccion();
                cargarDireccionesDisponibles();
                cargarAutoridadDireccion();
            });

            function cargarDireccionesDisponibles() {
                $.getJSON(parametros.direccionesUrl, {
                    userName: $("#username").val(),
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].idDireccion + '">'
                            + data[i].nombre
                            + '</option>';
                    }
                    $('#idDireccion').html(html);
                    $('#idDireccion').val("").change();
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function agregarDireccionUsuario() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                valueObj['idDireccion'] = $('#idDireccion').find('option:selected').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.direccionUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful5").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarDireccionesDisponibles();
                                cargarAutoridadDireccion();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function deshabilitarDireccionUsuario(idAutoridadDirec) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idAutoridadDirec'] = idAutoridadDirec;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideDireccionUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful6").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarAutoridadDireccion();
                                cargarDireccionesDisponibles();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideHandlerDir() {
                var idAutoridadDirec = $(this.innerHTML).data('id');
                if (idAutoridadDirec != null) {
                    var opcSi = $("#confirm_msg_opc_yes").val();
                    var opcNo = $("#confirm_msg_opc_no").val();
                    $.SmartMessageBox({
                        title: $("#msjConfirm").val(),
                        content: $("#msjOverride").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            deshabilitarDireccionUsuario(idAutoridadDirec);
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msjOverrideC").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                color: "#3276B1",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 3000
                            });
                        }
                    })
                }
            }

            $('#btnAddDireccion').click(function () {
                var $validarForm = $("#direccion-form").valid();
                if (!$validarForm) {
                    $validator6.focusInvalid();
                    return false;
                } else {
                    agregarDireccionUsuario();
                }
            });

            /****************************************************/
            /******************ROLE JEFE*********************/
            /****************************************************/
            $("#btn-mkJD").click(function () {
                mkDepartmentHeadUser();
            });
            function mkDepartmentHeadUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkDepartmentHeadUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkDepartmentHead").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + $('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            $("#btn-mkNoJD").click(function () {
                mkNoDepartmentHeadUser();
            });
            function mkNoDepartmentHeadUser() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.mkNoDepartmentHeadUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjMkNoDepartmentHead").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + username;
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            var departamentoTable = $('#departamento-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "pageLength": 4,
                "columns": [
                    null,
                    {
                        "className": 'overrideValue',
                        "orderable": false
                    }
                ],
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#departamento-list'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },

                fnDrawCallback: function () {
                    $('.overrideValue')
                        .off("click", overrideHandlerDepa)
                        .on("click", overrideHandlerDepa);
                }
            });

            function cargarAutoridadDeparta() {
                $.getJSON(parametros.autoridadDepartaUrl, {
                    userName: $('#username').val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    departamentoTable.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverrideC = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id=' + data[i].idAutoridadDepa + ' ' +
                            '> <i class="fa fa-times"></i>';
                        departamentoTable.fnAddData(
                            [data[i].departamento.nombre, btnOverrideC ]);
                    }

                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            var $validator7 = $("#departamento-form").validate({
                rules: {
                    idDepartamento: {
                        required: true}
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });

            function showModalDepartamento() {
                $("#modalDepartamento").modal({
                    show: true
                });
            }

            $("#btn-department").click(function () {
                showModalDepartamento();
                cargarDepartamentosDisponibles();
                cargarAutoridadDeparta();
            });

            function cargarDepartamentosDisponibles() {
                $.getJSON(parametros.departamentosUrl, {
                    userName: $("#username").val(),
                    ajax: 'true'
                }, function (data) {
                    var html = null;
                    var len = data.length;
                    html += '<option value="">' + $("#text_opt_select").val() + '...</option>';
                    for (var i = 0; i < len; i++) {
                        html += '<option value="' + data[i].idDepartamento + '">'
                            + data[i].nombre
                            + '</option>';
                    }
                    $('#idDepartamento').html(html).val("").change();
                }).fail(function (jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function agregarDepartamentoUsuario() {
                var username = $('#username').val();
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = username;
                valueObj['idDepartamento'] = $('#idDepartamento').find('option:selected').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.departaUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful7").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarDepartamentosDisponibles();
                                cargarAutoridadDeparta();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function deshabilitarDepartaUsuario(idAutoridadDepa) {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['idAutoridadDepa'] = idAutoridadDepa;
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.overrideDepartaUsuarioUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful8").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                cargarAutoridadDeparta();
                                cargarDepartamentosDisponibles();
                                desbloquearUI();
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideHandlerDepa() {
                var idAutoridadDepa = $(this.innerHTML).data('id');
                if (idAutoridadDepa != null) {
                    var opcSi = $("#confirm_msg_opc_yes").val();
                    var opcNo = $("#confirm_msg_opc_no").val();
                    $.SmartMessageBox({
                        title: $("#msjConfirm").val(),
                        content: $("#msjOverride").val(),
                        buttons: '[' + opcSi + '][' + opcNo + ']'
                    }, function (ButtonPressed) {
                        if (ButtonPressed === opcSi) {
                            deshabilitarDepartaUsuario(idAutoridadDepa);
                        }
                        if (ButtonPressed === opcNo) {
                            $.smallBox({
                                title: $("#msjOverrideC").val(),
                                content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                                color: "#3276B1",
                                iconSmall: "fa fa-times fa-2x fadeInRight animated",
                                timeout: 3000
                            });
                        }
                    })
                }
            }

            $('#btnAddDepartamento').click(function () {
                var $validarForm = $("#departamento-form").valid();
                if (!$validarForm) {
                    $validator7.focusInvalid();
                    return false;
                } else {
                    agregarDepartamentoUsuario();
                }
            });

            /****************************************************/
            /******************HABILITAR - DESHABILITAR*********************/
            /****************************************************/

            function enableUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.enableUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful9").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + $('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function disableUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['userName'] = $('#username').val();
                bloquearUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.disableUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(valueObj),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        success: function (data) {
                            if (data.mensaje.length > 0) {
                                $.smallBox({
                                    title: data.mensaje,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            } else {
                                var msg = $("#msjSuccessful10").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl + $('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (jqXHR) {
                            desbloquearUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            $('#btn-Enable').click(function () {
                enableUser();
            });

            $('#btn-Disable').click(function () {
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                $.SmartMessageBox({
                    title: $("#msjConfirm").val(),
                    content: $("#msjDisable").val(),
                    buttons: '[' + opcSi + '][' + opcNo + ']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {
                        disableUser();
                    }
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#msjCanceled").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                            color: "#3276B1",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 3000
                        });
                    }
                })
            });
        }
    };
}();