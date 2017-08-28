/**
 * Created by souyen-ics on 07-21-15.
 */
var OrganizationChart = function () {

    return {
        init: function (parametros) {
            getLabs();

            function blockUI() {
                var loc = window.location;
                var pathName = loc.pathname.substring(0, loc.pathname.indexOf('/', 1) + 1);
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
                tablet: 1024,
                phone: 480
            };
            var table1 = $('#labs-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,

                "columns": [
                    null, null,
                    {
                        "className": 'detailLab',
                        "orderable": false
                    }
                ],

                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#labs-records'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback: function () {
                    $('.detailLab')
                        .off("click", detailHandler)
                        .on("click", detailHandler);


                }
            });

            var table2 = $('#management-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,

                "columns": [
                    null,
                    {
                        "className": 'detailD',
                        "orderable": false
                    },
                    {
                        "className": 'overrideM',
                        "orderable": false
                    }
                ],

                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#management-records'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback: function () {
                    $('.detailD')
                        .off("click", detailDHandler)
                        .on("click", detailDHandler);
                    $('.overrideM')
                        .off("click", overrideHandler)
                        .on("click", overrideHandler);


                }
            });

            var table3 = $('#depart-manag-lab').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,

                "columns": [
                    null,
                    {
                        "className": 'detailA',
                        "orderable": false
                    },
                    {
                        "className": 'overrideD',
                        "orderable": false
                    }
                ],

                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#depart-manag-lab'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback: function () {
                    $('.detailA')
                        .off("click", detailAHandler)
                        .on("click", detailAHandler);

                    $('.overrideD')
                        .off("click", overrideDepHandler)
                        .on("click", overrideDepHandler);


                }
            });

            var table4 = $('#areas-records').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>" +
                    "t" +
                    "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth": true,

                "columns": [
                    null,

                    {
                        "className": 'overrideA',
                        "orderable": false
                    }
                ],

                "preDrawCallback": function () {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#areas-records'), breakpointDefinition);
                    }
                },
                "rowCallback": function (nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback": function (oSettings) {
                    responsiveHelper_dt_basic.respond();
                },


                fnDrawCallback : function() {

                    $('.overrideA')
                        .off("click", overrideAHandler)
                        .on("click", overrideAHandler);

                }
            });


            function detailHandler() {
                var data = $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var id = detalle[0];
                    var name = detalle[1];
                    $('#managementName').html(name);
                    $('#div1').hide();
                    getManagement(id);
                    $('#idLab').val(id);
                    $('#div2').fadeIn('slow');
                    $('#dBack1').show();
                }
            }

            function detailDHandler() {
                var data = $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var id = detalle[0];
                    var name = detalle[1];
                    var name2 = detalle[2];
                    $('#lab').html(name2);
                    $('#managementLab').html(name);
                    $('#div1').hide();
                    $('#div2').hide();
                    $('#idManagLab').val(id);
                    $('#div3').fadeIn('slow');
                    $('#dBack2').show();
                    $('#dBack1').hide();
                    getDepartments(id);
                }
            }

            function detailAHandler() {
                var data = $(this.innerHTML).data('id');
                if (data != null) {
                    var detalle = data.split(",");
                    var id = detalle[0];
                    var dep = detalle[1];
                    var dir = detalle[2];
                    var lab = detalle[3];
                    $('#labo').html(lab);
                    $('#dir').html(dir);
                    $('#dep').html(dep);
                    $('#div1').hide();
                    $('#div2').hide();
                    $('#div3').hide();
                    $('#idDepManag').val(id);
                    $('#div4').fadeIn('slow');
                    $('#dBack3').show();
                    $('#dBack1').hide();
                    $('#dBack2').hide();
                    getAreas(id);
                }
            }


            function getLabs() {
                $.getJSON(parametros.labsUrl, {
                    ajax: 'true'
                }, function (data) {
                    table1.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var btnAdd = '<button type="button" title="Direcciones asociadas" class="btn btn-primary btn-xs" data-id="' + data[i].codigo + "," + data[i].nombre +
                            '" > <i class="fa fa-list"></i>';

                        table1.fnAddData(
                            [data[i].codigo, data[i].nombre, btnAdd]);
                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            $('#btnBack1').click(function () {
                $('#div1').show();
                $('#div2').hide();
                $('#div4').hide();
                $('#managementName').html('');
                $('#idLab').val('');
                $('#lab').html('');
            });

            $('#btnBack2').click(function () {
                $('#div2').show();
                $('#div3').hide();
                $('#div4').hide();
                $('#managementLab').html('');
                $('#idManagLab').val('');
                $('#dBack1').show();
            });

            $('#btnBack3').click(function () {
                $('#div3').show();
                $('#div4').hide();
                $('#div2').hide();
                $('#labo').html('');
                $('#dir').html('');
                $('#dep').html('');
                $('#idDepManag').val('');
                $('#dBack2').show();
                $('#dBack3').hide();

            });


            function overrideHandler() {
                var data = $(this.innerHTML).data('id');

                if (data != null) {
                    overrideMLab(data);
                }
            }

            function overrideDepHandler() {
                var data = $(this.innerHTML).data('id');

                if (data != null) {
                    overrideDepartment(data);
                }
            }


            function overrideAHandler() {
                var data = $(this.innerHTML).data('id');

                if (data != null) {
                    overrideArea(data);
                }
            }

            function showModal1() {
                $("#myModal1").modal({
                    show: true
                });
            }

            function showModal2() {
                $("#myModal2").modal({
                    show: true
                });
            }

            function showModal3() {
                $("#myModal3").modal({
                    show: true
                });
            }


            function getManagement(lab) {
                $.getJSON(parametros.managementUrl, {
                    ajax: 'true',
                    lab: lab
                }, function (data) {
                    table2.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var btnDetail = '<button type="button" title="Dep. Asociados" class="btn btn-primary btn-xs" data-id="' + data[i].idDireccionLab + "," + data[i].direccion.nombre + "," + data[i].laboratorio.nombre +
                            '" > <i class="fa fa-list"></i>';

                        var btnOverride = '<button type="button" title="Anular" class="btn btn-danger btn-xs" data-id="' + data[i].idDireccionLab +
                            '" > <i class="fa fa-times"></i>';
                        table2.fnAddData(
                            [data[i].direccion.nombre, btnDetail, btnOverride]);
                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            $('#btnAddManagement').click(function () {
                showModal1();
            });

            $('#btnAddDepartment').click(function () {
                showModal2();
            });

            $('#btnAddArea').click(function () {
                showModal3();
            });


            <!-- Validacion formulario 1 -->
            var $validator = $('#ass-managment-form').validate({
                // Rules for form validation
                rules: {
                    management: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnSave1').click(function () {
                var $validarModal = $('#ass-managment-form').valid();
                if (!$validarModal) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    addManagment();
                }
            });

            function addManagment() {
                var obj = {};
                obj['mensaje'] = '';
                obj['idLab'] = $('#idLab').val();
                obj['management'] = $('#management').val();
                obj['idRecord'] = '';

                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveManagmentUrl,
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
                                getManagement($('#idLab').val());
                                var msg = $("#msjSuccManagment").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $('#management').val('').change();
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }


            function overrideMLab(idRecord) {
                var obj = {};
                obj['mensaje'] = '';
                obj['idLab'] = '';
                obj['managment'] = '';
                obj['idRecord'] = idRecord;

                var opcSi = $("#yes").val();
                var opcNo = $("#no").val();

                $.SmartMessageBox({
                    title: $("#confirmation").val(),
                    content: $("#confirm_content").val(),
                    buttons: '[' + opcSi + '][' + opcNo + ']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                        blockUI(parametros.blockMess);
                        $.ajax(
                            {
                                url: parametros.saveManagmentUrl,
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
                                        getManagement($('#idLab').val());
                                        var msg = $("#succesfulOverride").val();
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
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#override_cancel").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                            color: "#C46A69",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 4000
                        });
                    }
                });
            }

            function getDepartments(idManagementLab) {
                $.getJSON(parametros.departmentsUrl, {
                    ajax: 'true',
                    idManagementLab: idManagementLab
                }, function (data) {
                    table3.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var btnDetail = '<button type="button" title="Areas asociadas" class="btn btn-primary btn-xs" data-id="' + data[i].idDepartDireccion + "," + data[i].departamento.nombre + ","+ data[i].direccionLab.direccion.nombre + "," + data[i].direccionLab. laboratorio.nombre +
                            '" > <i class="fa fa-list"></i>';


                        var btnOverride = '<button type="button" title="Anular" class="btn btn-danger btn-xs" data-id="' + data[i].idDepartDireccion +
                            '" > <i class="fa fa-times"></i>';
                        table3.fnAddData(
                            [data[i].departamento.nombre, btnDetail, btnOverride]);

                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            <!-- Validacion formulario 2 -->
            var $validator1 = $('#department-form').validate({
                // Rules for form validation
                rules: {
                    department: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnSave2').click(function () {
                var $validarModal = $('#department-form').valid();
                if (!$validarModal) {
                    $validator1.focusInvalid();
                    return false;
                } else {
                    addDepartment();
                }
            });

            <!-- Validacion formulario 3 -->
            var $validator2 = $('#areas-form').validate({
                // Rules for form validation
                rules: {
                    area: {required: true}
                },
                // Do not change code below
                errorPlacement: function (error, element) {
                    error.insertAfter(element.parent());
                }
            });

            $('#btnSave3').click(function () {
                var $validarModal = $('#areas-form').valid();
                if (!$validarModal) {
                    $validator2.focusInvalid();
                    return false;
                } else {
                    addArea();
                }
            });

            function addDepartment() {
                var id = $('#idManagLab').val();
                var obj = {};
                obj['mensaje'] = '';
                obj['idManagLab'] = id;
                obj['department'] = $('#department').val();
                obj['idRecord'] = '';

                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveDepartmentUrl,
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
                                getDepartments(id);
                                var msg = $("#msjSuccDep").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $('#department').val('').change();
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideDepartment(idRecord) {
                var obj = {};
                obj['mensaje'] = '';
                obj['idManagLab'] = '';
                obj['department'] = '';
                obj['idRecord'] = idRecord;

                var opcSi = $("#yes").val();
                var opcNo = $("#no").val();

                $.SmartMessageBox({
                    title: $("#confirmation").val(),
                    content: $("#confirm_content_dep").val(),
                    buttons: '[' + opcSi + '][' + opcNo + ']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                        blockUI(parametros.blockMess);
                        $.ajax(
                            {
                                url: parametros.saveDepartmentUrl,
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
                                        getDepartments($('#idManagLab').val());
                                        var msg = $("#succesfulDepOverride").val();
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
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#depOverride_cancel").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
                            color: "#C46A69",
                            iconSmall: "fa fa-times fa-2x fadeInRight animated",
                            timeout: 4000
                        });
                    }
                });
            }

            function getAreas(id) {
                $.getJSON(parametros.areasUrl, {
                    ajax: 'true',
                    id: id
                }, function (data) {
                    table4.fnClearTable();
                    var len = data.length;
                    for (var i = 0; i < len; i++) {

                        var btnOverride = '<button type="button" title="Anular" class="btn btn-danger btn-xs" data-id="' + data[i].idAreaDepartamento +
                            '" > <i class="fa fa-times"></i>';
                        table4.fnAddData(
                            [data[i].area.nombre, btnOverride]);

                    }
                }).fail(function(jqXHR) {
                    unBlockUI();
                    validateLogin(jqXHR);
                });
            }

            function addArea() {
                var id = $('#idDepManag').val();
                var obj = {};
                obj['mensaje'] = '';
                obj['idDepManag'] = id;
                obj['area'] = $('#area').val();
                obj['idRecord'] = '';

                blockUI(parametros.blockMess);
                $.ajax(
                    {
                        url: parametros.saveAreaUrl,
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
                                getAreas(id);
                                var msg = $("#msjSuccArea").val();
                                $.smallBox({
                                    title: msg,
                                    content: $("#disappear").val(),
                                    color: "#739E73",
                                    iconSmall: "fa fa-success",
                                    timeout: 4000
                                });
                                $('#area').val('').change();
                            }
                            unBlockUI();
                        },
                        error: function (jqXHR) {
                            unBlockUI();
                            validateLogin(jqXHR);
                        }
                    });
            }

            function overrideArea(idRecord) {
                var obj = {};
                obj['mensaje'] = '';
                obj['idDepManag'] ='';
                obj['area'] = '';
                obj['idRecord'] = idRecord;

                var opcSi = $("#yes").val();
                var opcNo = $("#no").val();

                $.SmartMessageBox({
                    title: $("#confirmation").val(),
                    content: $("#confirm_content_area").val(),
                    buttons: '[' + opcSi + '][' + opcNo + ']'
                }, function (ButtonPressed) {
                    if (ButtonPressed === opcSi) {

                        blockUI(parametros.blockMess);
                        $.ajax(
                            {
                                url: parametros.saveAreaUrl,
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
                                        getAreas($('#idDepManag').val());
                                        var msg = $("#succesfulAreaOverride").val();
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
                    if (ButtonPressed === opcNo) {
                        $.smallBox({
                            title: $("#areaOverride_cancel").val(),
                            content: "<i class='fa fa-clock-o'></i> <i>" + $("#disappear").val() + "</i>",
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