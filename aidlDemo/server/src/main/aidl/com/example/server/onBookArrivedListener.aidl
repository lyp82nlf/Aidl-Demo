// onBookArrivedListener.aidl
package com.example.server;

// Declare any non-default types here with import statements

import com.example.server.Book;

interface onBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
      void onBookArrived(in Book book);
}
