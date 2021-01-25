package com.heshmat.mydietwatcher.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.heshmat.mydietwatcher.DatabaseInstance;
import com.heshmat.mydietwatcher.FormattingDate;
import com.heshmat.mydietwatcher.R;
import com.heshmat.mydietwatcher.StaticFields;
import com.heshmat.mydietwatcher.adapters.MySpinnerAdapter;

import java.util.Calendar;
import java.util.Date;

import static com.heshmat.mydietwatcher.StaticFields.FEMALE;
import static com.heshmat.mydietwatcher.StaticFields.MALE;
import static com.heshmat.mydietwatcher.models.User.currentUser;

public class InfoFormActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    @BindView(R.id.weightInL)
    TextInputLayout weightInL;
    @BindView(R.id.heightInL)
    TextInputLayout heightInL;
    @BindView(R.id.ageInL)
    TextInputLayout ageInL;
    @BindView(R.id.activeRateSpinner)
    Spinner activeRateSpinner;
    @BindView(R.id.gender_radio_group)
    RadioGroup genderRadioGroup;
    Context context;
    Calendar ageCalender;
    DatePickerDialog picker;
    long birthdate = -1;
    String gender = "";
    MySpinnerAdapter mySpinnerAdapter;
    String activeRateStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_form);
        ButterKnife.bind(this);
        context = this;
        ageCalender = Calendar.getInstance();

        ageInL.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        ageCalender.set(year, month, dayOfMonth);
                        ageInL.getEditText().setText(FormattingDate.formattedDate(ageCalender.getTime()));
                        birthdate = ageCalender.getTimeInMillis();

                    }
                }, ageCalender.get(Calendar.YEAR), ageCalender.get(Calendar.MONTH), ageCalender.get(Calendar.DAY_OF_MONTH));
                picker.getDatePicker().setMaxDate(System.currentTimeMillis() - 473354280000L);
                picker.show();

            }

        });

        mySpinnerAdapter = new MySpinnerAdapter(this, android.R.layout.simple_list_item_1, StaticFields.ACTIVE_RATE_ARR);
        activeRateSpinner.setAdapter(mySpinnerAdapter);
        activeRateSpinner.setSelection(mySpinnerAdapter.getCount());
        activeRateSpinner.setOnItemSelectedListener(this);

    }

    private boolean validateGender() {
        int id = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(R.id.radio_button_female);
        if (id == -1) {
            radioButton.setError(getString(R.string.required_field));
            return false;
        }

        radioButton.setError(null);
        return true;
    }

    public boolean validateFields(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText().getText().toString().trim().isEmpty()) {
            textInputLayout.setError(getString(R.string.required_field));

            return false;
        }
        textInputLayout.setError(null);
        return true;
    }

    @OnClick(R.id.completeBt)
    public void complete(View view) {
        boolean w = validateFields(weightInL);
        boolean h = validateFields(heightInL);
        boolean lifeSt = !activeRateStr.trim().isEmpty();
        boolean ag = birthdate != -1;
        boolean gen = validateGender();

        boolean allIsValid = w && h && lifeSt && ag && gen;
        if (allIsValid) {
            gender = genderRadioGroup.getCheckedRadioButtonId() == R.id.radio_button_female ? FEMALE : MALE;
            currentUser.setHeight(Double.parseDouble(heightInL.getEditText().getText().toString()));
            currentUser.setWeight(Double.parseDouble(weightInL.getEditText().getText().toString()));
            currentUser.setLifeStyle(activeRateStr);
            currentUser.setBirthdate(birthdate);
            currentUser.setGender(gender);
            currentUser.setNeededCAL();
            DatabaseInstance.getInstance().collection(StaticFields.USER_COLLECTION).document(currentUser.getId()).set(currentUser)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(InfoFormActivity.this, HomeActivity.class));
                                finish();
                            } else {
                                Toast.makeText(InfoFormActivity.this, "Something went wrong" + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (!StaticFields.ACTIVE_RATE_ARR[i].equals("Life style"))
            activeRateStr = StaticFields.ACTIVE_RATE_ARR[i];

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
