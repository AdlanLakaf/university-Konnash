package com.konnash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.konnash.R;
import com.konnash.database.DatabaseHelper;
import com.konnash.models.Transaction;
import com.konnash.utils.DateUtils;

import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "extra_type";

    private String  transactionType;
    private String  currentInput = "0";
    private boolean hasDecimal   = false;

    // Calculator memory
    private double  memoryValue  = 0;
    private String  operator     = null;
    private double  firstOperand = 0;
    private boolean waitingForSecond = false;

    private TextView tvTitle;
    private TextView tvTypeDisplay;
    private TextView tvAmountDisplay;
    private Button   btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        transactionType = getIntent().getStringExtra(EXTRA_TYPE);
        if (transactionType == null) transactionType = Transaction.TYPE_INCOME;

        tvTitle         = findViewById(R.id.tv_title);
        tvTypeDisplay   = findViewById(R.id.tv_type_display);
        tvAmountDisplay = findViewById(R.id.tv_amount_display);
        btnConfirm      = findViewById(R.id.btn_confirm);

        setupUI();
        setupCalculatorKeys();

        // Back button
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Confirm button
        btnConfirm.setOnClickListener(v -> confirmTransaction());
    }

    private void setupUI() {
        boolean isIncome = Transaction.TYPE_INCOME.equals(transactionType);

        String typeLabel = isIncome
                ? getString(R.string.add_income)
                : getString(R.string.add_expense);

        tvTitle.setText(typeLabel);
        tvTypeDisplay.setText(typeLabel);
        tvAmountDisplay.setTextColor(ContextCompat.getColor(this,
                isIncome ? R.color.income_green : R.color.expense_red));

        // Confirm button style
        btnConfirm.setBackgroundResource(isIncome
                ? R.drawable.bg_confirm_income
                : R.drawable.bg_confirm_expense);
        btnConfirm.setTextColor(ContextCompat.getColor(this,
                isIncome ? R.color.income_green : R.color.white));

        updateDisplay();
    }

    // ─── Calculator Logic ──────────────────────────────────

    private void setupCalculatorKeys() {
        int[] digitIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3,
                R.id.btn_4, R.id.btn_5, R.id.btn_6, R.id.btn_7,
                R.id.btn_8, R.id.btn_9
        };
        String[] digits = {"0","1","2","3","4","5","6","7","8","9"};

        for (int i = 0; i < digitIds.length; i++) {
            String digit = digits[i];
            findViewById(digitIds[i]).setOnClickListener(v -> appendDigit(digit));
        }

        // Decimal
        findViewById(R.id.btn_dot).setOnClickListener(v -> {
            if (!hasDecimal) {
                currentInput += ".";
                hasDecimal = true;
                updateDisplay();
            }
        });

        // AC – clear all
        findViewById(R.id.btn_ac).setOnClickListener(v -> {
            currentInput     = "0";
            hasDecimal       = false;
            operator         = null;
            firstOperand     = 0;
            waitingForSecond = false;
            updateDisplay();
        });

        // Backspace
        findViewById(R.id.btn_back_key).setOnClickListener(v -> {
            if (currentInput.length() > 1) {
                char last = currentInput.charAt(currentInput.length() - 1);
                if (last == '.') hasDecimal = false;
                currentInput = currentInput.substring(0, currentInput.length() - 1);
            } else {
                currentInput = "0";
            }
            updateDisplay();
        });

        // Operators
        findViewById(R.id.btn_add).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btn_sub).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btn_mul).setOnClickListener(v -> setOperator("*"));
        findViewById(R.id.btn_div).setOnClickListener(v -> setOperator("/"));

        // Percent
        findViewById(R.id.btn_pct).setOnClickListener(v -> {
            double val = parseCurrentInput() / 100.0;
            currentInput = formatResult(val);
            updateDisplay();
        });

        // Equals
        findViewById(R.id.btn_eq).setOnClickListener(v -> computeResult());

        // M+ memory add
        findViewById(R.id.btn_mplus).setOnClickListener(v ->
                memoryValue += parseCurrentInput());

        // M- memory subtract
        findViewById(R.id.btn_mminus).setOnClickListener(v ->
                memoryValue -= parseCurrentInput());
    }

    private void appendDigit(String digit) {
        if (waitingForSecond) {
            currentInput     = digit;
            hasDecimal       = false;
            waitingForSecond = false;
        } else if ("0".equals(currentInput) && !".".equals(digit)) {
            currentInput = digit;
        } else {
            currentInput += digit;
        }
        updateDisplay();
    }

    private void setOperator(String op) {
        firstOperand     = parseCurrentInput();
        operator         = op;
        waitingForSecond = true;
    }

    private void computeResult() {
        if (operator == null) return;
        double second = parseCurrentInput();
        double result = 0;
        switch (operator) {
            case "+": result = firstOperand + second; break;
            case "-": result = firstOperand - second; break;
            case "*": result = firstOperand * second; break;
            case "/":
                if (second != 0) result = firstOperand / second;
                break;
        }
        currentInput     = formatResult(result);
        operator         = null;
        waitingForSecond = false;
        hasDecimal       = currentInput.contains(".");
        updateDisplay();
    }

    private double parseCurrentInput() {
        try { return Double.parseDouble(currentInput); }
        catch (NumberFormatException e) { return 0; }
    }

    private String formatResult(double val) {
        if (val == Math.floor(val) && !Double.isInfinite(val)) {
            return String.valueOf((long) val);
        }
        return String.format(Locale.US, "%.2f", val);
    }

    private void updateDisplay() {
        tvAmountDisplay.setText(currentInput + " " + getString(R.string.currency));
    }

    // ─── Save Transaction ──────────────────────────────────

    private void confirmTransaction() {
        double amount = parseCurrentInput();
        if (amount <= 0) {
            Toast.makeText(this, getString(R.string.error_empty_amount),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db     = DatabaseHelper.getInstance(this);
        double         balance = db.getCurrentBalance();
        double         newBalance;

        if (Transaction.TYPE_INCOME.equals(transactionType)) {
            newBalance = balance + amount;
        } else {
            newBalance = balance - amount;
        }

        Transaction tx = new Transaction(
                transactionType,
                amount,
                newBalance,
                System.currentTimeMillis()
        );
        db.insertTransaction(tx);

        setResult(Activity.RESULT_OK);
        finish();
    }
}
