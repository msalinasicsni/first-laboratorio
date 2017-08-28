/**
 * Created by souyen-ics on 07-10-15.
 */
var Request  = function () {
    return {
        init: function (parametros) {
            getRequests();

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
                    null, null, null, null,
                    {
                        "className":      'edit',
                        "orderable":      false
                    },

                    {
                        "className":      'override',
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
                    $('.edit')
                        .off("click", editS)
                        .on("click", editS);

                    $('.override')
                        .off("click",overrideS)
                        .on("click", overrideS);
                }

            });

            function editS() {
                var data = $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var id = detalle[0];
                    var tipo = detalle[1];
                    getRequest(id, tipo);
                    showModal();
                }

            }

            function overrideS() {
                var data = $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var id = detalle[0];
                    var tipo = detalle[1];
                    overrideRequest(id, tipo);
                }


            }

            function showModal(){
                $("#myModal").modal({
                    show: true
                });
            }


            function getRequest(id, tipo) {
                blockUI(parametros.blockMess);
                $.getJSON(parametros.getRequestUrl, {
                    id: id,
                    tipo: tipo,
                    ajax : 'true'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;

                    if (len > 0) {

                        var tipo = $('#tipo');
                        $('#id').val(id);
                        $('#nombre').val(dataToLoad[0].nombre);
                        tipo.val(dataToLoad[0].tipo).change();
                        tipo.prop('disabled', true);
                        if(dataToLoad[0].pasivo =='true'){
                            $('#checkbox').prop('checked',false);
                        }else{
                            $('#checkbox').prop('checked',true);
                        }

                        $('#area').val(dataToLoad[0].idArea).change();
                        $('#prioridad').val(dataToLoad[0].prioridad);
                        $('#codigo').val(dataToLoad[0].codigo);

                    }
                    unBlockUI();
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }


            function getRequests() {
                $.getJSON(parametros.catalogueUrl, {
                    ajax: 'true'
                }, function (data) {
                    catalogueTable.fnClearTable();
                    var len = Object.keys(data).length;

                    for (var i = 0; i < len; i++) {

                        var btnEdit = '<button type="button" title="Editar" class="btn btn-primary btn-xs" data-id="'+data[i].id+ "," + data[i].tipo  +
                            '" > <i class="fa fa-edit"></i>' ;

                        var btnOverride = '<button type="button" title="Anular" class="btn btn-danger btn-xs" data-id="'+data[i].id+ "," + data[i].tipo + "," +
                            '" > <i class="fa fa-times"></i>' ;

                        var pasivo = '<span class="label label-success"><i class="fa fa-thumbs-up fa-lg"></i></span>';
                        if (data[i].pasivo=='true') {
                            pasivo = '<span class="label label-danger"><i class="fa fa-thumbs-down fa-lg"></i></span>';
                            btnOverride = '<button type="button" title="Anular" disabled class="btn btn-danger btn-xs" data-id="'+data[i].id+ "," + data[i].tipo + "," +
                                '" > <i class="fa fa-times"></i>' ;

                        }

                            catalogueTable.fnAddData(
                            [data[i].nombre, data[i].tipo, data[i].area, pasivo, btnEdit, btnOverride]);


                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            <!-- Validacion formulario -->
            var $validator = $("#request-form").validate({
                // Rules for form validation
                rules: {
                    tipo: {required : true},
                    prioridad:{required:function(){return $('#tipo').val() == 'Rutina'}},
                    codigo:{required:function(){return $('#tipo').val() == 'Estudio'}},
                    nombre: {required : true},
                    area: {required : true}

                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnSave').click(function() {
                var $validarModal = $("#request-form").valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    addRequest();
                }
            });

            $('#btnAdd').click(function(){
                var tipo = $('#tipo');
                tipo.val('').change();
                tipo.prop('disabled', false);
                $('#nombre').val('');
                $('#area').val('').change();
                $('#prioridad').val('');
                $('#codigo').val('');
                $('#id').val('');
                $('#checkbox').val('');
                showModal();

            });


            $('#tipo').change(function() {
                var tipo = $('#tipo');
                if(tipo.val() == ("Rutina")){
                    $('#sPriority').fadeIn('slow');
                    $('#sCodigo').hide();
                }else if(tipo.val() == ("Estudio")){
                    $('#sPriority').hide();
                    $('#sCodigo').fadeIn('slow');
                }
            });

            function addRequest() {
                var obj = {};
                obj['mensaje'] = '';
                obj['id'] = $('#id').val();
                obj['nombre'] = $('#nombre').val();
                obj['pasivo']= ($('#checkbox').is(':checked'));
                obj['tipo'] = $('#tipo').val();
                obj['area'] = $('#area').val();
                obj['prioridad'] = $('#prioridad').val();
                obj['codigo'] = $('#codigo').val();


                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.addUpdateUrl,
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
                                getRequests();
                                var tipo = $('#tipo');
                                tipo.val('').change();
                                tipo.prop('disabled', false);
                                $('#nombre').val('');
                                $('#area').val('').change();
                                $('#prioridad').val('');
                                $('#codigo').val('');
                                $('#id').val('');
                                $('#checkbox').val('');

                                var msg = $("#msjSucc").val();
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

            function overrideRequest(id, tipo) {
                var obj = {};
                obj['mensaje'] = '';
                obj['id'] = id;
                obj['nombre'] = '';
                obj['pasivo'] = 'true';
                obj['tipo'] = tipo;
                obj['area'] = '';
                obj['prioridad'] = '';
                obj['codigo'] = '';

                var opcSi = $('#yes').val();
                var opcNo = $('#no').val();

                $.SmartMessageBox({
                    title: $("#confirmation").val(),
                    content: $("#confirm_c").val(),
                    buttons: '['+opcSi+']['+opcNo+']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                        blockUI(parametros.blockMess);
                        $.ajax(
                            {
                                url: parametros.addUpdateUrl,
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
                                        getRequests();
                                        var msg = $("#succOverride").val();
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
                            title: $("#cancel").val(),
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