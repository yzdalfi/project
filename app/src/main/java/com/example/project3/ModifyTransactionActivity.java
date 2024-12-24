package com.example.project3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ModifyTransactionActivity extends AppCompatActivity {

    private EditText etAmountModify, etDescriptionModify;
    private Spinner spinnerCategory;
    private Button btnUpdate, btnDelete;
    private DatabaseHelper dbHelper;
    private Transaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifytransaction);

        dbHelper = new DatabaseHelper(this);

        // Ambil data dari intent
        transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        if (transaction == null)/*|| transaction.getId() == 0)*/ {
            Toast.makeText(this, "Invalid transaction data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hubungkan ke UI
        etAmountModify = findViewById(R.id.etAmountModify);
        etDescriptionModify = findViewById(R.id.etDescriptionModify);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        // Set data awal
        etAmountModify.setText(String.valueOf(transaction.getAmount()));
        etDescriptionModify.setText(transaction.getDescription());

        // Tombol Update
        btnUpdate.setOnClickListener(v -> {
            try {
                double amount = Double.parseDouble(etAmountModify.getText().toString());
                String description = etDescriptionModify.getText().toString();
                String category = spinnerCategory.getSelectedItem().toString();

                transaction.setAmount(amount);
                transaction.setDescription(description);
                transaction.setCategory(category);

                boolean isUpdated = dbHelper.updateTransaction(transaction);
                if (isUpdated) {
                    Toast.makeText(this, "Transaction updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to update transaction.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol Delete
        btnDelete.setOnClickListener(v -> {
            boolean isDeleted = dbHelper.deleteTransaction(transaction.getId());
            if (isDeleted) {
                Toast.makeText(this, "Transaction deleted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to delete transaction.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
