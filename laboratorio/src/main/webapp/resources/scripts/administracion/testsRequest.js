/**
 * Created by souyen-ics on 07-02-15.
 */

var TestsRequest  = function () {
    return {
        init: function (parametros) {
            getTestsRequest();

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
            var catalogueTable = $('#records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "columns": [
                    null, null, null,
                    {
                        "className":      'detail',
                        "orderable":      false
                    }


                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#records'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


               fnDrawCallback : function() {
                    $('.detail')
                        .off("click", detailExa)
                        .on("click", detailExa);


                }

            });

            function detailExa(){
                var data =  $(this.innerHTML).data('id');
                if(data != null){
                    var detalle = data.split("^");
                    var id= detalle[0];
                    var tipo = detalle[1];
                    var nombre = detalle[2];
                    var area = detalle[3];
                    var soli = nombre;

                    $('#sol').html(soli);
                    $('#div1').hide();
                    $('#div2').fadeIn('slow');
                    $('#dBack').show();
                    getExams(id, tipo);
                    getTestsByArea(area);
                    $('#idSolicitud').val(id);
                    $('#tipo').val(tipo);
                }

            }

            $('#btnBack').click(function() {
                $('#dBack').hide();
                $('#sol').html("");
                $('#div2').hide();
                $('#id').val('');
                $('#div1').fadeIn('slow');
                $('#idSolicitud').val('');
                $('#tipo').val('');

            });

            var testsTable = $('#tests').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                    "t"+
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true,

                "columns": [
                    null,
                    {
                        "className":      'defaultHandler',
                        "orderable":      false
                    },
                    {
                        "className":      'overrideT',
                        "orderable":      false
                    }

                ],

                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#tests'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                },
                fnDrawCallback : function() {
                    $('.defaultHandler')
                        .off("click", activateDeactivateT)
                        .on("click", activateDeactivateT);
                    $('.overrideT')
                        .off("click", overrideTe)
                        .on("click", overrideTe);

                }

            });


            function getTestsRequest() {
                $.getJSON(parametros.catalogueUrl, {
                    ajax: 'true'
                }, function (data) {
                    catalogueTable.fnClearTable();
                    var len = Object.keys(data).length;
                    for (var i = 0; i < len; i++) {

                        var btnDetail = '<button type="button" title="Examenes asociados" class="btn btn-primary btn-xs" data-id="'+data[i].id+ "^" + data[i].tipo + "^" + data[i].nombre + "^"+ data[i].idArea +
                            '" > <i class="fa fa-list"></i>' ;

                        catalogueTable.fnAddData(
                            [data[i].nombre, data[i].tipo, data[i].area, btnDetail]);


                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            function getExams(id, tipo) {
                $.getJSON(parametros.testsUrl, {
                    ajax: 'true',
                    id: id,
                    tipo:tipo
                }, function (dataToLoad) {
                    testsTable.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    for (var i = 0; i < len; i++) {
                        var btnOverride = '<button type="button" title="Anular" class="btn btn-danger btn-xs" data-id="'+dataToLoad[i].id +
                            '" > <i class="fa fa-times"></i>' ;
                        var btnDefault = '<button type="button" title="Activar" class="btn btn-danger btn-xs" data-id="'+dataToLoad[i].id +"^"+dataToLoad[i].porDefecto+
                            '" > '+$("#msg_no").val();
                        if ($('#tipo').val() === 'Estudio'){
                            btnDefault = "No Aplica";
                        }else if (dataToLoad[i].porDefecto == 'true'){
                            btnDefault = '<button type="button" title="Desactivar" class="btn btn-success btn-xs" data-id="'+dataToLoad[i].id +"^"+dataToLoad[i].porDefecto+
                                '" > '+$("#msg_yes").val();
                        }


                       testsTable.fnAddData(
                            [dataToLoad[i].nombreExamen, btnDefault, btnOverride]);
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

            $('#btnAddExa').click(function() {
                showModal();
            });


            function getTestsByArea(idArea){
                    $.getJSON(parametros.testsAreaUrl, {
                        idArea: idArea,
                        ajax: 'true'
                    }, function (data) {
                        var html = null;
                        var len = data.length;
                        html += '<option value="">' + $("#opt_select").val() + '...</option>';
                        for (var i = 0; i < len; i++) {
                            html += '<option value="' + data[i].idExamen + '">'
                                + data[i].nombre
                                + '</option>';
                            // html += '</option>';
                        }
                        $('#examen').html(html);
                    }).fail(function(jqXHR) {
                        unBlockUI();
                        validateLogin(jqXHR);
                    });
                }

            <!-- Validacion formulario -->
            var $validator = $("#form").validate({
                // Rules for form validation
                rules: {
                    examen: {required : true}
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnAddTest').click(function() {
                var $validarModal = $("#form").valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    addTest();
                }
            });

            function overrideTe(){
                var data =  $(this.innerHTML).data('id');
                if (data != null) {
                    overrideTest(data);
                }
            }

            function activateDeactivateT(){
                var data =  $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split("^");
                    activateDeactivateTest(detalle[0], detalle[1]);
                }
            }

            function addTest() {
                var obj = {};
                obj['mensaje'] = '';
                obj['idSolicitud'] = $('#idSolicitud').val();
                obj['idExamen'] = $('#examen').val();
                obj['pasivo'] = '';
                obj['porDefecto'] = ($('#chk_defecto').is(':checked'));
                obj['idRecord'] = '';
                obj['tipo'] = $('#tipo').val();

                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveTestUrl,
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
                                getExams($('#idSolicitud').val(), $('#tipo').val());
                                $('#examen').val('');
                                var msg = $("#succ").val();
                                $.smallBox({
                                    title: msg,
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

            function overrideTest(idRecord) {
                var obj = {};
                obj['mensaje'] = '';
                obj['idSolicitud'] = $('#idSolicitud').val();
                obj['idExamen'] = '';
                obj['pasivo'] = 'true';
                obj['idRecord'] = idRecord;
                obj['tipo'] = $('#tipo').val();

                var opcSi = $("#msg_yes").val();
                var opcNo = $("#msg_no").val();

                $.SmartMessageBox({
                    title: $("#msg_conf").val(),
                    content: $("#msg_overrideT_confirm_c").val(),
                    buttons: '['+opcSi+']['+opcNo+']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                        blockUI(parametros.blockMess);
                        $.ajax(
                            {
                                url: parametros.saveTestUrl,
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
                                        getExams($('#idSolicitud').val(), $('#tipo').val());
                                        var msg = $("#msg_succOverrideT").val();
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
                            title: $("#msg_overrideT_cancel").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>"+$("#disappear").val()+"</i>",
                            color: "#C46A69",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 4000
                        });
                    }
                });
            }

            function activateDeactivateTest(idRecord, porDefecto) {
                var obj = {};
                obj['mensaje'] = '';
                obj['porDefecto'] = porDefecto;
                obj['idRecord'] = idRecord;
                obj['tipo'] = $('#tipo').val();

                var opcSi = $("#msg_yes").val();
                var opcNo = $("#msg_no").val();
                var msgConfirm = $("#msg_activate_confirm_c").val();
                if (porDefecto == 'true'){
                    msgConfirm = $("#msg_deactivate_confirm_c").val();
                }


                $.SmartMessageBox({
                    title: $("#msg_conf").val(),
                    content: msgConfirm,
                    buttons: '['+opcSi+']['+opcNo+']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                        blockUI(parametros.blockMess);
                        $.ajax(
                            {
                                url: parametros.saveTestUrl,
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
                                        getExams($('#idSolicitud').val(), $('#tipo').val());
                                        var msg = $("#msg_action_successfull").val();
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
                            title: $("#msg_action_canceled").val(),
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