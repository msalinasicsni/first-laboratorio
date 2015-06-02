var ApproveResult = function () {
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
            var table1 = $('#solicitudesList').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,
                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#solicitudesList'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            var text_selected_all = $("#text_selected_all").val();
            var text_selected_none = $("#text_selected_none").val();
            var table2 = $('#examenes_repite').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "T"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,
                "searching": false,
                "lengthChange": false,
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#orders_result'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                "oTableTools": {
                    "sSwfPath": parametros.sTableToolsPath,
                    "sRowSelect": "multi",
                    "aButtons": [ {"sExtends":"select_all", "sButtonText": text_selected_all}, {"sExtends":"select_none", "sButtonText": text_selected_none}]
                }
            });

            <!-- formulario de búsqueda de resultados finales -->
            $('#approveResult-form').validate({
                // Rules for form validation
                rules: {
                    fecFinTomaMx:{required:function(){return $('#fecInicioTomaMx').val().length>0;}},
                    fecInicioTomaMx:{required:function(){return $('#fecFinTomaMx').val().length>0;}}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    //add here some ajax code to submit your form or just call form.submit() if you want to submit the form without ajax
                    aprobarResultado();
                }
            });

            //formulario de anulación de resultado
            $('#reject-result-form').validate({
                // Rules for form validation
                rules: {
                    causaRechazo: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                    var oTT = TableTools.fnGetInstance('examenes_repite');
                    var aSelectedTrs = oTT.fnGetSelected();
                    var len = aSelectedTrs.length;
                    var opcSi = $("#confirm_msg_opc_yes").val();
                    var opcNo = $("#confirm_msg_opc_no").val();
                    if (len > 0) {
                        rechazarResultado();
                    }else{
                        $.smallBox({
                            title : $("#msg_select_exam").val(),
                            content : "<i class='fa fa-clock-o'></i> <i>"+$("#smallBox_content").val()+"</i>",
                            color : "#C46A69",
                            iconSmall : "fa fa-times fa-2x fadeInRight animated",
                            timeout : 4000
                        });
                    }
                }
            });

            $("#reject-result").click(function() {
                var opcSi = $("#confirm_msg_opc_yes").val();
                var opcNo = $("#confirm_msg_opc_no").val();
                $.SmartMessageBox({
                    title: $("#msg_confirm_title").val(),
                    content: $("#msg_confirm_content").val(),
                    buttons: '[' + opcSi + '][' + opcNo + ']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {
                        getExamsToRepeat();
                        showModalReject();
                    }
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#msg_reject_cancel").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#smallBox_content").val() + "</i>",
                            color: "#3276B1",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 3000
                        });
                    }
                })
            });

            function getExamsToRepeat () {
                bloquearUI(parametros.blockMess);
                var idSolicitud = $('#idSolicitud').val();
                $.getJSON(parametros.sExamsRepeat, {
                    idSolicitud: idSolicitud,
                    ajax : 'true'
                }, function(dataToLoad) {
                    console.log(dataToLoad);
                    table2.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            table2.fnAddData(
                                [dataToLoad[i].codExamen.nombre+"<input type='hidden' value='"+dataToLoad[i].idOrdenExamen+"'/>",dataToLoad[i].codExamen.area.nombre]);
                        }
                    }else{
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#disappear").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                    desbloquearUI();
                })
                    .fail(function() {
                        desbloquearUI();
                        alert( "error" );
                    });
            }

            function showModalReject(){
                $("#myModal").modal({
                    show: true
                });
            }

            function hideModalReject(){
                $('#myModal').modal('hide');
            }

            getRespuestasSolicitud();

            function getRespuestasSolicitud() {
                bloquearUI(parametros.blockMess);
                var idSolicitud = $('#idSolicitud').val();
                $.getJSON(parametros.searchUrl, {
                    idSolicitud: idSolicitud,
                    ajax : 'true'
                }, function(dataToLoad) {
                    console.log(dataToLoad);
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            console.log(dataToLoad[i].resultado);
                            table1.fnAddData(
                                [dataToLoad[i].respuesta,dataToLoad[i].valor,dataToLoad[i].fechaResultado]);
                        }
                    }else{
                        $.smallBox({
                            title: $("#msg_no_results_found").val() ,
                            content: $("#disappear").val(),
                            color: "#C79121",
                            iconSmall: "fa fa-warning",
                            timeout: 4000
                        });
                    }
                   desbloquearUI();
                })
                    .fail(function() {
                        desbloquearUI();
                        alert( "error" );
                    });
            }

            function aprobarResultado(){
                bloquearUI(parametros.blockMess);
                var objResultado = {};
                objResultado["idSolicitud"] = $("#idSolicitud").val();
                objResultado["mensaje"] = '';
                $.ajax(
                    {
                        url: parametros.sApproveResult,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(objResultado),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        async: false,
                        success: function (data) {
                            desbloquearUI();
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg = $("#msg_result_approve").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });

                                setTimeout(function () {window.location.href = parametros.sInitUrl},3000);
                            }
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }

            function rechazarResultado(){
                bloquearUI(parametros.blockMess);
                var oTT = TableTools.fnGetInstance('examenes_repite');
                var aSelectedTrs = oTT.fnGetSelected();
                var len = aSelectedTrs.length;
                var idOrdenesEx = {};
                //el input hidden debe estar siempre en la primera columna
                for (var i = 0; i < len; i++) {
                    var texto = aSelectedTrs[i].firstChild.innerHTML;
                    var input = texto.substring(texto.lastIndexOf("<"),texto.length);
                    idOrdenesEx[i]=$(input).val();
                }
                console.log(idOrdenesEx);
                var objResultado = {};
                objResultado["idSolicitud"] = $("#idSolicitud").val();
                objResultado["causaRechazo"] = $("#causaRechazo").val();
                objResultado["idOrdenes"] = idOrdenesEx;
                objResultado['cantOrdenes']=len;
                objResultado["mensaje"] = '';
                $.ajax(
                    {
                        url: parametros.sRejectResult,
                        type: 'POST',
                        dataType: 'json',
                        data: JSON.stringify(objResultado),
                        contentType: 'application/json',
                        mimeType: 'application/json',
                        async: false,
                        success: function (data) {
                            desbloquearUI();
                            if (data.mensaje.length > 0){
                                $.smallBox({
                                    title: data.mensaje ,
                                    content: $("#smallBox_content").val(),
                                    color: "#C46A69",
                                    iconSmall: "fa fa-warning",
                                    timeout: 4000
                                });
                            }else{
                                var msg = $("#msg_result_reject").val();
                                $.smallBox({
                                    title: msg ,
                                    content: $("#smallBox_content").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 3000
                                });
                                hideModalReject();
                                setTimeout(function () {window.location.href = parametros.sInitUrl},3000);
                            }
                        },
                        error: function (data, status, er) {
                            desbloquearUI();
                            alert("error: " + data + " status: " + status + " er:" + er);
                        }
                    });
            }
        }
    };

}();