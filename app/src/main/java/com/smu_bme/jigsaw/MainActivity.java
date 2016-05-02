package com.smu_bme.jigsaw;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.Chart;
import com.smu_bme.jigsaw.WorkerThreads.WorkerThreads;

import java.sql.Date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    SectionsPagerAdapter mSectionsPagerAdapter;
    WrapContentHeightPager mViewPager;
    TabLayout tabLayout;
    DbHelper dbHelper;
    public Calendar CurrentCalendar = Calendar.getInstance();
    public Calendar ShowedCalendar = CurrentCalendar;
    AlertDialog dialog;
    Intent intent;
    View layout;
    LayoutInflater inflater;
//    public final static int THEME_DEFAULT = 0;
//    public final static int THEME_WHITE = 1;
//    public final static int THEME_BLUE = 2;
//    public final static int THEME_BLUE = 2;
//    public final static int THEME_BLUE = 2;
//    public final static int THEME_BLUE = 2;
//    public final static int THEME_BLUE = 2;
//    public final static int THEME_BLUE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (WrapContentHeightPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setManager(getWindowManager());
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            private double duration = 0.15;
            @Override
            public void onClick(View v) {
                inflater = LayoutInflater.from(MainActivity.this);
                layout = inflater.inflate(R.layout.dialog_create, null);
                final EditText name = (EditText) layout.findViewById(R.id.create_name);
                final EditText remark = (EditText) layout.findViewById(R.id.create_remark);
                final TextView textView = (TextView) layout.findViewById(R.id.show_duration);
                textView.setText(0.15 + "hours");
                ImageButton imageButton = (ImageButton) layout.findViewById(R.id.edit_duration);
                final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekBar);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                        popupMenu.inflate(R.menu.duration);
                        popupMenu.getMenu().findItem(R.id.quart_hour).setChecked(true);
                        final TextView textView = (TextView) layout.findViewById(R.id.show_duration);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.quart_hour: duration = 0.15;  break;
                                    case R.id.half_hour: duration = 0.5; break;
                                    case R.id.one_hour: duration = 1; break;
                                    case R.id.one_and_half_hour: duration = 1.5; break;
                                    case R.id.two_hour: duration = 2; break;
                                    case R.id.three_hour: duration = 3; break;
                                }
                                textView.setText(duration + "hours");
                                seekBar.setProgress((int)(duration * 4 - 1));
                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                });
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        duration = progress * 0.25 + 0.25;
                        textView.setText(duration + "hours");
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        textView.setText("Tracking");
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        textView.setText(duration + "hours");
                    }
                });
                dialog = new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.create)).setView(layout).setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            String CurrentDateString = new SimpleDateFormat("yyyy-MM-dd").format(CurrentCalendar.getTime());
                            String CurrentTimeString = new SimpleDateFormat("mm:ss").format(CurrentCalendar.getTime());
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nameInput = name.getText().toString();
                                String remarkInput = remark.getText().toString();
                                if (nameInput.equals("")) {
                                    Toast.makeText(MainActivity.this, getString(R.string.noName), Toast.LENGTH_SHORT).show();
                                } else {
                                    dbHelper = new DbHelper(MainActivity.this);
                                    DbData dbData;
                                    if (remarkInput.equals("")){
                                        dbData = new DbData(CurrentDateString, CurrentTimeString ,(int)duration * 3600, nameInput);
                                        dbHelper.addData(dbData);
                                    } else {
                                        dbData = new DbData(CurrentDateString, CurrentTimeString ,(int)duration * 3600, nameInput, remarkInput);
                                        dbHelper.addData(dbData);
                                    }
                                    intent = new Intent(MainActivity.this, TimerActivity.class);
                                    intent.putExtra("Event", dbData);
                                    startActivity(intent);
                                }
                            }
                        }).setNegativeButton(getString(R.string.cancel), null).show();
            }
        });
//        Intent intent = getIntent();
//        if (intent.getStringExtra("Action").equals("button")){
//           fab.callOnClick();
//        } else if (intent.getStringExtra("Action").equals("dialog_create")) {
//            new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.create))
//                    .setPositiveButton(getString(R.string.yes),null).show();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        inflater = LayoutInflater.from(MainActivity.this);
        switch (item.getItemId()){
            case R.id.theme:
                layout = inflater.inflate(R.layout.dialog_select_theme, null);
                dialog = new AlertDialog.Builder(MainActivity.this).setView(layout).setNegativeButton(getString(R.string.cancel), null)
                        .show();
                break;
            case R.id.back_to_jigsaw: intent = new Intent(MainActivity.this, JigsawActivity.class);
                startActivity(intent);
                break;
            case R.id.background:
                layout = inflater.inflate(R.layout.dialog_select_background, null);
                dialog = new AlertDialog.Builder(MainActivity.this).setView(layout).setNegativeButton(getString(R.string.cancel), null)
                        .show();
                break;
//            case R.id.tutorial: intent = new Intent(MainActivity.this, MainActivity.this);
//                break;
            case R.id.about_jigsaw:
                layout = inflater.inflate(R.layout.dialog_about_jigsaw, null);
                dialog = new AlertDialog.Builder(MainActivity.this).setView(layout).setTitle(getString(R.string.about_jigsaw)).setPositiveButton(getString(R.string.yes), null).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        public static Calendar CurrentCalendar = Calendar.getInstance(Locale.CHINA);
        public Calendar ShowedCalendar = CurrentCalendar;
        public static LogUI logUI;
        private static ChartView chart;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt("section number", sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view;
            if (getArguments().getInt("section number") == 1) {
                logUI = new LogUI(getContext(), inflater, container);
                view = logUI.getView(getContext(), ShowedCalendar);
            } else {

                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                WorkerThreads workerThreads = new WorkerThreads(inflater,container,getContext());
//                workerThreads.refreshDate(ShowedCalendar);
//                view = workerThreads.getView();
//                return initChart(inflater, container);
                ChartView chart = new ChartView(inflater, container, getContext(), ShowedCalendar);
//                ShowedCalendar.setTime(Date.valueOf("1970-1-1"));
                view = chart.getView(ShowedCalendar);


                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
            return view;
        }

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

            @Override
            public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.Log);
                case 1:
                    return getString(R.string.Data);
            }
            return null;
        }
    }
}
