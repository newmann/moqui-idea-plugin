package org.moqui.idea.plugin.action.webTool;

import com.intellij.openapi.diagnostic.Logger;
import org.cef.callback.CefCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.misc.IntRef;
import org.cef.misc.StringRef;
import org.cef.network.CefResponse;

import java.io.InputStream;
import java.net.URLConnection;

public class WebviewOpenedConnection implements WebviewResourceState{
    private static final Logger LOGGER = Logger.getInstance(WebviewOpenedConnection.class);

    private final URLConnection connection;

    private InputStream inputStream;

    public WebviewOpenedConnection(URLConnection connection) {
        this.connection = connection;
        try {
            this.inputStream = connection.getInputStream();
        } catch (Exception exception) {
            LOGGER.warn(exception);
//            System.out.println(exception);
        }
    }

    @Override
    public void getResponseHeaders(CefResponse cefResponse, IntRef responseLength, StringRef redirectUrl) {
        try {
            String url = this.connection.getURL().toString();
            cefResponse.setMimeType(this.connection.getContentType());
            try {
                responseLength.set(this.inputStream.available());
                cefResponse.setStatus(200);
            } catch (Exception e) {
                cefResponse.setError(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND);
                cefResponse.setStatusText(e.getLocalizedMessage());
                cefResponse.setStatus(404);
            }
        } catch (Exception e) {
            LOGGER.warn("Exception");
            cefResponse.setError(CefLoadHandler.ErrorCode.ERR_FILE_NOT_FOUND);
            cefResponse.setStatusText(e.getLocalizedMessage());
            cefResponse.setStatus(404);
        }
    }

    @Override
    public boolean readResponse(byte[] dataOut, int designedBytesToRead, IntRef bytesRead, CefCallback callback) {
        try {
            int availableSize = this.inputStream.available();
            if (availableSize > 0) {
                int maxBytesToRead = Math.min(availableSize, designedBytesToRead);
                int realNumberOfReadBytes = this.inputStream.read(dataOut, 0, maxBytesToRead);
                bytesRead.set(realNumberOfReadBytes);
                return true;
            }
        } catch (Exception exception) {
            LOGGER.warn(exception);
        } finally {
            this.close();
        }
        return false;
    }

    @Override
    public void close() {
        try {
            if (this.inputStream != null)
                this.inputStream.close();
        } catch (Exception exception) {
            LOGGER.warn(exception);
        }
    }
}
