package algonquin.cst2335.khan0498.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import algonquin.cst2335.khan0498.R;
import algonquin.cst2335.khan0498.data.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this).get(MainViewModel.class);
        setContentView(R.layout.activity_main);
        TextView mytext=findViewById(R.id.textview);
        Button btn=findViewById(R.id.mybutton);
        EditText myedit=findViewById(R.id.myedittext);
        Switch myswitch=findViewById(R.id.myswitch);
        RadioButton myradiobtn=findViewById(R.id.myradiobutton);
        CheckBox chkbx=findViewById(R.id.mycheckBox);
        ImageButton imgbtn=findViewById(R.id.myimageButton);
        btn.setOnClickListener(v -> {
            String editString = myedit.getText().toString();
            mytext.setText( "Your edit text has: " + editString);
        });
        myswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            model.isSelected.postValue(isChecked);
        });
        myradiobtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            model.isSelected.postValue(isChecked);
        });
        chkbx.setOnCheckedChangeListener((buttonView, isChecked) -> {
            model.isSelected.postValue(isChecked);
        });

        model.isSelected.observe(this, selected -> {
            myswitch.setChecked(selected);
            myradiobtn.setChecked(selected);
            chkbx.setChecked(selected);
            CharSequence text = "The value is now: isChecked.";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this /* MyActivity */, text, duration);
            toast.show();
        });
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int width = imgbtn.getWidth();
                int height = imgbtn.getHeight();
                String toastMessage = "The width = " + width + " and height = " + height;
                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }
}