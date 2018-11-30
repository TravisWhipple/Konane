
package twhipple.konane;

        import android.content.Context;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.text.Layout;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.GridLayout;
        import android.widget.TextView;

        import java.lang.ref.WeakReference;

/******************************************************************
 *
 * Class: B
 */

public class BoardView extends AppCompatActivity {

    private Button tempButton;
    private TextView player;

    private Board board;
    private GridLayout gridLayout;
    private Context context;

    public BoardView(Context a_context, Board a_board, GridLayout a_gridLayout){
        context = a_context;
        board = a_board;
        //gridLayout = new GridLayout(context);
        gridLayout = a_gridLayout;
        setTiles();
    }

    public void setTiles(){
        int x = 0;
        int y = 0;

        for(Cell cell : board.getBoard()){

            tempButton = new Button(context);
            String text = cell.getIdString();

            //GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            //lp.width = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
            //lp.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;


            //tempButton.setLayoutParams(lp);
            tempButton.setText(text);
            tempButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //clicked(view);
                }
            });

            gridLayout.addView(tempButton);
        }
    }


    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.konane);

        board = new Board();



        gridLayout = (GridLayout) findViewById(R.id.gridView);
        player = (TextView) findViewById(R.id.player);

        for(int y = 0; y < 6; y++){
            for(int x = 0; x < 6; x++){
                tempButton = new Button(this);
                String text = Integer.toString(y) + "-" + Integer.toString(x);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
                lp.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT;


                tempButton.setLayoutParams(lp);
                tempButton.setText(text);
                tempButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clicked(view);
                    }
                });

                gridLayout.addView(tempButton);
            }
        }
    }
    */
}
