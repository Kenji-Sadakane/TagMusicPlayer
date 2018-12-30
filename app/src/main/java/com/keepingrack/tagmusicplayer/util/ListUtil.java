package com.keepingrack.tagmusicplayer.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {
    // リストの末尾の要素を返却
    public static <E> E last(List<E> list) {
        E result = null;
        if (list != null && list.size() > 0) {
            result = list.get(list.size() - 1);
        }
        return result;
    }

    // リストの最後尾に要素を追加。
    // 要素数がmax値に達していたら、最初に追加した要素を削除する
    public static <E> List<E> lastInFirstOut(List<E> list, E elm, int max) {
        if (list != null) {
            if (list.size() >= max) {
                list.remove(0);
            }
            list.add(elm);
        }
        return list;
    }

    // リストの指定された部分を返却
    public static <E> List<E> subList(List<E> list, int fromIdx, int toIdx) {
        List<E> result = new ArrayList<E>();
        for (int i = 0; i < list.size(); i++) {
            E elm = list.get(i);
            if (fromIdx <= i && i < toIdx) {
                result.add(elm);
            }
        }
        return result;
    }

    // リストの最後の要素のみ保持するリストを返却
    public static <E> List<E> lastOnly(List<E> list) {
        return subList(list, list.size() - 1, list.size());
    }

    public static List<String> removeEmptyItem(List<String> list) {
        List<String> result = new ArrayList<>();
        for (String item : list) {
            if (item != null && !item.isEmpty()) {
                result.add(item);
            }
        }
        return result;
    }
}
