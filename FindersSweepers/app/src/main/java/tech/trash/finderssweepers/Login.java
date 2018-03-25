package tech.trash.finderssweepers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

public class Login extends AppCompatActivity {
    Button finders, sweepers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        finders.setOnClickListener((v)->{
            Log.d("Login Button Clicked", "Finders pressed");
        });
        sweepers.setOnClickListener((view -> {
            Log.d("Login Button Clicked", "Sweepers pressed");
        }));
    }
}
