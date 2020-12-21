package com.vullnetlimani.englishdictionary;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vullnetlimani.englishdictionary.adapter.RecyclerViewAdapterHistory;
import com.vullnetlimani.englishdictionary.dataBaseHelper.DatabaseHelper;
import com.vullnetlimani.englishdictionary.dataBaseHelper.LoadDatabaseAsync;
import com.vullnetlimani.englishdictionary.model.History;
import com.vullnetlimani.englishdictionary.util.Constants;
import com.vullnetlimani.englishdictionary.util.Helper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    boolean databaseOpened = false;
    private SearchView search_view;
    private DatabaseHelper databaseHelper;
    private SimpleCursorAdapter suggestionAdapter;
    private RecyclerView recycler_view_history;
    private LinearLayout empty_history;
    private ArrayList<History> historyList;
    private Cursor cursorHistory;
    private RecyclerViewAdapterHistory recyclerViewAdapterHistory;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadViews();
        initDatabase();
        initSuggestions();
        initSearchView();
    }

    private void LoadViews() {

        Toolbar mToolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        Helper.FixFlicker(MainActivity.this, R.id.mAppBarLayout);

        search_view = findViewById(R.id.search_view);
        recycler_view_history = findViewById(R.id.recycler_view_history);
        empty_history = findViewById(R.id.empty_history);

    }

    private void initDatabase() {
        databaseHelper = new DatabaseHelper(MainActivity.this, null);

        if (databaseHelper.checkDatabase()) {
            openDatabase();
        } else {
            LoadDatabaseAsync loadDatabaseAsync = new LoadDatabaseAsync(MainActivity.this);
            loadDatabaseAsync.execute();
        }
    }

    private void initSuggestions() {
        final String[] from = new String[]{DatabaseHelper.EN_WORD};
        final int[] to = new int[]{R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this, R.layout.suggestion_row, null, from, to, 0) {

            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };
        search_view.setSuggestionsAdapter(suggestionAdapter);
    }

    private void initSearchView() {
        ImageView searchIcon = search_view.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView searchCloseIcon = search_view.findViewById(androidx.appcompat.R.id.search_close_btn);
        searchIcon.setColorFilter(Color.DKGRAY);
        searchCloseIcon.setColorFilter(Color.DKGRAY);

        search_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_view.setIconified(false);
            }
        });

        search_view.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                CursorAdapter cursorAdapter = search_view.getSuggestionsAdapter();
                Cursor cursor = cursorAdapter.getCursor();
                cursor.moveToPosition(position);
                String clicked_word = cursor.getString(cursor.getColumnIndex(DatabaseHelper.EN_WORD));

                search_view.setQuery(clicked_word, false);
                search_view.clearFocus();
                search_view.setFocusable(false);

                Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(DatabaseHelper.EN_WORD, clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_in_right_anim, R.anim.push_out_right);
                return false;
            }
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.d("EnglishD", "Ju kerkuat - " + query);

                String kerkimi = search_view.getQuery().toString();

                Cursor cursor = databaseHelper.getMeaning(kerkimi);

                search_view.clearFocus();
                search_view.setFocusable(false);

                if (cursor.getCount() == 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(R.string.word_not_founded);
                    builder.setMessage(R.string.please_search_again);


                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            search_view.setQuery("", false);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(DatabaseHelper.EN_WORD, kerkimi);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_in_right_anim, R.anim.push_out_right);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                search_view.setIconifiedByDefault(false);
                Cursor cursorSuggestion = databaseHelper.getSuggestions(newText);

                if (cursorSuggestion.getCount() > 0) {
                    suggestionAdapter.changeCursor(cursorSuggestion);
                }

                return false;
            }
        });

        layoutManager = new LinearLayoutManager(MainActivity.this);
        recycler_view_history.setLayoutManager(layoutManager);
        fetchHistory();

    }


    private void fetchHistory() {

        historyList = new ArrayList<>();
        recyclerViewAdapterHistory = new RecyclerViewAdapterHistory(this, historyList, new RecyclerViewAdapterHistory.ItemClickListener() {
            @Override
            public void onItemClickListener(int pos, String word, View sharedView) {

                Intent intent = new Intent(MainActivity.this, WordMeaningActivity.class);
                intent.putExtra(DatabaseHelper.EN_WORD, word);
                intent.putExtra(Constants.EN_WORD_TRANSITION, word);

                search_view.setTransitionName(word);

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                        sharedView,
                        word
                );

                startActivity(intent, optionsCompat.toBundle());
            }
        });
        recycler_view_history.setAdapter(recyclerViewAdapterHistory);

        History history;

        if (databaseOpened) {
            cursorHistory = databaseHelper.getHistory();
            if (cursorHistory.moveToFirst()) {

                do {
                    history = new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")),
                            cursorHistory.getString(cursorHistory.getColumnIndex("en_definition")));
                    historyList.add(history);
                }
                while (cursorHistory.moveToNext());

            }

            recyclerViewAdapterHistory.notifyDataSetChanged();

        }

        if (recyclerViewAdapterHistory.getItemCount() == 0) {
            empty_history.setVisibility(View.VISIBLE);
        } else {
            empty_history.setVisibility(View.GONE);

            recycler_view_history.setVisibility(View.VISIBLE);

        }

    }

    public void openDatabase() {

        try {
            databaseHelper.openDatabase();
            databaseOpened = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings_id:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_in_right_anim, R.anim.push_out_right);
                break;
            case R.id.exit_id:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchHistory();
    }
}