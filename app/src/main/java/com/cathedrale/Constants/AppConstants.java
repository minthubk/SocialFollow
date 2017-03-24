package com.cathedrale.Constants;

/**
 * Created by Aspire on 12/15/2015.
 */
public class AppConstants {
    public static final String URL ="https://dev-cathedraleye.cs7.force.com/services/apexrest/";
    public static final String DBNAME= "Cathedraleyes";
    public static final String TableName= "TableName";
    public static final String Login_Success= "Login_Success";
    public static final String FBName= "FBName";
    public static final String FBFIRSTName= "FBFIRSTName";
    public static final String FBLASTName= "FBLASTName";
    public static final String TRUE= "TRUE";
    public static final String FALSE= "FALSE";
    public static final String FBId= "FBId";
    public static final String FBEMAIL= "FBEMAIL";
    public static final String NONFB_EMAIL= "NONFB_EMAIL";
    public static final String ContactId= "ContactId";
    public static final String NONFB_NAME= "NONFB_NAME";
    public static final String SetEyepeaceReminderTime= "SetEyepeaceReminderTime";
    public static final String SetHeatmassageReminderTime= "SetHeatmassageReminderTime";
    public static final String SetHeatmassageSnoozeTime= "SetHeatmassageSnoozeTime";
    public static final String SetEyepeaceSnoozeTime= "SetEyepeaceSnoozeTime";
    public static final String SetEyenutritionRemindertime1= "SetEyenutritionRemindertime1";
    public static final String SetEyenutritionRemindertime2= "SetEyenutritionRemindertime2";
    public static final String SetEyedropsReminderTime= "SetEyedropsReminderTime";
    public static final String EyedropsReminder= "Eyedrops Reminder";
    public static final String EyepeaceReminder= "Eyepeace Reminder";
    public static final String HeatmassageReminder= "Heat massage Reminder";
    public static final String EyenutritionReminder= "Eyenutrition Reminder";
    public static final String EyenutritionReminder2= "Eyenutrition Reminder.";
    public static final String EyenutritionMessage= "Please take you Eye Nutrition";
    public static final String EyedropsnMessage= "Please use Dry Eye Treatment";
    public static final String EyepeaceMessage= "Please use the Eyepeace";
    public static final String HeatmassageMessage= "Please take Eye Heat Massage";
    public static final String NONFB_PASSWORD= "NONFB_PASSWORD";
    public static final String CURRENTWEEK= "CURRENTWEEK";
    public static final String CURRENTDATE= "CURRENTDATE";
    public static final String PREVIOUSDATE= "PREVIOUSDATE";
    public static final String FirstTimeOpeneddate= "FirstTimeOpeneddate";
    public static final String COMPLETED_QUESTIONS_FORthisWEEK= "COMPLETED_QUESTIONS_FORthisWEEK";
    public static final String COMPLETED= "COMPLETED";
    public static final String NOTCOMPLETED= "NOTCOMPLETED";
    public static final String DateafterSevenDays= "DateafterSevenDays";
    public static final String SWITCHCASE= "SWITCHCASE";
    public static final String ODDWEEK= "Odd";
    public static final String EVENWEEK= "EVEN";
    public static final String TWITTER_USERNAME= "TWITTER_USERNAME";
    public static final String No_Patioent_found_message= "Sorry, we do not have any patient in our database with specified email address. Please retry with different email. You need to provide the email address you have provided at our clinic.";
    public static final String YOUTUBEVIDEOURL= "https://www.youtube.com/watch?v=G7MiF8UiBX8";
    public static final String YOUTUBECHANNELURL= "https://www.youtube.com/watch?v=G7MiF8UiBX8&list=UL";
    public static final String CATHEDRALWEBSITE= "http://cathedraleye.com/#sthash.wOt55LgM.dpbs";
    public static final String ShowQuestionsCompleted_Message= "You have already submitted questionnaire for this week.";

    public final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public static final String DEVICE_MODEL= "DEVICE_MODEL";
    public static final String DEVICE_TYPE= "DEVICE_TYPE";
    public static final String AndroidVersion= "AndroidVersion";
    public static final String DEVICE_UNIQUE_ID= "DEVICE_UNIQUE_ID";
    public static final String LOGIN_URL =URL+"app/register";
    public static final String ADVERT_URL =URL+"adverts";
    public static final String ANSWERS_URL =URL+"answers";
    public static final String QUESTIONS_URL =URL+"questions";
    public static String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req" , tag_string_req ="string_req";

}
