package com.example.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoteService extends Service {

    private List<Book> bookList;
    private RemoteCallbackList<onBookArrivedListener> remoteCallbackList;
    private AtomicBoolean isServiceFinish = new AtomicBoolean(false);

    public RemoteService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("main1","onCreate");
        bookList = new ArrayList<>();
        remoteCallbackList = new RemoteCallbackList<>();
        new Thread(onBookArrivedNotify).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceFinish.set(true);
    }

    private Runnable onBookArrivedNotify = new Runnable() {
        @Override
        public void run() {
            while (!isServiceFinish.get()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int N = bookList.size();
                Book book = new Book(N + 1, "书籍#" + (N + 1));
                bookList.add(book);
                onNewBookArrived(book);
            }


        }
    };

    private void onNewBookArrived(Book book) {
        Log.e("main1", "启动推送");
        int N = remoteCallbackList.beginBroadcast();

        for (int i = 0; i < N; i++) {
            onBookArrivedListener onBookArrivedListener = remoteCallbackList.getBroadcastItem(i);
            if (onBookArrivedListener != null) {
                try {
                    onBookArrivedListener.onBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }


        remoteCallbackList.finishBroadcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new BookBinder();
    }


    private class BookBinder extends IBookManager.Stub {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            bookList.add(book);
        }

        @Override
        public void registerBookArrivedListener(onBookArrivedListener listener) throws RemoteException {
            remoteCallbackList.register(listener);
            Log.e("main1", "注册");

        }

        @Override
        public void unRegisterBookArrivedListener(onBookArrivedListener listener) throws RemoteException {
            remoteCallbackList.unregister(listener);
            Log.e("main1", "取消注册");

        }
    }


}
