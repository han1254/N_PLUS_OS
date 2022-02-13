package com.neuqer.n_plus_os.memory.thread_factory.thread;

/**
 * Time:2020/12/21 10:18
 * Author: han1254
 * Email: 1254763408@qq.com
 * Function:
 */
public class OpenFileMsgObj {
    private char[] content;
    private int maxPage;
    private int page;
    private int itemPosition;
    private int throwOutMemPage;
    private int[] allFileBlockAddress;

    public OpenFileMsgObj(char[] content, int maxPage, int page, int itemPosition) {
        this.content = content;
        this.maxPage = maxPage;
        this.page = page;
        this.itemPosition = itemPosition;
    }

    public char[] getContent() {
        return content;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public int getPage() {
        return page;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setThrowOutMemPage(int throwOutMemPage) {
        this.throwOutMemPage = throwOutMemPage;
    }

    public int getThrowOutMemPage() {
        return throwOutMemPage;
    }

    public int[] getAllFileBlockAddress() {
        return allFileBlockAddress;
    }
}
