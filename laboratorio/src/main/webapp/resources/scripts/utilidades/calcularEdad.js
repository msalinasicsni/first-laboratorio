var getAge = function(dateString) {
    var now = new Date(), yearNow = now.getYear(), monthNow = now.getMonth(), dateNow = now.getDate();
    var dob = new Date(dateString.substring(6,10),
            dateString.substring(3,5)-1,
        dateString.substring(0,2)
    );
    yearDob = dob.getYear(),monthDob = dob.getMonth(),dateDob = dob.getDate(),age = {},ageString = "",yearString = "",monthString = "",dayString = "",monthAge = 0, dateAge = 0, yearAge = 0;
    yearAge = yearNow - yearDob;
    if (monthNow >= monthDob)
        monthAge = monthNow - monthDob;
    else {
        yearAge--;
        monthAge = 12 + monthNow -monthDob;
    }
    if (dateNow >= dateDob)
        dateAge = dateNow - dateDob;
    else {
        monthAge--;
        dateAge = 31 + dateNow - dateDob;
        if (monthAge < 0) {
            monthAge = 11;
            yearAge--;
        }
    }
    age = {years: yearAge, months: monthAge, days: dateAge};
    if ( age.years > 1 ) yearString = " años"; else yearString = " año";
    if ( age.months> 1 ) monthString = " meses"; else monthString = " mes";
    if ( age.days > 1 ) dayString = " días"; else dayString = " día";
    if ( (age.years > 0) && (age.months > 0) && (age.days > 0) ) ageString = age.years + yearString + ", " + age.months + monthString + ", " + age.days + dayString;
    else if ( (age.years == 0) && (age.months == 0) && (age.days >= 0) ) ageString = age.days + dayString;
    else if ( (age.years > 0) && (age.months == 0) && (age.days == 0) ) ageString = age.years + yearString;
    else if ( (age.years > 0) && (age.months > 0) && (age.days == 0) ) ageString = age.years + yearString + ", " + age.months + monthString;
    else if ( (age.years == 0) && (age.months > 0) && (age.days > 0) ) ageString = age.months + monthString + ", " + age.days + dayString;
    else if ( (age.years > 0) && (age.months == 0) && (age.days > 0) ) ageString = age.years + yearString + ", " + age.days + dayString;
    else if ( (age.years == 0) && (age.months > 0) && (age.days == 0) ) ageString = age.months + monthString;
    else ageString = "Edad inválida!";
    return ageString;
};
