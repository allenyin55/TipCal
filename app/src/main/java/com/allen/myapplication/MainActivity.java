package com.allen.myapplication;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    //Declare all the EditText, checkboxes and button.

    EditText billBeforeTipET;
    EditText billAmountET;
    EditText tipRateET;
    EditText tipAmountET;

    CheckBox goodCheckBox;
    CheckBox normalCheckBox;
    CheckBox badCheckBox;

    Button startBT;
    Button pauseBT;
    Button resetBT;

    Spinner hotnessSpinner;

    Chronometer waitingTimeCM;

    SeekBar changeTipSeekBar;

    //Declare all the doubles to hold the data from EditText;

    private double billBeforeTip;
    private double billAmount;
    private double tipRate;
    private double tipAmount;
    private int [] checkValueList = new int[6];
    private long secondsYouveWaited;
    private double tipFromList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize all the buttons and stuff.

        billBeforeTipET = (EditText) findViewById(R.id.billBeforeTipET);
        billAmountET = (EditText) findViewById(R.id.billAmountET);
        tipRateET = (EditText) findViewById(R.id.tipRateET);
        tipAmountET = (EditText) findViewById(R.id.tipAmountET);

        //Change the bill when the billBeforeTipET is changed
        billBeforeTipET.addTextChangedListener(billBeforeTipListener);

        //Give EditTexts' values to double
        setDoubleValues();

        goodCheckBox = (CheckBox) findViewById(R.id.goodCheckBox);
        normalCheckBox = (CheckBox) findViewById(R.id.normalCheckBox);
        badCheckBox = (CheckBox) findViewById(R.id.badCheckBox);

        //Set up checkboxes
        setCheckBoxes();

        startBT = (Button) findViewById(R.id.startButton);
        pauseBT = (Button) findViewById(R.id.pauseButton);
        resetBT = (Button) findViewById(R.id.resetButton);

        //set onClickListener for buttons
        setButtonOnClickListener();

        //Set up spinner
        hotnessSpinner = (Spinner) findViewById(R.id.hotnessSpinner);

        //Set listener for the spinner
        setItemSelectedListenerToSpinner();

        //Set up chronometer and its listener
        waitingTimeCM = (Chronometer)findViewById(R.id.timeWaitingCM);

        //Set up Seek bar and its listener
        changeTipSeekBar = (SeekBar)findViewById(R.id.seekBar);
        changeTipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);
    }

    private TextWatcher billBeforeTipListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try{

                // Change the billBeforeTip to the new input

                billBeforeTip = Double.parseDouble(editable.toString());


            }

            catch(NumberFormatException e){

                billBeforeTip = 0.0;

            }

            updateFinalBill();


        }
    };

    public void setDoubleValues() {
        try{

            // Change the billBeforeTip to the new input

            billBeforeTip = Double.parseDouble(billBeforeTipET.toString());
            billAmount = Double.parseDouble(billAmountET.getText().toString());
            tipRate = Double.parseDouble(tipRateET.getText().toString());

        }

        catch(NumberFormatException e){

            billBeforeTip = 0.0;
            billAmount = 0.0;
            tipRate = 0.15;

        }

    }


    public void setButtonOnClickListener(){

        startBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stoppedMilliseconds = 0;

                // Get time from the chronometer

                String chronoText = waitingTimeCM.getText().toString();
                String array[] = chronoText.split(":");
                if (array.length == 2) {

                    // Find the seconds

                    stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000
                            + Integer.parseInt(array[1]) * 1000;
                } else if (array.length == 3) {

                    // Find the minutes

                    stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000
                            + Integer.parseInt(array[1]) * 60 * 1000
                            + Integer.parseInt(array[2]) * 1000;
                }

                // Amount of time elapsed since the start button was
                // pressed, minus the time paused

                waitingTimeCM.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);

                // Set the number of seconds you have waited
                // This would be set for minutes in the real world
                // obviously. That can be found in array[2]

                secondsYouveWaited = Long.parseLong(array[1]);

                updateTipBasedOnTimeWaited(secondsYouveWaited);

                // Start the chronometer

                waitingTimeCM.start();

            }
        });

        pauseBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingTimeCM.stop();
            }
        });

        resetBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingTimeCM.setBase(SystemClock.elapsedRealtime());
            }
        });

    }

    public void setCheckBoxes(){

        //Set goodCheckBox by adding 4 into checkValueList if the box is checked.
        goodCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkValueList[0] = (goodCheckBox.isChecked())?4:0;
        }
    });

        //Set normalCheckBox by adding 1 into checkValueList if the box is checked.
        normalCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkValueList[1] = (normalCheckBox.isChecked())?1:0;
            }
        });

        //Set badCheckBox by subtracting 4 from checkValueList if the box is checked.
        badCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkValueList[2] = (badCheckBox.isChecked())?-4:0;
            }
        });

        setTipFromCheckList();
        updateFinalBill();

    }

    private void setItemSelectedListenerToSpinner() {
        hotnessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkValueList[3] = (String.valueOf(hotnessSpinner.getSelectedItem()).equals("Looking hot"))?3:0;
                checkValueList[4] = (String.valueOf(hotnessSpinner.getSelectedItem()).equals("Giving you free treat"))?3:0;
                checkValueList[5] = (String.valueOf(hotnessSpinner.getSelectedItem()).equals("Smelling good"))?3:0;

                setTipFromCheckList();
                updateFinalBill();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

        private SeekBar.OnSeekBarChangeListener tipSeekBarListener = new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Get the value set on the SeekBar

                tipRate = (changeTipSeekBar.getProgress()) * .01;

                // Set tipAmountET with the value from the SeekBar

                tipRateET.setText(String.format("%.02f", tipRate));

                // Update all the other EditTexts

                updateFinalBill();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

    public void updateFinalBill(){
        tipAmount = billBeforeTip*tipRate+tipFromList;
        billAmount = billBeforeTip+tipAmount;

        tipAmountET.setText(String.format("%.02f", tipAmount));
        billAmountET.setText(String.format("%.02f", billAmount));

    }

    public void setTipFromCheckList(){
        int checkListValue = 0;

        for (int value: checkValueList){
            checkListValue += value;
            Log.i("allenMessage", String.valueOf(checkListValue));
        }

        tipFromList += checkListValue*0.1;

        updateFinalBill();

    }

    private void updateTipBasedOnTimeWaited(long secondsYouWaited){

        // If you spent less then 10 seconds then add 2 to the tip
        // if you spent more then 10 seconds subtract 2

        checkValueList[6] = (secondsYouWaited > 10)?-2:2;

        // Calculate tip using the waitress checklist options

        setTipFromCheckList();

        // Update all the other EditTexts

        updateFinalBill();

    }



}
