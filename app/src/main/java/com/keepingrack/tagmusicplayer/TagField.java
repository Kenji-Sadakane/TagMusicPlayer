package com.keepingrack.tagmusicplayer;

import android.text.Layout;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.DISPLAY_WIDTH;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;

public class TagField {

    private MainActivity activity;
    private int prevTagId = -1;
    private int allTagLength = 0;
    private RelativeLayout tagField;

    public TagField(MainActivity _activity) {
        this.activity = _activity;
    }

    public RelativeLayout createTagField(String key) {
        tagField = new RelativeLayout(activity);
        setTagTextToTagField(key);
        return tagField;
    }

    public LinearLayout.LayoutParams createTagFieldParams() {
        LinearLayout.LayoutParams tagFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagFieldParams.setMargins(20, 0, 20, 20); // 左, 上, 右, 下
        return tagFieldParams;
    }

    private void setTagTextToTagField(String key) {
        List<String> tags = musicItems.get(key).getTags();
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                TextView tagText = createTagTextView(tag);
                tagField.addView(tagText);
                allTagLength += Layout.getDesiredWidth(tagText.getText(), tagText.getPaint()) + 30; // 30=padding(左)+padding(右)+margin(左)+margin(右)
                tagText.setLayoutParams(createTagTextLayoutParams());
                prevTagId = tagText.getId();
                resetAllTagLength();
            }
        }
    }

    private TextView createTagTextView(String tag) {
        TextView tagText = new TextView(activity);
        tagText.setId(View.generateViewId());
        tagText.setText(tag);
        tagText.setPadding(10, 0, 10, 0); // 左, 上, 右, 下
        tagText.setBackgroundResource(R.drawable.tag_item);
        tagText.setClickable(true);
        tagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = ((TextView) v).getText().toString();
                ((Switch) activity.findViewById(R.id.switch1)).setChecked(true);
                ((AutoCompleteTextView) activity.findViewById(R.id.editText)).setText(tag);
                activity.keyWord.execSearch();
            }
        });
        return tagText;
    }

    private RelativeLayout.LayoutParams createTagTextLayoutParams() {
        RelativeLayout.LayoutParams tagTextParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(10, 0, 0, 15); // 左, 上, 右, 下
        if (tagField.getChildCount() != 0) {
            if (allTagLength > DISPLAY_WIDTH - 40) { // 40=tagFieldのmargin(左)+margin(右)
                tagTextParams.addRule(RelativeLayout.BELOW, prevTagId);
            } else {
                tagTextParams.addRule(RelativeLayout.ALIGN_TOP, prevTagId);
                tagTextParams.addRule(RelativeLayout.RIGHT_OF, prevTagId);
            }
        }
        return tagTextParams;
    }

    private void resetAllTagLength() {
        if (tagField.getChildCount() != 0) {
            if (allTagLength > DISPLAY_WIDTH - 40) { // 40=tagFieldのmargin(左)+margin(右)
                allTagLength = 0;
            }
        }
    }
}
