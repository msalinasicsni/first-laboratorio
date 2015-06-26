var Users = function () {

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
            }
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
            var table1 = $('#users-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
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

            /****************************************************/
            /******************EDITAR USUARIO*********************/
            /****************************************************/
            var $validator = $("#edit-user-form").validate({
                rules: {
                    completeName: {required: true },
                    correoe : {
                        email : true
                    },
                    laboratorio : {
                        required : true}
                },

                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                },
                  submitHandler: function (form) {
                 //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                 updateUser();
                 }
            });

            function updateUser() {
                var valueObj = {};
                valueObj['mensaje'] = '';
                valueObj['habilitado']= ($('#checkbox-enable').is(':checked'));
                valueObj['userName'] = $('#username').val();
                valueObj['nombreCompleto'] = $('#completeName').val();
                valueObj['email'] = $('#correoe').val();
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                setTimeout(function () {
                                    window.location.href = parametros.sAdminUserUrl+$('#username').val();
                                }, 4000);
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            /****************************************************/
            /******************CAMBIAR CONTRASEÑA*********************/
            /****************************************************/
            jQuery.validator.addMethod("noSpace", function(
                value, element) {
                return value.indexOf(" ") < 0 && value != "";
            }, "No se permite espacio en blanco");

            var $validator2 = $("#cpass-user-form").validate({
                rules: {
                    password : {
                        required : true,
                        noSpace : true,
                        minlength : 4
                    },
                    confirm_password : {
                        required : true,
                        noSpace : true,
                        minlength : 4,
                        equalTo : "#password"
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
                valueObj['password']=  $('#password').val();
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg ,
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
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            /****************************************************/
            /******************AGREGAR USUARIO*********************/
            /****************************************************/
            jQuery.validator.addMethod("noSpace", function(
                value, element) {
                return value.indexOf(" ") < 0 && value != "";
            }, "No se permite espacio en blanco");

            var $validator3 = $("#add-user-form").validate({
                rules: {
                    username : {
                        required : true,
                        noSpace : true,
                        minlength : 5,
                        maxlength : 50
                    },
                    completeName : {
                        required : true,
                        minlength : 5,
                        maxlength : 250
                    },
                    email : {
                        email : true
                    },
                    password : {
                        required : true,
                        noSpace : true,
                        minlength : 4
                    },
                    confirm_password : {
                        required : true,
                        noSpace : true,
                        minlength : 4,
                        equalTo : "#password"
                    },
                    laboratorio : {
                        required : true}
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
                valueObj['habilitado']= ($('#checkbox-enable').is(':checked'));
                valueObj['password'] = $('#password').val();
                valueObj['labAsignado'] = $('#laboratorio').find('option:selected').val();
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg = $("#msjSuccessful").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                setTimeout(function () {
                                    window.location.href = parametros.sAdminUserUrl+$('#username').val();
                                }, 4000);
                            }
                            desbloquearUI();
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            /****************************************************/
            /******************ROLE ADMIN*********************/
            /****************************************************/
            $("#btn-mkAdmin").click(function(){
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            }else{
                                var msg = $("#msjMkAdmin").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl+$('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            $("#btn-mkNoAdmin").click(function(){
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            }else{
                                var msg = $("#msjMkNoAdmin").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl+username;
                                }, 3000);
                            }

                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }


            /****************************************************/
            /******************ROLE RECEPCION*********************/
            /****************************************************/
            $("#btn-mkRecep").click(function(){
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            }else{
                                var msg = $("#msjMkRecept").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl+$('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            $("#btn-mkNoRecep").click(function(){
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            }else{
                                var msg = $("#msjMkNoRecept").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl+username;
                                }, 3000);
                            }

                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            /****************************************************/
            /******************ROLE ANALISTA*********************/
            /****************************************************/
            $("#btn-mkAnalyst").click(function(){
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            }else{
                                var msg = $("#msjMkAnalyst").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl+$('#username').val();
                                }, 3000);
                            }

                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            $("#btn-mkNoAnalyst").click(function(){
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
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#disappear").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 3000
                                });
                                desbloquearUI();
                            }else{
                                var msg = $("#msjMkNoAnalyst").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                setTimeout(function () {
                                    desbloquearUI();
                                    window.location.href = parametros.sUsuarioUrl+username;
                                }, 3000);
                            }

                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            var areasTable = $('#areas-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "pageLength": 4,
                "columns": [
                    null,
                    {
                        "className":      'overrideValue',
                        "orderable":      false
                    }
                ],
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#areas-list'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },

                fnDrawCallback : function() {
                    $('.overrideValue')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                }
            });
            var examenesTable = $('#examenes-list').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "pageLength": 4,
                "columns": [
                    null,null,
                    {
                        "className":      'overrideValue',
                        "orderable":      false
                    }
                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#examenes-list'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.overrideValue')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);
                }
            });

            function showModalAreaExamen(){
                $("#modalAreaExamen").modal({
                    show: true
                });
            }

            function cargarAutoridadArea(){
                $.getJSON(parametros.autoridadAreaUrl, {
                    userName: $('#username').val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    areasTable.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverrideC = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idAutoridadArea+' ' +
                            '> <i class="fa fa-times"></i>';
                        areasTable.fnAddData(
                            [data[i].area.nombre, btnOverrideC ]);
                    }

                });
            }

            function cargarAutoridadExamen(){
                $.getJSON(parametros.autoridadExamenUrl, {
                    userName: $('#username').val(),
                    ajax: 'false'
                }, function (data) {
                    var len = data.length;
                    examenesTable.fnClearTable();
                    for (var i = 0; i < len; i++) {
                        var btnOverrideC = ' <button type="button" class="btn btn-default btn-xs btn-danger" data-id='+data[i].idAutoridadExamen+' ' +
                            '> <i class="fa fa-times"></i>';
                        examenesTable.fnAddData([data[i].examen.nombre,data[i].autoridadArea.area.nombre, btnOverrideC ]);
                    }

                });
            }

            var $validator4 = $("#area-examen-form").validate({
                rules: {
                    idArea : {
                        required : true}
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                }
            });

            var $validator5 = $("#examen-form").validate({
                rules: {
                    idExamen : {
                        required : true}
                },
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());

                }
            });

            $('#btnArea').click(function() {
                var $validarForm = $("#area-examen-form").valid();
                if (!$validarForm) {
                    $validator4.focusInvalid();
                    return false;
                }else{
                    alert("guardar area");
                }

            });

            $('#btnExamen').click(function() {
                var $validarForm =$("#examen-form").valid();
                if (!$validarForm) {
                    $validator5.focusInvalid();
                    return false;
                }else{
                    alert("guardar examen");
                }

            });

            $("#btn-areaExam").click(function(){
                showModalAreaExamen();
                cargarAutoridadArea();
                cargarAutoridadExamen();
            });

            function overrideHandler(){
                var idCatalogoLista = $(this.innerHTML).data('id');
               // overrideValue(idCatalogoLista);


            }
        }
    };
}();