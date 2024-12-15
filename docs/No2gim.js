
class No2gim {

    // todo super naive --> look-up-table: ת,ש,ר 400,300,200
    // see also https://github.com/Scimonster/js-gematriya/blob/master/gematriya.js for prk
    no2gim(input) {
        let output = "";
        while (input > 0) {
            if (input >= 400) {
                input -= 400;
                output += "ת";
            } else if (input >= 300) {
                input -= 300;
                output += "ש";
            } else if (input >= 200) {
                input -= 200;
                output += "ר";
            } else if (input >= 100) {
                input -= 100;
                output += "ק";
            } else if (input >= 90) {
                input -= 90;
                output += "צ";
            } else if (input >= 80) {
                input -= 80;
                output += "פ";
            } else if (input >= 70) {
                input -= 70;
                output += "ע";
            } else if (input >= 60) {
                input -= 60;
                output += "ס";
            } else if (input >= 50) {
                input -= 50;
                output += "נ";
            } else if (input >= 40) {
                input -= 40;
                output += "מ";
            } else if (input >= 30) {
                input -= 30;
                output += "ל";
            } else if (input >= 20) {
                input -= 20;
                output += "כ";
            } else if (input == 16) {
                input -= 16;
                output += "טז";
            } else if (input == 15) {
                input -= 15;
                output += "טו";
            } else if (input >= 10) {
                input -= 10;
                output += "י";
            } else if (input >= 9) {
                input -= 9;
                output += "ט";
            } else if (input >= 8) {
                input -= 8;
                output += "ח";
            } else if (input >= 7) {
                input -= 7;
                output += "ז";
            } else if (input >= 6) {
                input -= 6;
                output += "ו";
            } else if (input >= 5) {
                input -= 5;
                output += "ה";
            } else if (input >= 4) {
                input -= 4;
                output += "ד";
            } else if (input >= 3) {
                input -= 3;
                output += "ג";
            } else if (input >= 2) {
                input -= 2;
                output += "ב";
            } else if (input >= 1) {
                input -= 1;
                output += "א";
            }
        }
        return output;
    }

}

export { No2gim }
