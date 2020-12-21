package com.vullnetlimani.englishdictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.vullnetlimani.englishdictionary.about.About;
import com.vullnetlimani.englishdictionary.dataBaseHelper.DatabaseHelper;
import com.vullnetlimani.englishdictionary.util.Constants;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_icon);

        MySettingsFragment mySettingsFragment = new MySettingsFragment();
        mySettingsFragment.setActivity(SettingsActivity.this);
        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, mySettingsFragment).commit();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            finish();
        overridePendingTransition(R.anim.push_in_left, R.anim.push_out_left);

        return super.onOptionsItemSelected(item);
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat implements PreferenceManager.OnPreferenceTreeClickListener {
        DatabaseHelper myDBHelper;
        private AppCompatActivity mParentActivity;

        public void setActivity(AppCompatActivity activity) {
            this.mParentActivity = activity;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.prefrences, rootKey);
            myDBHelper = new DatabaseHelper(mParentActivity, null);
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            Log.d(Constants.TAG, "onPreferenceTreeClick - " + preference.getKey());

            switch (preference.getKey()) {
                case Constants.CLEAR_HISTORY_KEY:
                    myDBHelper.openDatabase();
                    showAlertDialog();
                    break;
                case Constants.ABOUT_KEY:

                    Intent intent = new Intent(mParentActivity, About.class);
                    startActivity(intent);
                    mParentActivity.overridePendingTransition(R.anim.push_in_right_anim, R.anim.push_out_right);

                    break;
            }

            return false;
        }

        private void showAlertDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mParentActivity);
            builder.setTitle("Are you sure?");
            builder.setMessage("All the history will be deleted");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myDBHelper.deleteHistory();
                    Toast.makeText(mParentActivity, "List deleted successfully", Toast.LENGTH_SHORT).show();

                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();

            dialog.show();


        }
    }


}