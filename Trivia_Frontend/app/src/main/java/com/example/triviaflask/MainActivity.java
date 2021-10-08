package com.example.triviaflask;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity
{

    TextView text_view_question;
    int correct_answer;
    Toast toast_for_player_choice;
    ArrayMap<Integer, String> questions_map = new ArrayMap<>();
    ArrayMap<Integer, Integer> answers_map = new ArrayMap<>();
    int question_number;
    final String url_of_json_data = "http://someUrl/questAndAnswers";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        correct_answer = 0;   //will contain the correct answer which wil be received from the Json
        question_number = 1;

        text_view_question = findViewById(R.id.textQuestion);


        OkHttpClient okHttpClientQuestions = new OkHttpClient();
        Request requestQuestions = new Request.Builder().url(url_of_json_data).build();
        okHttpClientQuestions.newCall(requestQuestions).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                String error = e.getMessage();
                Log.e("err", error);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);

                    Iterator<String> response_keys = jsonObject.keys();
                    String question = "not null";
                    int index_of_question = 1; //the key in the json in order to get question and answer
                    int place_of_question = 0; //the json object has an array, index 0 contains the question
                    int place_of_answer = 1; //index 1 contains the answer

                    while(response_keys.hasNext())
                    {
                        JSONArray jsonArray = (JSONArray) jsonObject.get(String.valueOf(index_of_question));
                        question = (String) jsonArray.get(place_of_question);
                        correct_answer = (int) jsonArray.get(place_of_answer);

                        questions_map.put(index_of_question, question);
                        answers_map.put(index_of_question, correct_answer);
                        index_of_question = index_of_question + 1;

                        response_keys.next();
                    }

                    text_view_question.setText(questions_map.get(question_number));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // on button click the method checks if the user chose the correct answer by comparing the two Integers (one from the JSON the other from the button text)
    public void checkIfUserCorrect(View view)
    {
        Button btUserChoice = (Button) view;

        if(answers_map.get(question_number) != null)
        {
            if (answers_map.get(question_number) == Integer.parseInt(btUserChoice.getText().toString()))
            {
                toast_for_player_choice = Toast.makeText(getApplicationContext(),"Correct Answer!",Toast.LENGTH_SHORT);
                toast_for_player_choice.show();
                nextQuestionReplacement();
            }
            else
                {
                    toast_for_player_choice = Toast.makeText(getApplicationContext(),"Wrong Answer!",Toast.LENGTH_SHORT);
                    toast_for_player_choice.show();

                    //If the user is wrong then he will be sent back to the first screen
                    Intent intent = new Intent(this, first_screen.class);
                    startActivity(intent);
                }
        }
    }

        //This method checks if there are more questions in the map, if so the program will go to the next one
        // otherwise it will remain the same.
    public void nextQuestionReplacement()
    {
        question_number = question_number + 1;

        if(questions_map.get(question_number) != null)
            text_view_question.setText(questions_map.get(question_number));

        if(answers_map.get(question_number) != null)
            correct_answer = answers_map.get(question_number);
    }

}
