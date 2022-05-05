package com.example.aninterface.Late;

import android.app.Activity;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMS extends Activity{

    public SMS(Activity activity,String phone,String late){

        if(Integer.parseInt(late)==0){
            return;
        }
        try {
            SmsManager sms_manager = SmsManager.getDefault();
            sms_manager.sendTextMessage(phone, null,caseMessage(Integer.parseInt(late)), null, null);
        } catch (Exception e) {
            Toast.makeText(activity, "יש שגיאה בשליחה, נסה שוב", Toast.LENGTH_SHORT).show();
        }

    }
    private String caseMessage(int late){
        if(late>5){
            return ("לא קמת לתפילה \n לכן הפלאפון יישאר עוד שבוע מהיום");
        }
        switch (late%5){

            case 1:
                return  "לא היית היום בתפילה פעם ראשונה";
            case 2:
                return  "לא היית היום בתפילה פעם שניה";
            case 3:
                return  "לא היית היום בתפילה פעם שלישית";
            case 4:
                return  "לא היית היום בתפילה פעם רביעית";
            case 0:
                return "יש להגיש לי את המכשיר למשך שבוע";
        }

        return null;
    }

}
