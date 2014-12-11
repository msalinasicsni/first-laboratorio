var handleDatePickers = function (idioma) {
    if (jQuery().datepicker) {
        //para el resto de datetimepicker por defecto
        $('.date-picker').datepicker({
            language: idioma,
            format:'dd/mm/yyyy',
            autoclose: true
        });
        $('body').removeClass("modal-open"); // fix bug when inline picker is used in modal
        // Para trabajar con rangos de fecha
        //Fecha Inicio Rango
        $('.from_date').datepicker({
            language: idioma,
            format:'dd/mm/yyyy',
            autoclose: true
        })
            .on('changeDate', function (selected) {
                //console.log('change fecha inicio');
                startDate = new Date(selected.date.valueOf());
                startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
                //si la fecha de inicio es mayor a la fecha de fin, entoces la fecha de fin se etsablece igual a la fecha de inicio
                startDateCompare=new Date(selected.date.getFullYear(),selected.date.getMonth(),selected.date.getDate(),0,0,0);
                endDateCompare=$('.to_date').datepicker("getDate");
                if(endDateCompare!=null&&endDateCompare!='undefined'){
                    if (startDateCompare > endDateCompare){
                        //console.log('fec inicio es mayor - from_date');
                        $('.to_date').datepicker('update', startDateCompare);
                    }
                }
                //se actualiza la fecha en que inicia el datetimepicker
                $('.to_date').datepicker('setStartDate', startDate);
            });

        //Fecha Fin Rango
        $('.to_date')
            .datepicker({
                language: idioma,
                format:'dd/mm/yyyy',
                autoclose: true
            })
            .on('changeDate', function (selected) {
                //console.log('change fecha fin');
                FromEndDate = new Date(selected.date.valueOf());
                FromEndDate.setDate(FromEndDate.getDate(new Date(selected.date.valueOf())));
                //si la fecha de inicio es mayor a la fecha de fin, entoces la fecha de inicio se etsablece igual a la fecha de fin
                endDateCompare=new Date(selected.date.getFullYear(),selected.date.getMonth(),selected.date.getDate(),0,0,0);
                startDateCompare=$('.from_date').datepicker("getDate");
                if(endDateCompare!=null&&endDateCompare!='undefined'){
                    if (startDateCompare > endDateCompare){
                        //console.log('fec fin es menor - to_date');
                        $('.from_date').datepicker('update', endDateCompare);
                    }
                }
                //se actualiza la fecha en que inicia el datetimepicker
                $('.from_date').datepicker('setEndDate', FromEndDate);
            });
    }
};
