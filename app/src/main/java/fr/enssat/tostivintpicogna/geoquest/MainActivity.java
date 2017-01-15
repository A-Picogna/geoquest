package fr.enssat.tostivintpicogna.geoquest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button launchButton = (Button) findViewById(R.id.launch_game);


        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //declare our intent object which takes two parameters, the context and the new activity name

                // the name of the receiving activity is declared in the Intent Constructor
                Intent intent = new Intent(getApplicationContext(), MapTracker.class);

                String sendMessage = "hello world";
                //put the text inside the intent and send it to another Activity
                //intent.putExtra("blabla", sendMessage);
                //start the activity
                startActivity(intent);

            }
        });
    }
}
