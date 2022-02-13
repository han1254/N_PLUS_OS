package com.neuqer.n_plus_os.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NowString {
    public static String getCurrentTime() {
          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
          return df.format(new Date());
    }
}