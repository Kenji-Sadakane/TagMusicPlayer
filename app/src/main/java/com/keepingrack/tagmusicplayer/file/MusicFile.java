package com.keepingrack.tagmusicplayer.file;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.Variable;
import com.keepingrack.tagmusicplayer.bean.MusicItem;
import com.keepingrack.tagmusicplayer.db.entity.MusicTagsRecord;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.keepingrack.tagmusicplayer.MainActivity.BASE_DIR;
import static com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper.SEPARATE;
import static com.keepingrack.tagmusicplayer.util.ListUtil.*;

public class MusicFile {

    public static final String MUSIC_FILE_EXTENSION = ".mp3";
    public static final String TAG_NOTHING = "タグなし";
    public static final List<String> TAG_NOTHING_LIST = Arrays.asList(TAG_NOTHING);

    private Map<String, MusicTagsRecord> musicTagsRecordMap;
    private MainActivity activity;

    public MusicFile (MainActivity _activity) {
        this.activity = _activity;
    }

    public void readMusicFilesAndDatabase() throws Exception {
        Variable.clearTagKinds();
        Variable.clearMusicKeys();
        Variable.clearMusicItems();
        musicTagsRecordMap = activity.musicTagsLogic.selectAllRecords();
        getMusicFiles(BASE_DIR);
    }

    // ストレージから楽曲ファイルを取得
    private void getMusicFiles(String dirPath) throws Exception {
        File[] files = new File(dirPath).listFiles();
        Arrays.sort(files, sortListFilesRule());
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getMusicFiles(file.getAbsolutePath());
                } else {
                    if (file.getName().endsWith(MUSIC_FILE_EXTENSION)) {
                        String key = getFileKey(file);
                        if (Variable.getMusicKeys().contains(key)) { continue; } // 同一楽曲はスルー
                        List<String> tags = getTagsForFile(file, key);
                        Variable.addTagKinds(tags);
                        Variable.addMusicKeys(key);
                        Variable.putMusicItems(key, new MusicItem(file.getAbsolutePath(), file.getName(), tags, null));
                    }
                }
            }
        }
    }

    // ファイルリストのソートルール設定
    // ファイル種別はディレクトリ > ファイルの順。種別が同じ場合はファイル名の昇順
    private Comparator<File> sortListFilesRule() {
        return new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                int sortResult = 0;
                if (o1.isDirectory() && o2.isFile()) {
                    sortResult = 1;
                } else if (o1.isFile() && o2.isDirectory()) {
                    sortResult = -1;
                } else if ((o1.isDirectory() && o2.isDirectory()) || (o1.isFile() && o2.isFile())) {
                    sortResult = o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
                }
                return sortResult;
            }
        };
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

    private List<String> getTagsForFile(File file, String key) {
        List<String> tags = getTagsFromRecord(key);
        if (tags.isEmpty() || isTagNothingList(tags)) {
            tags = getTagsFromFilePath(file.getAbsolutePath());
        }
        if (tags.isEmpty()) {
            tags = TAG_NOTHING_LIST;
        }
        return tags;
    }

    private List<String> getTagsFromRecord(String key) {
        List<String> tags = new ArrayList<>();
        MusicTagsRecord musicTagsRecord = musicTagsRecordMap.get(key);
        if (musicTagsRecord != null) {
            tags = stringToList(musicTagsRecord.getTags(), SEPARATE);
        }
        return tags;
    }

    private boolean isTagNothingList(List<String> tags) {
        boolean result = false;
        for (String tag : tags) {
            if (TAG_NOTHING.equals(tag)) {
                result = true;
            }
        }
        return result;
    }

    private List<String> getTagsFromFilePath(String filePath) {
        List<String> tags = new ArrayList<>();
        String relativePath = filePath.split(BASE_DIR)[1];
        List<String> dirNames = Arrays.asList(relativePath.split("/"));
        for (String dirName : dirNames) {
            if (!dirName.isEmpty() && !dirName.equals(last(dirNames))) {
                tags.add(dirName);
            }
        }
        return tags;
    }
}
