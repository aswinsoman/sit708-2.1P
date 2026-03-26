# Travel Companion App

A simple Android application designed to help international travellers convert essential values.

## Features

- **Currency Conversion:** Supports USD, AUD, EUR, JPY, and GBP using fixed 2026 rates.
- **Fuel Efficiency & Distance:**
  - MPG to KM/L
  - Gallons (US) to Liters
  - Nautical Miles to Kilometers
- **Temperature Conversion:** Supports Celsius, Fahrenheit, and Kelvin.
- **Safety & Validation:**
  - Prevents empty or non-numeric inputs.
  - Blocks negative values for Currency and Fuel Efficiency.
  - Handles "Identity Conversions" (e.g., USD to USD).
  - Validates Temperature against Absolute Zero.
  - Ensures unit compatibility (e.g., won't convert Volume to Distance).

## How to Use

1. Select a **Category** (Currency, Fuel & Distance, or Temperature) from the top dropdown.
2. Choose your **Source Unit** and **Destination Unit**.
3. Enter the value you want to convert in the text field.
4. Tap the **CONVERT** button to see the result.

## Development

Built using:
- Java
- XML (Basic LinearLayout)
- Android Studio
- Min SDK: 27
- Target SDK: 36
