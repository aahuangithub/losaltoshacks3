package tech.trash.finderssweepers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    Button finders, sweepers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toast.makeText(this, "Thanks for downloading! App is kinda unstable, reopen and it should be fine (wait for map to load).", Toast.LENGTH_LONG).show();
        finders = findViewById(R.id.finders_button);
        finders.setOnClickListener((v)->{
            startActivity(new Intent(getApplicationContext(), Finder.class));
//            Log.d("Login Button Clicked", "Finders pressed");
        });
        sweepers = findViewById(R.id.sweepers_button);
        sweepers.setOnClickListener((view -> {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
//            Log.d("Login Button Clicked", "Sweepers pressed");
        }));
    }
}
