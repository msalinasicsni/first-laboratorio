
var consolidatedExams = function () {

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

    var desbloquearUI = function () {
        setTimeout($.unblockUI, 500);
    };

    return {
        //main function to initiate the module
        init: function (parametros) {
            var responsiveHelper_data_result = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };

            var $validator = $('#result_form').validate({
                // Rules for form validation
                rules : {
                    idDx:{
                        required:true
                    },
                    semI:{
                        required:true
                    },
                    semF:{
                        required:true
                    },
                    anio:{
                        required:true
                    }
                },
                // Do not change code below
                errorPlacement : function(error, element) {
                    error.insertAfter(element.parent());
                },
                submitHandler: function (form) {
                }
            });

            function showMessage(title,content,color,icon,timeout){
                $.smallBox({
                    title: title,
                    content: content,
                    color: color,
                    iconSmall: icon,
                    timeout: timeout
                });
            }


            $("#exportExcel").click(function(){
                var $validarForm = $("#result_form").valid();
                if (!$validarForm) {
                    $validator.focusInvalid();
                    return false;
                } else {
                    bloquearUI('');
                    var filtro = {};
                    filtro['anio'] = $('#anio').find('option:selected').val();
                    filtro['semFinal'] = $('#semF').find('option:selected').val();
                    filtro['semInicial'] = $('#semI').find('option:selected').val();
                    filtro['codLabo'] = $('#codigoLab').find('option:selected').val();
                    filtro['consolidarPor'] = $('input[name="rbFechaBusqueda"]:checked', '#result_form').val();
                    var valores = $('#idDx').val();
                    var strValores = '';
                   for (var i = 0; i < valores.length; i++) {
                        if (i == 0)
                            strValores = +valores[i];
                        else
                            strValores = strValores + ',' + valores[i];
                    }
                    filtro['diagnosticos'] = strValores;
                    $(this).attr("href",parametros.excelUrl+"?filtro="+JSON.stringify(filtro));
                    desbloquearUI();
                }
            });
        }
    };

}();
