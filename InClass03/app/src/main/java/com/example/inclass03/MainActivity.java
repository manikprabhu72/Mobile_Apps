/*
InClass03
Group5
Manik Prabhu Cheekoti - 801150452
Akhil Reddy Yakkaluri - 801148613
 */

package com.example.inclass03;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE = 0x001;
    ImageView iv_avatar;
    Button bt_save;
    String genderMain = "gender";
    public static final String USER_KEY = "user";
    EditText et_firstName;
    EditText et_lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("My Profile");
        iv_avatar = findViewById(R.id.iv_avatar);
        et_firstName = findViewById(R.id.et_firstName);
        et_lastName = findViewById(R.id.et_lastName);
        bt_save = findViewById(R.id.bt_edit);
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSelectAvatar = new Intent(MainActivity.this,SelectAvatar.class);
                startActivityForResult(toSelectAvatar,REQ_CODE);
            }
        });
        bt_save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String firstName = et_firstName.getText().toString();
                String lastName = et_lastName.getText().toString();
                if(firstName.isEmpty() || lastName.isEmpty() || genderMain.equals("gender")){
                    if(firstName.isEmpty()){
                        Toast.makeText(MainActivity.this,"FirstName cant be Empty",Toast.LENGTH_SHORT).show();
                    }else if(lastName.isEmpty()){
                        Toast.makeText(MainActivity.this,"LastName cant be Empty",Toast.LENGTH_SHORT).show();
                    }else if(genderMain.equals("gender")){
                        Toast.makeText(MainActivity.this,"Please select an avatar",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    User user = new User(firstName,lastName,genderMain);
                    Intent toDisplayProfile = new Intent(MainActivity.this,DisplayProfile.class);
                    toDisplayProfile.putExtra(USER_KEY,user);
                    startActivity(toDisplayProfile);
                    finish();
                }

            }
        });
        if(getIntent() !=null && getIntent().getExtras() != null) {
            User user = (User) getIntent().getExtras().getSerializable(MainActivity.USER_KEY);
            et_firstName.setText(user.getFirstName());
            et_lastName.setText(user.getLastName());
            if(user.getGender().equals("Male")){
                iv_avatar.setImageDrawable(getDrawable(R.drawable.male));
            }else{
                iv_avatar.setImageDrawable(getDrawable(R.drawable.female));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQ_CODE && resultCode == RESULT_OK && data != null){
            String gender = data.getExtras().getString(SelectAvatar.GENDER);

            if (gender.equals("female")) {
                genderMain = "Female";
                iv_avatar.setImageDrawable(getDrawable(R.drawable.female));
            }else{
                genderMain = "Male";
                iv_avatar.setImageDrawable(getDrawable(R.drawable.male));
            }
        }
    }
}
