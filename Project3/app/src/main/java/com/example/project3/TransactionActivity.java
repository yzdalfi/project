package com.example.project3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TransactionActivity extends AppCompatActivity {

    private EditText etAmount, etDescription;
    private Spinner spinnerCategory;
    private DatePicker datePicker;
    private DatabaseHelper databaseHelper;
    private Button btnSaveTransaction;
    private int userId; // Menyimpan ID pengguna

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // Inisialisasi View
        datePicker = findViewById(R.id.datePicker);
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveTransaction = findViewById(R.id.btnSaveTransaction);

        // Inisialisasi DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Ambil userId dari Intent
        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID tidak ditemukan. Harap login kembali.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set Listener untuk tombol Simpan
        btnSaveTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction();
            }
        });
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem() != null
                ? spinnerCategory.getSelectedItem().toString()
                : "";

        // Get the date from DatePicker
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(calendar.getTime());

        // Validasi input
        if (amountStr.isEmpty() || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Masukkan semua data transaksi", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Jumlah transaksi tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simpan transaksi ke database
        boolean success = databaseHelper.insertTransaction(userId, amount, category, description, date);
        if (success) {
            Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan transaksi", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        etAmount.setText("");
        etDescription.setText("");
        if (spinnerCategory.getCount() > 0) {
            spinnerCategory.setSelection(0); // Reset ke kategori pertama
        }
        // Reset DatePicker ke tanggal hari ini
        Calendar calendar = Calendar.getInstance();
        datePicker.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }
}
