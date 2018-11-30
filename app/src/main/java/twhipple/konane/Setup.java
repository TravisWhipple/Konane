package twhipple.konane;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by traviswhipple on 3/19/18.
 */

public class Setup extends AppCompatActivity {

    Button startButton;
    Spinner boardSizeSpinner;
    private int boardWidth = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_layout);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Setup.this, Konane.class);
                intent.putExtra("boardWidth", Integer.toString(boardWidth));
                startActivity(intent);

            }
        });



        // Spinner allows user to select from different search algorithms.
        boardSizeSpinner       = findViewById(R.id.boardSizeSpinner);

        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(this, R.array.boardSizes, android.R.layout.simple_spinner_item);

        boardSizeSpinner.setAdapter(arrayAdapter);
        boardSizeSpinner.setBackgroundColor(getResources().getColor(R.color.orange));

        // Set search algorithm based on what is selected.
        boardSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){

                TextView myText = (TextView) view;
                String selected = myText.getText().toString();

                // Check if user selected Depth First Search.
                if(selected.contains("6")){
                    boardWidth = 6;
                }

                // Check if user selected Breadth First Search.
                if(selected.contains("8")){
                    boardWidth = 8;
                }

                // Check if user selected Best First Search.
                if(selected.contains("10")){
                    boardWidth = 10;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}