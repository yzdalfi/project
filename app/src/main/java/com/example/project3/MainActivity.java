package com.example.project3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMain;
    private TextView tvEmptyMain;
    private TransactionAdapter adapter;
    private ArrayList<Transaction> transactionList;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi view
        recyclerViewMain = findViewById(R.id.recyclerViewMain);
        tvEmptyMain = findViewById(R.id.empty);
        transactionList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        adapter = new TransactionAdapter(transactionList, new TransactionAdapter.OnTransactionClickListener() {
            @Override
            public void onTransactionClick(Transaction transaction) {
                // Contoh: Handle klik transaksi, arahkan ke halaman edit
                Intent intent = new Intent(MainActivity.this, ModifyTransactionActivity.class);
                intent.putExtra("transaction", transaction); // Pastikan `Transaction` implements Serializable/Parcelable
                startActivity(intent);
            }
        });
        //adapter = new TransactionAdapter(transactionList);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMain.setAdapter(adapter);

        // Ambil data dari intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        int userId = intent.getIntExtra("USER_ID", -1);

        TextView headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText("Hello, " + username + "!");

        loadUserTransactions(userId);

        findViewById(R.id.btnAddTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent transactionIntent = new Intent(MainActivity.this, TransactionActivity.class);
                transactionIntent.putExtra("USER_ID", userId);
                startActivity(transactionIntent);
            }
        });

        findViewById(R.id.btnViewReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),ReportActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = getIntent().getStringExtra("username");
        int userId = databaseHelper.getUserId(username);
        // Memuat ulang data setiap kali aktivitas ini terlihat
        loadUserTransactions(userId);
    }


    private void loadUserTransactions(int userId) {
        transactionList.clear();

        Cursor cursor = databaseHelper.getTransactionsByUserId(userId);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                // Parsing transaksi
                try {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
                    @SuppressLint("Range") String category = cursor.getString(cursor.getColumnIndex("category"));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex("description"));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));

                    transactionList.add(new Transaction(id, amount, category, description, date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        // Balik urutan transaksi agar data terbaru berada di atas
        Collections.reverse(transactionList);

        // Update UI
        if (transactionList.isEmpty()) {
            tvEmptyMain.setVisibility(TextView.VISIBLE);
            recyclerViewMain.setVisibility(RecyclerView.GONE);
        } else {
            tvEmptyMain.setVisibility(TextView.GONE);
            recyclerViewMain.setVisibility(RecyclerView.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }
}
