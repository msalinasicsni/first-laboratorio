/**
 * Created by souyen-ics on 06-10-15.
 */
var AssociationSamplesReq  = function () {

    return {
        init: function (parametros) {
            getNoti();

            function blockUI(){
                var loc = window.location;
                var pathName = loc.pathname.substring(0,loc.pathname.indexOf('/', 1)+1);
                //var mess = $("#blockUI_message").val()+' <img src=' + pathName + 'resources/img/loading.gif>';
                var mess = '<img src=' + pathName + 'resources/img/ajax-loading.gif> ' + parametros.blockMess;
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
            }

            function unBlockUI() {
                setTimeout($.unblockUI, 500);
            }

            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };
            var notiTable = $('#noti-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "columns": [
                    null, null,
                    {
                        "className":      'addAssociation',
                        "orderable":      false
                    }
                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#noti-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback : function() {
                    $('.addAssociation')
                        .off("click", addCHandler)
                        .on("click", addCHandler);


                }
            });


           function addCHandler(){
                var data =  $(this.innerHTML).data('id');
               if (data != null) {
                   $('#dNoti').hide();
                   getMxNoti(data);
                   $('#noti').val(data);
                   $('#dMx-noti').fadeIn('slow');
                   $('#dBack1').show();
               }
            }


            function getNoti() {
                $.getJSON(parametros.notiUrl, {
                    ajax: 'true'
                }, function (data) {
                    notiTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var btnAdd = '<button type="button" title="Tipos de muestras asociadas" class="btn btn-primary btn-xs" data-id="'+data[i].codigo +
                            '" > <i class="fa fa-list"></i>' ;



                        notiTable.fnAddData(
                            [data[i].codigo, data[i].valor, btnAdd]);


                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            $('#btnBack').click(function() {
                $('#dMx-noti').hide();
                $('#dBack1').hide();
                $('#dNoti').fadeIn('slow');
                $('#dBack2').hide();
                $('#dxSt').hide();
                $('#idTipoMxNoti').val('');
            });



            var mxNotiTable = $('#mx-noti-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "columns": [
                    null, null,
                    {
                        "className":      'addAssociation1',
                        "orderable":      false
                    },
                    {
                        "className":      'overrideTipoMx',
                        "orderable":      false
                    }
                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#mx-noti-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback : function() {
                    $('.addAssociation1')
                        .off("click", addCHandler1)
                        .on("click", addCHandler1);

                    $('.overrideTipoMx')
                        .off("click", overrideTMx)
                        .on("click", overrideTMx);

                }
            });

            var detailTable = $('#dx-studies-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "columns": [
                    null, null, null, null,
                    {
                        "className":      'addAssociation2',
                        "orderable":      false
                    }
                ],


                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#dx-studies-records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },

                fnDrawCallback : function() {
                    $('.addAssociation2')
                        .off("click", addCHandler2)
                        .on("click", addCHandler2);

                }



            });

            function addCHandler1(){
                var data =  $(this.innerHTML).data('id');
                $('#dNoti').hide();
                $('#dMx-noti').hide();
                $('#dBack1').hide();
                $('#dBack2').show();
                $('#dxSt').fadeIn('slow');
                $('#idTipoMxNoti').val(data);
                getRoutinesAndStudies(data);
            }

            function overrideTMx(){
                var data =  $(this.innerHTML).data('id');
                var detalle = data.split(",");
                var id= detalle[0];
                var noti = detalle[1];
                overrideAssTipoMx(id, noti);
            }

            function addCHandler2(){
               var data =  $(this.innerHTML).data('id');
               var detalle = data.split(",");
               var id= detalle[0];
               var tipo = detalle[1];
               var idTipoMxNoti = detalle[2];
              overrideRequest(id, tipo, idTipoMxNoti);
            }


            function getMxNoti(codNoti) {
                $.getJSON(parametros.mxNotiUrl, {
                    ajax: 'true',
                    codNoti: codNoti
                }, function (data) {
                    mxNotiTable.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {
                        var btnAdd1 = '<button type="button" title="Solicitudes asociadas" class="btn btn-primary btn-xs" data-id="'+data[i].id +
                            '" > <i class="fa fa-list"></i>' ;

                        var btnOverride = '<button type="button" title="Anular" class="btn btn-danger btn-xs" data-id="'+data[i].id + "," + data[i].tipoNotificacion.codigo +
                            '" > <i class="fa fa-times"></i>' ;
                        mxNotiTable.fnAddData(
                            [data[i].tipoMx.nombre, data[i].tipoNotificacion.valor, btnAdd1, btnOverride]);
                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            $('#btnBack2').click(function() {
                $('#dMx-noti').show();
                $('#dBack1').show();
                $('#dNoti').hide();
                $('#dBack2').show();
                $('#dxSt').hide();
                $('#idTipoMxNoti').val('')
            });


            function getRoutinesAndStudies(idTipoMxNoti) {
                $.getJSON(parametros.dxStUrl, {
                    ajax: 'true',
                    idTipoMxNoti: idTipoMxNoti
                }, function (dataToLoad) {
                    detailTable.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    for (var i = 0; i < len; i++) {
                        var btnOverride = '<button type="button" title="Anular" class="btn btn-danger btn-xs" data-id="'+dataToLoad[i].id + "," + dataToLoad[i].tipoSolicitud + "," + dataToLoad[i].idTipoMxTipoNoti +
                            '" > <i class="fa fa-times"></i>' ;

                        detailTable.fnAddData(
                            [dataToLoad[i].nombre, dataToLoad[i].tipoSolicitud, dataToLoad[i].nombreNotificacion,  dataToLoad[i].tipoMx, btnOverride]);
                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }


            function showModal(){
                $("#myModal").modal({
                    show: true
                });
            }

            function showModal1(){
                $("#myModal1").modal({
                    show: true
                });
            }

            $('#btnAddA').click(function() {
                showModal();
            });

            $('#btnTMxNoti').click(function() {
                showModal1();
            });

            $('#tipo').change(function () {
                var tipo = $('#tipo').val();
                if(tipo == "Estudio"){
                    $('#sDx').hide();
                    $('#codDx').val('').change();
                    $('#sEstudio').show();
                }else if(tipo == "Rutina"){
                    $('#codEstudio').val('').change();
                    $('#sEstudio').hide();
                    $('#sDx').show();

                }
            });

            <!-- Validacion formulario -->
            var $validator = $("#request-form").validate({
                // Rules for form validation
                rules: {
                    tipo: {required : true},
                    codDx:{required:function(){return $('#tipo').val() == 'Rutina'}},
                    codEstudio:{required:function(){return $('#tipo').val() == 'Estudio'}}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnSaveRequest').click(function() {
                var $validarModal = $("#request-form").valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                   addRequest();
                }
            });

            <!-- Validacion formulario -->
            var $validator1 = $("#assTipoMx-form").validate({
                // Rules for form validation
                rules: {
                    tipoMx: {required : true}

                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnSaveAssTipoMx').click(function() {
                var $validarModal = $("#assTipoMx-form").valid();
                if (!$validarModal) {
                    $validator1.focusInvalid();
                    return false;
                } else {
                  addAssTipoMx();
                }
            });

            function addAssTipoMx() {
                var obj = {};
                obj['mensaje'] = '';
                obj['tipoMx'] = $('#tipoMx').val();
                obj['noti'] = $('#noti').val();
                obj['pasivo'] = '';
                obj['idRecord'] = '';

                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveTipoMxUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(obj),
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
                                getMxNoti($('#noti').val());
                                var msg = $("#msjSuccTipoMx").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $('#tipoMx').val('').change();

                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }



            function addRequest() {
                var obj = {};
                obj['mensaje'] = '';
                obj['codDx'] = $('#codDx').val();
                obj['codEstudio'] = $('#codEstudio').val();
                obj['id'] = $('#idTipoMxNoti').val();
                obj['pasivo'] = '';
                obj['idRecord'] = '';
                obj['tipo'] = '';

                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveRequestUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(obj),
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
                                getRoutinesAndStudies($('#idTipoMxNoti').val());
                                var msg = $("#msjSucc").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $('#tipo').val('').change();
                                $('#codDx').val('').change();
                                $('#codEstudio').val('').change();
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideRequest(idRecord, tipo, idTipoMxNoti) {
                var obj = {};
                obj['mensaje'] = '';
                obj['codDx'] = '';
                obj['codEstudio'] = '';
                obj['id'] = $('#idTipoMxNoti').val();
                obj['pasivo'] = 'true';
                obj['idRecord'] = idRecord;
                obj['tipo'] = tipo;

                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();

                $.SmartMessageBox({
                    title: $("#msg_confirmation").val(),
                    content: $("#msg_override_confirm_c").val(),
                    buttons: '['+opcSi+']['+opcNo+']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveRequestUrl,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(obj),
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
                                getRoutinesAndStudies(idTipoMxNoti);
                                var msg = $("#msg_req_cancel").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });

                    }
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#msg_override_cancel").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>"+$("#disappear").val()+"</i>",
                            color: "#C46A69",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 4000
                        });
                    }
                });
            }

            function overrideAssTipoMx(idRecord, noti) {
                var obj = {};
                obj['mensaje'] = '';
                obj['tipoMx'] = '';
                obj['noti'] = '';
                obj['pasivo'] = 'true';
                obj['idRecord'] = idRecord;

                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();

                $.SmartMessageBox({
                    title: $("#msg_confirmation").val(),
                    content: $("#msg_overrideMx_confirm_c").val(),
                    buttons: '['+opcSi+']['+opcNo+']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                        blockUI(parametros.blockMess);
                        $.ajax(
                            {
                                url: parametros.saveTipoMxUrl,
                                type: 'POST',
                                dataType: 'json',
                                data: JSON.stringify(obj),
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
                                        getMxNoti(noti);
                                        var msg = $("#msg_mx_cancel").val();
                                        $.smallBox({
                                            title: msg ,
                                            content: $("#disappear").val(),
                                            color: "#739E73",
                                            iconSmall: "fa fa-success",
                                            timeout: 4000
                                        });
                                    }
                                    unBlockUI();
                                },
                                error: function (jqXHR) {
                                    unBlockUI();
                                    validateLogin(jqXHR);
                                }
                            });

                    }
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#msg_overrideMx_cancel").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>"+$("#disappear").val()+"</i>",
                            color: "#C46A69",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 4000
                        });
                    }
                });
            }
        }
    };

}();