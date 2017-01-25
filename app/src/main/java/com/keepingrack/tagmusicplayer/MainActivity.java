package com.keepingrack.tagmusicplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.external.db.logic.MusicTagsLogic;
import com.keepingrack.tagmusicplayer.external.file.MusicFile;
import com.keepingrack.tagmusicplayer.layout.GrayPanel;
import com.keepingrack.tagmusicplayer.layout.topField.KeyWordEditText;
import com.keepingrack.tagmusicplayer.layout.bottomField.LoopButton;
import com.keepingrack.tagmusicplayer.layout.bottomField.MusicSeekBar;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagLayout;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagLink;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagScrollView;
import com.keepingrack.tagmusicplayer.layout.topField.SearchButton;
import com.keepingrack.tagmusicplayer.layout.topField.SearchSwitch;
import com.keepingrack.tagmusicplayer.layout.TagInfoDialog;
import com.keepingrack.tagmusicplayer.layout.topField.MsgView;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicScrollView;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, Runnable {

//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//    private static int CURRENT_PERMISSION;

    public static final String BASE_DIR = "/storage/sdcard1/PRIVATE/SHARP/CM/MUSIC";

    public static MainActivity activity;
    public static MediaPlayer mp;
    public static int DISPLAY_WIDTH;
    public static String PLAYING_MUSIC;

    public GrayPanel grayPanel;
    public KeyWordEditText keyWordEditText;
    public LoopButton loopButton;
    public MsgView msgView;
    private MusicFile musicFile;
    public MusicLinearLayout musicLinearLayout;
    public MusicPlayer musicPlayer;
    public MusicScrollView musicScrollView;
    public MusicTagsLogic musicTagsLogic;
    public ShuffleMusicList shuffleMusicList;
    public MusicSeekBar musicSeekBar;
    public RelateTagLogic relateTagLogic;
    public RelateTagLayout relateTagLayout;
    public RelateTagLink relateTagLink;
    public RelateTagScrollView relateTagScrollView;
    public SearchButton searchButton;
    public SearchSwitch searchSwitch;
    public TagInfoDialog tagInfoDialog;
    public Handler handler = new Handler();

    private void initializeField() {
        activity = this;
        measureDisplayWidth();
        mp = new MediaPlayer();
        PLAYING_MUSIC = "";

        keyWordEditText = (KeyWordEditText) findViewById(R.id.keyWordEditText);
        grayPanel = (GrayPanel) findViewById(R.id.grayPanel);
        loopButton = (LoopButton) findViewById(R.id.loopButton);
        msgView = (MsgView) findViewById(R.id.msgView);
        musicLinearLayout = (MusicLinearLayout) findViewById(R.id.linearLayout);
        musicSeekBar = (MusicSeekBar) findViewById(R.id.seekBar);
        musicScrollView = (MusicScrollView) findViewById(R.id.scrollView);
        searchSwitch = (SearchSwitch) findViewById(R.id.searchSwitch);
        relateTagLayout = (RelateTagLayout) findViewById(R.id.relateTagLayout);
        relateTagLink = (RelateTagLink) findViewById(R.id.switchRelateTagText);
        relateTagScrollView = (RelateTagScrollView) findViewById(R.id.relateTagScrollView);
        searchButton = (SearchButton) findViewById(R.id.searchButton);

        musicFile = new MusicFile(this);
        musicPlayer = new MusicPlayer(this);
        musicTagsLogic = new MusicTagsLogic(this);
        shuffleMusicList = new ShuffleMusicList(this);
        relateTagLogic = new RelateTagLogic(this);
        tagInfoDialog = new TagInfoDialog(this);
        handler = new Handler();
        Variable.initialize();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //// デフォルトコード開始
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //// デフォルトコード終了

        // 権限確認
//        verifyStoragePermissions(this);
//        if (CURRENT_PERMISSION != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }

        try {
            initializeField();
            // タグ、楽曲表示
            final ProgressDialog progressDialog = startLoading();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        musicTagsLogic.selectAndReflectTags();
                        musicLinearLayout.createContents();
                        musicLinearLayout.changeMusicList();
                    } catch (Exception ex) {
                        msgView.outErrorMessage(ex);
                    } finally {
                        endLoading(progressDialog);
                    }
                }
            }).start();
            //
            Thread currentThread = new Thread(this);
            currentThread.start();
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }
    }

    private ProgressDialog startLoading() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((View) findViewById(R.id.grayPanel)).setVisibility(View.VISIBLE);
            }
        });
        ProgressDialog progressDialog = showProgressDialog();
        return progressDialog;
    }

    private ProgressDialog showProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    private void endLoading(final ProgressDialog progressDialog) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((View) findViewById(R.id.grayPanel)).setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
    }

    private void update() {
        try {
            if (!PLAYING_MUSIC.isEmpty()) {
                stopMusic();
            }
            final ProgressDialog progressDialog = startLoading();
            keyWordEditText.setText("");
            relateTagLogic.initializeTagField();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 楽曲ファイル捜索
                        musicFile.readMusicFilesAndDatabase();
                        // DB更新
                        musicTagsLogic.deleteAll();
                        musicTagsLogic.insertAll();
                        // 楽曲リスト(画面部品)作成
                        musicLinearLayout.createContents();
                        musicPlayer.showTrackNo();
                    } catch (Exception ex) {
                        msgView.outErrorMessage(ex);
                    } finally {
                        endLoading(progressDialog);
                    }
                }
            }).start();
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }
    }

    public void hideKeyBoard() {
        ((TextView) activity.findViewById(R.id.dummyText)).requestFocus();
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 再生時間更新
                if (!PLAYING_MUSIC.isEmpty()) { musicSeekBar.setPlayTime(); }
                // シークバー＆ボタン表示
                if (!musicSeekBar.visible && !keyWordEditText.focusOn) {
                    musicSeekBar.hiddenTime++;
                    if (musicSeekBar.hiddenTime > 2) {
                         musicSeekBar.visible();
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
            //
        }
    }

    public void playMusic(String key) throws Exception {
        musicLinearLayout.resetMusicRowBackGround();
        musicPlayer.startMusicPlayer(key);
        musicPlayer.showTrackNo();
        musicLinearLayout.selectMusicAndDeselectOldMusic(key);
        musicScrollView.scrollMusicView();
        musicLinearLayout.setMusicRowBackGround();
        loopButton.playButtonCheck(true);
    }

    public void stopMusic() throws Exception {
        musicLinearLayout.resetMusicRowBackGround();
        loopButton.playButtonCheck(false);
        musicPlayer.stopMusicPlayer();
        musicPlayer.showTrackNo();
        musicSeekBar.setTotalTime(0);
        musicSeekBar.setPlayTime(0);
    }

//    // クリアボタン押下時
//    public void onClearClicked(View v) {
//        ((EditText) findViewById(R.id.editText)).setText("");
//        keyWord.afterTextChangedProcess();
//    }

//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        CURRENT_PERMISSION = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (CURRENT_PERMISSION != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }

    // 戻る押下時処理
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            goHome();
        }
    }

    // ホーム画面移行
    private void goHome() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.this.startActivity(homeIntent);
    }

    // アプリ終了
    private void finishApp() throws Exception {
        if (!PLAYING_MUSIC.isEmpty()) {
            stopMusic();
        }
        super.onBackPressed();
        super.finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        measureDisplayWidth();
    }

    private void measureDisplayWidth() {
        Point outSize = new Point();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(outSize);
        DISPLAY_WIDTH = outSize.x;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        try {
            if (id == R.id.action_settings) {
                return true;
            } else if (id == R.id.action_update) {
                // 更新ボタン押下
                update();
                return true;
            } else if (id == R.id.action_exit) {
                // 終了ボタン押下
                finishApp();
                return true;
            }
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
