// IBookManager.aidl
package com.example.server;

// Declare any non-default types here with import statements

import com.example.server.Book;
import com.example.server.onBookArrivedListener;

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<Book> getBookList();


    void addBook(in Book book);


    void registerBookArrivedListener(onBookArrivedListener listener);

    void unRegisterBookArrivedListener(onBookArrivedListener listener);

}
