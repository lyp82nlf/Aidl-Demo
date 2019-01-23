package com.example.dsg.aidldemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.server.Book;
import com.example.server.IBookManager;
import com.example.server.onBookArrivedListener;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindView(R.id.btn_bind)
    Button btnBind;
    @BindView(R.id.btn_check)
    Button btnCheck;
    private IBookManager mIBookManager;
    private BookArrivedListener bookArrivedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bookArrivedListener = new BookArrivedListener();
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("main1","onServiceConnected");
            mIBookManager = IBookManager.Stub.asInterface(service);
            try {
                mIBookManager.registerBookArrivedListener(bookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("main1","onServiceDisconnected");

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mIBookManager.unRegisterBookArrivedListener(bookArrivedListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
    }

    public class BookArrivedListener extends com.example.server.onBookArrivedListener.Stub {

        @Override
        public void onBookArrived(Book book) throws RemoteException {
            Log.e("main1", book.getBookID() + "   name:" + book.getBookName());
        }
    }


    private int i = 0;

    @OnClick({R.id.btn_add, R.id.btn_bind, R.id.btn_check})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                i++;
                Book book = new Book(i, "书籍" + i);
                try {
                    mIBookManager.addBook(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_bind:
                Intent intent = new Intent();
                intent.setAction("com.example.server.remoteService");
                intent.setPackage("com.example.server");
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                break;
            case R.id.btn_check:
                try {
                    List<Book> bookList = mIBookManager.getBookList();
                    for (Book book1 : bookList) {
                        Log.e("main1", book1.getBookName() + "___" + book1.getBookID());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
