package com.keepingrack.tagmusicplayer;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.db.logic.MusicTagsLogic;
import com.keepingrack.tagmusicplayer.file.ApplicationLog;
import com.keepingrack.tagmusicplayer.file.MusicFile;
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

import static com.keepingrack.tagmusicplayer.MainActivityLogic.*;
import static com.keepingrack.tagmusicplayer.util.StringUtil.concat;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    private static String[] mPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSION_CODE = 1;

//    public static final String BASE_DIR = "/storage/sdcard1/PRIVATE/SHARP/CM/MUSIC";
//    public static final String BASE_DIR = "/storage/6231-3131/PRIVATE/SHARP/CM/MUSIC";
    public static final String BASE_DIR = "/storage/6366-6430/private/music";

    public static MainActivity activity;
    public static MediaPlayer mp;
    public static int DISPLAY_WIDTH;
    public static String PLAYING_MUSIC;

    public GrayPanel grayPanel;
    public KeyWordEditText keyWordEditText;
    public LoopButton loopButton;
    public MsgView msgView;
    public MusicFile musicFile;
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
    public String baseDirPath = "";

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

        activity = this;
        measureDisplayWidth();
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

        try {
            // 権限確認
            if (grantedPermission()) {
                afterGranted();
            } else {
                requestPermission();
            }
        } catch (Exception e) {
            ApplicationLog.error(e);
            alertErrorDialog();
        }
    }

    // 必要権限が付与されているかチェック
    private boolean grantedPermission() {
        boolean granted = true;
        for (String mPermission : mPermissions) {
            if (ContextCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {
                granted = false;
            }
        }
        return granted;
    }

    // 権限付与リクエスト
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, mPermissions, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean allGranted = true;
            for (int i=0; i<permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                    }
                }
            }
            if (allGranted) {
                afterGranted();
            } else {
                alertFinishDialog();
            }
        }
    }

    private void afterGranted() {
        try {
            setBaseDirPath();
            // 各種変数初期化
            initializeField();
            // タグ、楽曲表示
            initProcess();
        } catch (Exception e) {
            ApplicationLog.error(e);
            alertErrorDialog();
        }
    }

    // 基底ディレクトリ設定
    private void setBaseDirPath() throws Exception {
        baseDirPath = ApplicationDirectory.getAbsolutePath(this, false);
        if (baseDirPath.isEmpty()) {
            // アプリケーションパスが取得できなければアプリ終了
            ApplicationLog.error("アプリケーションパス取得失敗");
            alertErrorDialog();
        }
        ApplicationLog.info(concat("baseDirPath=", baseDirPath));
    }

    private void initializeField() {
        mp = new MediaPlayer();
        PLAYING_MUSIC = "";

        musicFile = new MusicFile(this);
        musicPlayer = new MusicPlayer(this);
        musicTagsLogic = new MusicTagsLogic(this);
        shuffleMusicList = new ShuffleMusicList(this);
        relateTagLogic = new RelateTagLogic(this);
        tagInfoDialog = new TagInfoDialog(this);
        Variable.initialize();
    }

    public void hideKeyBoard() {
        ((TextView) activity.findViewById(R.id.dummyText)).requestFocus();
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
    private void finishApp() {
        try {
            if (!PLAYING_MUSIC.isEmpty()) {
                stopMusic();
            }
            finishThread();
            super.onBackPressed();
            super.finish();
        } catch (Exception e) {
            ApplicationLog.error(e);
            super.finish();
        }
    }

    // アプリ終了通知ダイアログ表示
    private AlertDialog alertFinishDialog() {
        return new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("アプリ終了")
                .setMessage("本アプリ実行に必要な権限が付与されていないため、アプリを終了します。")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishApp();
                    }
                })
                .show();
    }

    // アプリエラー終了通知ダイアログ表示
    public AlertDialog alertErrorDialog() {
        return new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("アプリ終了")
                .setMessage("システムエラーが発生しました。アプリを終了します。")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishApp();
                    }
                })
                .show();
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
                // 設定押下
                return true;
            } else if (id == R.id.action_select) {
                // 全て選択押下
                for (String key : Variable.getDisplayMusicNames()) {
                    Variable.getMusicRow(key).getCheckBox().checkManually(true);
                }
                return true;
            } else if (id == R.id.action_deselect) {
                // 選択解除押下
                for (String key : Variable.getDisplayMusicNames()) {
                    Variable.getMusicRow(key).getCheckBox().checkManually(false);
                }
            } else if (id == R.id.action_update) {
                // 更新ボタン押下
                updateProcess();
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
