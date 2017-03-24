package com.cathedrale.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cathedrale.Constants.AppConstants;
import com.cathedrale.Constants.Model;
import com.cathedrale.Constants.OptionsScoremodel;
import com.cathedrale.Constants.Optionsmodel;
import com.cathedrale.Graph.BarGraphActivity;
import com.cathedrale.R;
import com.cathedrale.Utils.NetworkUtils;
import com.cathedrale.Utils.SharedPref;
import com.cathedrale.cview.TypefaceTextview;
import com.cathedrale.database.Database;
import com.cathedrale.webservices.HttpGetHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class QuestioneriesNewActivity extends Activity implements AdapterView.OnItemClickListener {

    Database db;
    private SharedPref sharedPref;
    ListView questions_list;
    ArrayList<Model> modelList = new ArrayList<Model>();
    ArrayList<OptionsScoremodel> OptionsScoreList = new ArrayList<OptionsScoremodel>();
    int position = 0;
    AdapterItem questionslistadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_questioneries_new);

        initialize();
    }

    private void initialize() {
        db = new Database(this);
        db.open();
        sharedPref = new SharedPref(this);
        questions_list = (ListView) findViewById(R.id.questionslist);
        getQuestionsfromserver();
        questions_list.setOnItemClickListener(this);
    }

    private void getQuestionsfromserver() {
//        getQuestionsfromServer(sharedPref.getString(AppConstants.CURRENTWEEK));
        if (sharedPref.getString(AppConstants.COMPLETED_QUESTIONS_FORthisWEEK).equals(AppConstants.COMPLETED)) {
            showMessage(AppConstants.ShowQuestionsCompleted_Message);
        } else if (sharedPref.getString(AppConstants.COMPLETED_QUESTIONS_FORthisWEEK).equals(AppConstants.NOTCOMPLETED)) {
            getQuestionsfromServer(sharedPref.getString(AppConstants.CURRENTWEEK));
        }
    }

    private void getQuestionsfromServer(String Week) {
        if (Week.equals(AppConstants.ODDWEEK)) {
            Week = "oddweek";
        } else if (Week.equals(AppConstants.EVENWEEK)) {
            Week = "evenweek";
        }
        String url = AppConstants.QUESTIONS_URL + "/" + Week;
        String post = url.replaceAll(" ", "%20");
        URI sourceUrl = null;
        try {
            sourceUrl = new URI(post);
//            Log.e("@@@@@@", sourceUrl.toString());
            getquestions(sourceUrl.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String Message) {
        TypefaceTextview textMessage;
        textMessage = (TypefaceTextview) findViewById(R.id.textMessage);
        findViewById(R.id.questionslist).setVisibility(View.GONE);
        textMessage.setVisibility(View.VISIBLE);
        textMessage.setText(Message);
    }

    private void getquestions(final String url) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        new AsyncTask<Void, Void, Void>() {
            String responce = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                responce = new HttpGetHandler().jsonUrl(url);
//                Log.e("response", responce);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (responce != null) {
                    try {
                        JSONArray jsonarray = new JSONArray(responce);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject obj = jsonarray.getJSONObject(i);
                            String id = obj.getString("Id");
                            String Order = obj.getString("Order__c");
                            String Questions = obj.getString("Questions__c");
                            String Week = obj.getString("Week__c");
                            String Type__c = obj.getString("Type__c");

                            JSONObject questionnaire_answers = obj.getJSONObject("Questionnaire_Answers__r");
                            int totalSize = questionnaire_answers.getInt("totalSize");
                            String done = questionnaire_answers.getString("done");
                            String records = questionnaire_answers.getString("records");

                            JSONArray answersarray = new JSONArray(records);
                            String Answer__c = null, Score__c = null;

                            ArrayList<Optionsmodel> OptionsList = new ArrayList<Optionsmodel>();
                            for (int j = 0; j < answersarray.length(); j++) {
                                JSONObject answersobj = answersarray.getJSONObject(j);
                                String Question__c = answersobj.getString("Question__c");
                                String Id = answersobj.getString("Id");
                                Answer__c = answersobj.getString("Answer__c");
                                Score__c = answersobj.getString("Score__c");
                                String Color__c = answersobj.getString("Color__c");
                                Optionsmodel optionsList = new Optionsmodel(Question__c, Id, Answer__c, Score__c, Color__c);
                                OptionsList.add(optionsList);
                            }
                            Model models = new Model(id, Order, Questions, Week, Type__c);
                            models.setOptionsmodelList(OptionsList);
                            modelList.add(models);
                        }

                        if (modelList.size() > 0) {
                            String FirstTimeOpeneddate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
                            sharedPref.putString(AppConstants.FirstTimeOpeneddate, FirstTimeOpeneddate);
                            sharedPref.putString(AppConstants.PREVIOUSDATE, FirstTimeOpeneddate);
                            getquestionsbyposition(position);
                        } else {
                            showMessage("No Questions Available for this Week");
                        }

                    } catch (Exception e) {
                        showMessage("Something went wrong " + "\n" + "please try again after some time.....");
                    }
                }

            }
        }.execute();
    }

    private void Nextquestionwithposition() {
//        Log.e("position", "" + position);
        if (modelList.get(position).getAnswer() != null) {
            if (position >= modelList.size() - 1) {
//                findViewById(R.id.backbutton).setVisibility(View.GONE);
                showAlertDialog(QuestioneriesNewActivity.this, "questionnaire", "questionnaire Completed ");
            } else {
                position = position + 1;
                if (position == 0) {
                    findViewById(R.id.backbutton).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.backbutton).setVisibility(View.VISIBLE);
                    findViewById(R.id.backbutton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            Log.e("positionsclick ", "" + position);
                            if (position == 1) {
                                findViewById(R.id.backbutton).setVisibility(View.GONE);
                                position = position - 1;
                                getquestionsbyposition(position);
                            } else {
                                findViewById(R.id.backbutton).setVisibility(View.VISIBLE);
                                position = position - 1;
                                getquestionsbyposition(position);
                            }
                        }
                    });
                }
//                Log.e("positions ", "" + position);
                getquestionsbyposition(position);
            }
        } else {
            Toast.makeText(QuestioneriesNewActivity.this, "answer", Toast.LENGTH_LONG).show();
        }
    }

    private void getquestionsbyposition(int position) {
        TypefaceTextview questiontext;
        questiontext = (TypefaceTextview) findViewById(R.id.questiontext);
        if (modelList.size() > 0) {
            final Model models = modelList.get(position);
            questiontext.setText(models.getQuestions());

            OptionsScoreList.clear();

            for (int i = 0; i < models.getOptionsmodelList().size(); i++) {
                String answers = models.getOptionsmodelList().get(i).getAnswers();
                String score = models.getOptionsmodelList().get(i).getScore();
                String color = models.getOptionsmodelList().get(i).getColor();
                String questionid = models.getId();

                OptionsScoremodel optionsList = new OptionsScoremodel(answers, score, color, questionid);
                OptionsScoreList.add(optionsList);
            }

            Collections.sort(OptionsScoreList, new Comparator() {
                @Override
                public int compare(Object obj1, Object obj2) {
                    OptionsScoremodel score1 = (OptionsScoremodel) obj1;
                    OptionsScoremodel score2 = (OptionsScoremodel) obj2;
                    return score1.getScore().compareToIgnoreCase(score2.getScore());
                }
            });
            questionslistadapter = new AdapterItem(QuestioneriesNewActivity.this, R.layout.customlayoutquestions, OptionsScoreList);
            questions_list.setAdapter(questionslistadapter);
        }
    }

    private void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setCancelable(true);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finalbuttonclick();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

        view.setBackgroundColor(Color.BLACK);
        OptionsScoremodel optionsScoremodel = OptionsScoreList.get(pos);
        String selectedanswer = optionsScoremodel.getAnswers();
        String answerscore = optionsScoremodel.getScore();
        String questionid = optionsScoremodel.getQuestionid();

        modelList.get(position).setAnswer(selectedanswer);
        modelList.get(position).setScore(answerscore);
        modelList.get(position).setSeletedPostion(pos);

        Nextquestionwithposition();
    }

    private class AdapterItem extends ArrayAdapter<OptionsScoremodel> {

        private ArrayList<OptionsScoremodel> optionsList;

        public AdapterItem(QuestioneriesNewActivity context, int textViewResourceId, ArrayList<OptionsScoremodel> optionsList) {
            super(context, textViewResourceId, optionsList);
            this.optionsList = optionsList;
        }

        private class ViewHolder {
            TypefaceTextview optionstext;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.customlayoutquestions, null);
                holder = new ViewHolder();

                holder.optionstext = (TypefaceTextview) convertView.findViewById(R.id.options);
                final OptionsScoremodel models = optionsList.get(position);
                holder.optionstext.setText(models.getAnswers());
                convertView.setBackgroundColor(Color.parseColor(models.getColor()));

            }
            return convertView;
        }
    }

    private void finalbuttonclick() {

        String jsonfinalStr = null;
        String DatabasejsonStr = null;
        int symptomsum = 0;
        int treatmentsum = 0;
        int totalsum = 0;

        ArrayList<String> arrayList = new ArrayList<String>();
        ArrayList<String> DatabasearrayList = new ArrayList<String>();
        for (int i = 0; i < modelList.size(); i++) {
            Model answerstate = modelList.get(i);
            totalsum = totalsum + Integer.parseInt(String.valueOf(answerstate.getScore()));
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("questionId", answerstate.getId());
                jsonObj.put("asnwer", answerstate.getAnswer());
                arrayList.add(jsonObj.toString());
//                Log.e("json", arrayList.toString());

                JSONObject DatabasejsonObj = new JSONObject();
                DatabasejsonObj.put("questionId", answerstate.getId());
                DatabasejsonObj.put("questions", answerstate.getQuestions());
                DatabasejsonObj.put("asnwer", answerstate.getAnswer());
                DatabasearrayList.add(DatabasejsonObj.toString());
//                Log.e("Databasejson", DatabasejsonObj.toString());

            } catch (Exception e) {
//                    Log.e("error ", e.getMessage());
            }
        }

        for (int i = 0; i < modelList.size(); i++) {
            Model answerstate = modelList.get(i);
//            Log.e("getType", answerstate.getType().trim());
            if (answerstate.getType().trim().equals("Symptom")) {
                symptomsum = symptomsum + Integer.parseInt(String.valueOf(answerstate.getScore()));
            } else if (answerstate.getType().trim().equals("Treatment")) {
                treatmentsum = treatmentsum + Integer.parseInt(String.valueOf(answerstate.getScore()));
            }
        }

        db.insertSypmtomScore(symptomsum, sharedPref.getString(AppConstants.CURRENTWEEK));
        db.insertTreatmentScore(treatmentsum, sharedPref.getString(AppConstants.CURRENTWEEK));
        db.insertTotalScore(totalsum, sharedPref.getString(AppConstants.CURRENTWEEK));

        if (arrayList.size() == modelList.size()) {
            try {
                JSONArray jsonArray = new JSONArray(arrayList.toString());
                JSONObject finalObj = new JSONObject();
                if (sharedPref.getString(AppConstants.ContactId) != null) {
                    finalObj.put("ContactId", sharedPref.getString(AppConstants.ContactId));
                }
                finalObj.put("answers", jsonArray);
                jsonfinalStr = finalObj.toString();
//                Log.e("jsonStrs", jsonfinalStr);

                JSONArray DatabasejsonArray = new JSONArray(DatabasearrayList.toString());
                DatabasejsonStr = DatabasejsonArray.toString();
//                Log.e("DatabasejsonStr", DatabasejsonStr);

                sendanswerstoserver(jsonfinalStr, DatabasejsonStr,totalsum);

            } catch (Exception e) {
//                Log.e("jsonStrs", e.getMessage());
            }
        }
    }

    private void sendanswerstoserver(String jsonfinalStr, String DatabasejsonStr,int totalsum) {
        String url = AppConstants.ANSWERS_URL;
        postanswers(url, jsonfinalStr, DatabasejsonStr,totalsum);
    }

    private void postanswers(final String url, final String jsonfinalStr, final String DatabasejsonStr, final int totalsum) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        new AsyncTask<Void, Void, String>() {
            String responce = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("Answers", jsonfinalStr);

                try {
                    responce = NetworkUtils.makePOSTRequest(paramMap, url, QuestioneriesNewActivity.this);
                    Log.e("responseString", responce);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return responce;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (responce != null) {
                    if(responce.trim().equals("SUCCESS")){
                        db.insertWeeklyAnswers(DatabasejsonStr, sharedPref.getString(AppConstants.CURRENTWEEK));
                        sharedPref.putString(AppConstants.COMPLETED_QUESTIONS_FORthisWEEK, AppConstants.COMPLETED);
                        Toast.makeText(QuestioneriesNewActivity.this, "Your total score is " + totalsum + " for this week", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(QuestioneriesNewActivity.this, BarGraphActivity.class));
                        finish();
                    }else{
                        Toast.makeText(QuestioneriesNewActivity.this,"Something went wrong, Please try again.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }.execute();
    }
}
