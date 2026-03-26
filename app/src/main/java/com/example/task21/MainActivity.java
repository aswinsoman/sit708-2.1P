package com.example.task21;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory, spinnerFrom, spinnerTo;
    private EditText editTextInput;
    private Button buttonConvert;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo = findViewById(R.id.spinner_to);
        editTextInput = findViewById(R.id.edit_text_input);
        buttonConvert = findViewById(R.id.button_convert);
        textViewResult = findViewById(R.id.text_view_result);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void updateUnitSpinners(int categoryIndex) {
        int arrayResId;
        switch (categoryIndex) {
            case 0: arrayResId = R.array.currency_units; break;
            case 1: arrayResId = R.array.fuel_units; break;
            case 2: arrayResId = R.array.temp_units; break;
            default: arrayResId = R.array.currency_units;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
    }

    private void performConversion() {
        String inputStr = editTextInput.getText().toString().trim();
        
        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;
        }

        double inputValue;
        try {
            inputValue = Double.parseDouble(inputStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoryIndex = spinnerCategory.getSelectedItemPosition();
        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit = spinnerTo.getSelectedItem().toString();

        // 1. Validate negative values for Currency (0) and Fuel (1) FIRST
        if ((categoryIndex == 0 || categoryIndex == 1) && inputValue < 0) {
            Toast.makeText(this, "Value cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 2. Validate Temperature Absolute Zero FIRST
        if (categoryIndex == 2 && isBelowAbsoluteZero(inputValue, fromUnit)) {
            Toast.makeText(this, "Value is below absolute zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Handle same Conversions
        if (fromUnit.equals(toUnit)) {
            Toast.makeText(this, "Same units selected", Toast.LENGTH_SHORT).show();
            textViewResult.setText(String.format(Locale.getDefault(), "Result: %.2f %s", inputValue, toUnit));
            return;
        }

        // 4. Check compatibility for Fuel & Distance category
        if (categoryIndex == 1) {
            if (!areUnitsCompatible(fromUnit, toUnit)) {
                Toast.makeText(this, "Incompatible types (e.g. Volume to Distance)", Toast.LENGTH_LONG).show();
                textViewResult.setText("Invalid Conversion");
                return;
            }
        }

        double result = 0;
        if (categoryIndex == 0) result = convertCurrency(inputValue, fromUnit, toUnit);
        else if (categoryIndex == 1) result = convertFuelDistance(inputValue, fromUnit, toUnit);
        else if (categoryIndex == 2) result = convertTemperature(inputValue, fromUnit, toUnit);

        textViewResult.setText(String.format(Locale.getDefault(), "Result: %.2f %s", result, toUnit));
    }

    private boolean areUnitsCompatible(String from, String to) {
        boolean fromIsFuel = from.equals("MPG") || from.equals("KM/L");
        boolean toIsFuel = to.equals("MPG") || to.equals("KM/L");
        if (fromIsFuel && toIsFuel) return true;

        boolean fromIsVol = from.equals("Gallon (US)") || from.equals("Liter");
        boolean toIsVol = to.equals("Gallon (US)") || to.equals("Liter");
        if (fromIsVol && toIsVol) return true;

        boolean fromIsDist = from.equals("Nautical Mile") || from.equals("Kilometer");
        boolean toIsDist = to.equals("Nautical Mile") || to.equals("Kilometer");
        return fromIsDist && toIsDist;
    }

    private boolean isBelowAbsoluteZero(double val, String unit) {
        if (unit.equals("Celsius")) return val < -273.15;
        if (unit.equals("Fahrenheit")) return val < -459.67;
        if (unit.equals("Kelvin")) return val < 0;
        return false;
    }

    private double convertCurrency(double value, String from, String to) {
        double inUSD = from.equals("USD") ? value : from.equals("AUD") ? value/1.55 : 
                       from.equals("EUR") ? value/0.92 : from.equals("JPY") ? value/148.5 : value/0.78;
        return to.equals("USD") ? inUSD : to.equals("AUD") ? inUSD*1.55 : 
               to.equals("EUR") ? inUSD*0.92 : to.equals("JPY") ? inUSD*148.5 : inUSD*0.78;
    }

    private double convertFuelDistance(double value, String from, String to) {
        if (from.equals("MPG") && to.equals("KM/L")) return value * 0.425;
        if (from.equals("KM/L") && to.equals("MPG")) return value / 0.425;
        if (from.equals("Gallon (US)") && to.equals("Liter")) return value * 3.785;
        if (from.equals("Liter") && to.equals("Gallon (US)")) return value / 3.785;
        if (from.equals("Nautical Mile") && to.equals("Kilometer")) return value * 1.852;
        if (from.equals("Kilometer") && to.equals("Nautical Mile")) return value / 1.852;
        return value;
    }

    private double convertTemperature(double value, String from, String to) {
        double c = from.equals("Celsius") ? value : from.equals("Fahrenheit") ? (value-32)/1.8 : value-273.15;
        return to.equals("Celsius") ? c : to.equals("Fahrenheit") ? (c*1.8)+32 : c+273.15;
    }
}