package com.keepingrack.tagmusicplayer;

import com.keepingrack.tagmusicplayer.external.db.entity.MusicTagsRecord;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.keepingrack.tagmusicplayer.MainActivity.BASE_DIR;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.musicKeys;
import static com.keepingrack.tagmusicplayer.MainActivity.tagKinds;
import static com.keepingrack.tagmusicplayer.external.db.helper.MusicTagsHelper.SEPARATE;
import static com.keepingrack.tagmusicplayer.util.Utility.*;

public class MusicFile {

    public static final String MUSIC_FILE_EXTENSION = ".mp3";
    public static final List<String> TAG_NOTHING_LIST = Arrays.asList("タグなし");
    private Map<String, MusicTagsRecord> musicTagsRecordMap;

    private MainActivity activity;

    public MusicFile (MainActivity _activity) {
        this.activity = _activity;
    }

    public void readMusicFilesAndDatabase() throws Exception {
        tagKinds.clear();
        musicKeys.clear();
        musicItems.clear();
        musicTagsRecordMap = activity.musicTagsLogic.selectAllRecords();
        getMusicFiles(BASE_DIR);
    }

    // ストレージから楽曲ファイルを取得
    private void getMusicFiles(String dirPath) throws Exception {
        File[] files = new File(dirPath).listFiles();
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getMusicFiles(file.getAbsolutePath());
                } else {
                    if (file.getName().endsWith(MUSIC_FILE_EXTENSION)) {
                        String key = getFileKey(file);
                        MusicTagsRecord musicTagsRecord = musicTagsRecordMap.get(key);
                        List<String> tags = musicTagsRecord != null ? stringToList(musicTagsRecord.getTags(), SEPARATE) : TAG_NOTHING_LIST;
                        tagKinds.addAll(tags);
                        musicKeys.add(key);
                        musicItems.put(key, new MusicItem(file.getAbsolutePath(), file.getName(), tags, null));
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
