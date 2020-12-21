package com.vullnetlimani.englishdictionary;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vullnetlimani.englishdictionary.dataBaseHelper.DatabaseHelper;
import com.vullnetlimani.englishdictionary.fragments.FragmentAntonyms;
import com.vullnetlimani.englishdictionary.fragments.FragmentDefinition;
import com.vullnetlimani.englishdictionary.fragments.FragmentExample;
import com.vullnetlimani.englishdictionary.fragments.FragmentSynonyms;
import com.vullnetlimani.englishdictionary.util.Constants;
import com.vullnetlimani.englishdictionary.util.Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordMeaningActivity extends AppCompatActivity {
    public String enDefinition;
    public String example;
    public String synonyms;
    public String antonyms;
    String[] mTabTitles = new String[]{"Definition", "Synonyms", "Antonyms", "Example"};
    private String enWord;
    private DatabaseHelper myDatabaseHelper;
    private Cursor mCursor = null;
    private TabLayout tabLayout_id;
    private ViewPager2 viewPager2_id;
    private FloatingActionButton btnSpeak;
    private TextToSpeech textToSpeech;

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            supportFinishAfterTransition();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        initData();
        LoadViews();
        initViewPagerTabLayout();
        readTheWord();

    }

    private void initData() {

        supportPostponeEnterTransition();

        Bundle bundle = getIntent().getExtras();
        enWord = bundle.getString(DatabaseHelper.EN_WORD);

        Helper.FixFlicker(WordMeaningActivity.this, R.id.mAppBarLayout);

        String transition = bundle.getString(Constants.EN_WORD_TRANSITION);
        findViewById(R.id.mAppBarLayout).setTransitionName(transition);

        supportStartPostponedEnterTransition();

        myDatabaseHelper = new DatabaseHelper(WordMeaningActivity.this, null);

        myDatabaseHelper.openDatabase();

        mCursor = myDatabaseHelper.getMeaning(enWord);

        if (mCursor.moveToFirst()) {

            enDefinition = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EN_DEFINITION));
            example = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.EXAMPLE));
            synonyms = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.SYNONYMS));
            antonyms = mCursor.getString(mCursor.getColumnIndex(DatabaseHelper.ANTONYMS));

        }

        myDatabaseHelper.insertHistory(enWord);
    }

    private void LoadViews() {
        Toolbar toolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(enWord);

        toolbar.setNavigationIcon(R.drawable.ic_back_icon);


        tabLayout_id = findViewById(R.id.tabLayout_id);
        viewPager2_id = findViewById(R.id.viewPager_id);
        btnSpeak = findViewById(R.id.btnSpeak);
    }

    private void initViewPagerTabLayout() {
        tabLayout_id.setBackgroundColor(getResources().getColor(R.color.primary));
        tabLayout_id.setTabRippleColor(ColorStateList.valueOf(Color.WHITE));
        tabLayout_id.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        tabLayout_id.setTabTextColors(Color.WHITE, Color.WHITE);

        setupViewPager(viewPager2_id);

        new TabLayoutMediator(tabLayout_id, viewPager2_id, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mTabTitles[position]);
            }
        }).attach();

        tabLayout_id.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2_id.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void readTheWord() {
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(WordMeaningActivity.this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {

                            int result = textToSpeech.setLanguage(Locale.getDefault());

                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                                Log.e(Constants.TAG, "This language is not supported");

                            } else {
                                textToSpeech.speak(enWord, TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                        } else {
                            Log.e(Constants.TAG, "Init Failed! - " + status);
                        }
                    }
                });
            }
        });
    }


    private void setupViewPager(ViewPager2 viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPagerAdapter.addFragment(new FragmentDefinition(this));
        viewPagerAdapter.addFragment(new FragmentSynonyms(this));
        viewPagerAdapter.addFragment(new FragmentAntonyms(this));
        viewPagerAdapter.addFragment(new FragmentExample(this));

        viewPager.setAdapter(viewPagerAdapter);
    }

    public boolean checkDataEmpty(String text) {
        return text.equals("NA") || text.equals("NA,NA") || text.equals("NA,NA,NA") || text.equals("NA,NA,NA,NA");
    }

    public static class ViewPagerAdapter extends FragmentStateAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public int getItemCount() {
            return mFragmentList.size();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragmentList.get(position);
        }
    }
}