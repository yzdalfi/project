package com.example.project3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvSummary;
    private TextView tvEmpty;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        calendarView = findViewById(R.id.calendarView);
        tvSummary = findViewById(R.id.tvSummary);
        tvEmpty = findViewById(R.id.empty);
        recyclerView = findViewById(R.id.recyclerView);

        databaseHelper = new DatabaseHelper(this);
        transactionList = new ArrayList<>();
        //adapter = new TransactionAdapter(transactionList);
        adapter = new TransactionAdapter(transactionList, new TransactionAdapter.OnTransactionClickListener() {
            @Override
            public void onTransactionClick(Transaction transaction) {
                // Contoh: Handle klik transaksi, arahkan ke halaman edit
                Intent intent = new Intent(ReportActivity.this, ModifyTransactionActivity.class);
                intent.putExtra("transaction", transaction); // Pastikan `Transaction` implements Serializable/Parcelable
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Menampilkan data untuk tanggal hari ini
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        loadTransactionsByDate(currentDate);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            //String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);

            loadTransactionsByDate(selectedDate);
        });
    }

    private void loadTransactionsByDate(String date) {
        transactionList.clear();

        // Ambil user_id dari SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = preferences.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = databaseHelper.getTransactionsByUserIdAndDate(userId, date);
        double totalIncome = 0;
        double totalExpense = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    // Ambil nilai dari cursor
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
                    @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));

                    // Buat objek Transaction
                    transactionList.add(new Transaction(id, amount, category, description, date));

                    // Hitung pemasukan dan pengeluaran
                    if (amount > 0) {
                        totalIncome += amount;
                    } else {
                        totalExpense += amount;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error membaca data transaksi", Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();
        }
        // Balik urutan transaksi agar data terbaru berada di atas
        Collections.reverse(transactionList);
        // Perbarui RecyclerView
        adapter.notifyDataSetChanged();

        // Tampilkan ringkasan pemasukan dan pengeluaran
        /*String summary =  "Pemasukan: Rp " + Math.abs(totalExpense) + "\nPengeluaran: Rp " + totalIncome;
        tvSummary.setText(summary);*/
        if (transactionList.isEmpty()) {
            tvEmpty.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
            tvSummary.setText("Pengeluaran: Rp 0");
        } else {
            tvEmpty.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
            String summary = "Pengeluaran: Rp " + totalIncome;
            tvSummary.setText(summary);
        }
    }

}
