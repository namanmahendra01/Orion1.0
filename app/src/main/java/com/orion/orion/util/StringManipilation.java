package com.orion.orion.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringManipilation {
    public static String expandUsername(String username){
        return username.replace("."," ");
    }
    public static String condenseUsername(String username){
        return  username.replace(" ",".");
    }
    public static String getTime(String time){
         String time1= time.substring(0,10);
         String time2=time1.substring(8,10)+"-"+time1.substring(5,7)+"-"+time1.substring(0,4);
         return time2;
    }
    public static String getDateAndTime(String time) throws ParseException {
        String date1= time.substring(0,10);
        String time1=time.substring(11,19);

        String s = time1;
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
        Date d = f1.parse(s);
        DateFormat f2 = new SimpleDateFormat("h:mma");
        f2.format(d).toLowerCase(); // "12:18a

        String time3=date1+" "+ f2.format(d).toLowerCase();
        return time3;
    }

    public static  String getTags(String string){
    int counter = 0;
    for( int i=0; i<string.length(); i++ ) {
        if( string.charAt(i) == '#' ) {
            counter++;
        }
    }

    if(counter>0){
            StringBuilder sb = new StringBuilder();
            char[] charArray = string.toCharArray();
            boolean foundword=false;
            for(char c : charArray){
                if(c=='#'){
                    foundword = true;
                    sb.append(c);
                }else{
                    if(foundword){
                        sb.append(c);
                    }
                }
                if(c ==' '){
                    foundword=false;
                }
            }
            String s = sb.toString().replace(" ","").replace("#",",#");
            return  s.substring(1,s.length());

        }
        return  string;
}
}
