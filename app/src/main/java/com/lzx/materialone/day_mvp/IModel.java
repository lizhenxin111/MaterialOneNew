package com.lzx.materialone.day_mvp;

import com.lzx.materialone.Bean.data.common_item.BasicData;
import com.lzx.materialone.Bean.data.common_item.ItemData;

import java.util.List;

/**
 * Created by lizhe on 2017/6/4.
 */

public interface IModel {
    List<ItemData> getItemDataList();
    BasicData getBasicData();
    String getContentJson();
}
