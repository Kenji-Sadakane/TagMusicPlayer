package com.example.sada.tagmusicplayer;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

import java.io.File;
import java.io.FileInputStream;

import static com.example.sada.tagmusicplayer.MainActivity.musicItems;

public class MusicFile {

    public static final String MUSIC_FILE_EXTENSION = ".mp3";

    private MainActivity activity;

    StringBuffer sb = new StringBuffer();

    public MusicFile (MainActivity _activity) {
        this.activity = _activity;
    }

    public void readMusicFiles(String dirPath) throws Exception {
        musicItems.clear();
        getMusicFiles(dirPath);
    }

    private void getMusicFiles(String dirPath) throws Exception {
        File[] files = new File(dirPath).listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getMusicFiles(file.getAbsolutePath());
                } else {
                    if (file.getName().endsWith(MUSIC_FILE_EXTENSION)) {
                        musicItems.put(getFileKey(file), new MusicItem(getMusicTitle(file), file, null, null));
                    }
                }
            }
        }
    }

    // 楽曲ファイルの末尾1000byte(ID3v1タグ128byteは除く)をハッシュ化し返却
    private String getFileKey(File file) throws Exception {
        // DigestUtils.sha256hex("..."); は使えない。android SDKが内部的にcommons-codec使ってるのが原因？
        FileInputStream fis = new FileInputStream(file);
        byte[] readBuffer = new byte[50000];
        fis.skip(fis.available() - 50128);
        fis.read(readBuffer);
        fis.close();
        return new String(Hex.encodeHex(DigestUtils.sha256(readBuffer)));
//        return file.getAbsolutePath();
    }

    private String getMusicTitle(File file) throws Exception {
        String musicTitle = file.getName();
//        try {
//            MP3File mp3File = new MP3File(file);
//            if(mp3File.hasID3v2Tag()) {
//                AbstractID3v2 tag = mp3File.getID3v2Tag();
//                if (tag != null && tag.getSongTitle() != null && !tag.getSongTitle().isEmpty()) {
//                    musicTitle = tag.getSongTitle();
//                }
//            } else if(mp3File.hasID3v1Tag()) {
//                ID3v1 tag = mp3File.getID3v1Tag();
//                if (tag != null && tag.getTitle() != null && !tag.getTitle().isEmpty()) {
//                    musicTitle = tag.getTitle();
//                }
//            }
//        } catch (TagException | UnsupportedOperationException te) {
//            MusicMetadataSet src_set = new MyID3().read(file);
//            MusicMetadata metadata = (MusicMetadata) src_set.getSimplified();
//            musicTitle = metadata.getSongTitle();
//        } catch (UnsupportedOperationException te) {
//
//        }
        return musicTitle;
    }
}