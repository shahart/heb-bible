
class No2gim {

    // see also https://github.com/Scimonster/js-gematriya/blob/master/gematriya.js for prk
    no2gim(input) {
        const letters = ["ה'","ד'","ג'","ב'","א'","ת","ש","ר","ק","צ","פ","ע","ס","נ","מ","ל","כ","טז","טו","י","ט","ח","ז","ו","ה","ד","ג","ב","א"];
       const values = [5000,4000,3000,2000,1000,400,300,200,100,90,80,70,60,50,40,30,20,16,15,10,9,8,7,6,5,4,3,2,1];
       let output = "";
       while (input > 0) {
           for (let i = 0; i < letters.length; ++i) {
               if (input >= values[i]) {
                   input -= values[i];
                   output += letters[i];
                   break;
               }
           }
       }
       return output;
    }

}

export { No2gim }
