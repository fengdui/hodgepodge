package com.hodgepodge.framework.mail;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * 字节url
 */
public class ByteURL {

    public URL getResource(String name, byte[] data) {
        try {
            return new URL(null, "bytes:///" + name, new BytesHandler(data));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @Getter
    @Setter
    class BytesHandler extends URLStreamHandler {
        private byte[] data;

        public BytesHandler(byte[] data) {
            this.data = data;
        }

        @Override
        protected URLConnection openConnection(URL u) {
            return new ByteUrlConnection(u, data);
        }
    }

    @Getter
    @Setter
    class ByteUrlConnection extends URLConnection {

        private byte[] data;

        public ByteUrlConnection(URL url, byte[] data) {
            super(url);
            this.data = data;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }
    }
}
