package com.innohawk.dan.activitymaps;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCRedirectException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketException;
import java.net.URI;
import java.util.Map;

/*
 * Example:
 *
 *      BxConnector o = new BxConnector ("http://192.168.1.64/d700/xmlrpc/", "test", "123456");
 *
 *      Object[] aParams = {
 *      		"123",
 *      		"456",
 *      };
 *
 *      o.execAsyncMethod("dolphin.concat", aParams, new BxConnector.Callback() {
 *			public void callFinished(Object result) {
 *				t.setText (result.toString());
 *			}
 *      });
 *
 *
 *
 */
public class ConnectorMaps extends Object implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String TAG = "OO Connector";
    transient protected XMLRPCClient m_oClient;
    protected String m_sUrl_Maps;
    protected int m_iMemberId_Maps;
    protected String m_sUsername_Maps;
    protected String m_sPwd_Maps;
    protected String m_sPwdClear_Maps;
    protected int m_iProtocolVer_Maps;
    protected Context m_context_Maps;

    public ConnectorMaps(String sUrl, String sUsername, String sPwd, int iMemberId) {
        m_sUrl_Maps = sUrl;
        m_iMemberId_Maps = iMemberId;
        m_sUsername_Maps = sUsername;
        m_sPwdClear_Maps = sPwd;
        m_sPwd_Maps = sPwd;
        m_iProtocolVer_Maps = 2;
        m_oClient = new XMLRPCClient(URI.create(m_sUrl_Maps));
    }

    public String execAsyncMethodMyCoordenates(String sMethod, Object[] aParams, Callback oCallBack, Context context) {
        this.m_context_Maps = context;

        //setLoadingIndicator(false);

        XMLRPCMethodMaps method = new XMLRPCMethodMaps(sMethod, oCallBack);
        method.call(aParams);
        return sMethod;
    }

    public static class Callback {
        public void callFinished(Object result) {
        }

        public boolean callFailed(Exception e) {
            Log.e(TAG, "Exception: " + e.toString());
            return true;
        }
    }

    class XMLRPCMethodMaps extends Thread {
        private int redirectsCount = 0;
        private String method;
        private Object[] params;
        private Handler handler;
        private Callback callBack;

        public XMLRPCMethodMaps(String method, Callback callBack) {
            this.method = method;
            this.callBack = callBack;
            handler = new Handler();
        }

        public void call() {
            call(null);
        }

        public void call(Object[] params) {
            this.params = params;
            start();
        }

        @Override
        public void run() {

            try {

                boolean isRepeatLoop;
                do {

                    isRepeatLoop = false;
                    try {

                        final long t0 = System.currentTimeMillis();
                        final Object result = m_oClient.call(method, params);
                        final long t1 = System.currentTimeMillis();
                        handler.post(new Runnable() {
                            @SuppressWarnings("unchecked")
                            public void run() {
                                Log.i(TAG, "XML-RPC call took " + (t1 - t0) + "ms");
                                //setLoadingIndicator(false);

                                if ((result instanceof Map) && null != ((Map<String, String>) result).get("error")) {
                                    // Builder builder = new AlertDialog.Builder(m_context);
                                    // builder.setTitle(m_context.getResources().getString(R.string.error));
                                    // builder.setMessage(((Map<String, String>) result).get("error"));
                                    // builder.setNegativeButton(m_context.getResources().getString(R.string.close), null);
                                    // builder.show();
                                } else {
                                    callBack.callFinished(result);
                                }
                            }
                        });

                    } catch (final XMLRPCRedirectException e) {

                        if (++redirectsCount < 4) {
                            m_sUrl_Maps = e.getRedirectUrl();
                            Log.i(TAG, "Redirect: " + m_sUrl_Maps);
                            m_oClient = new XMLRPCClient(URI.create(m_sUrl_Maps));
                            isRepeatLoop = true;
                        } else {
                            //throw new Exception("Redirection limit exceeded");
                        }

                    }

                } while (isRepeatLoop);

            } catch (final SocketException e) {

                Log.e(TAG, "SOCKET ERROR:" + e.toString());
                //setLoadingIndicator(false);

            } catch (final Exception e) {

                handler.post(new Runnable() {
                    public void run() {
                        Log.e(TAG, "Error: " + e.getMessage());
                        //setLoadingIndicator(false);

                        if (callBack.callFailed(e)) {
                            // Builder builder = new AlertDialog.Builder(m_context);
                            //  builder.setTitle(R.string.exception);
                            // builder.setMessage(e.getMessage());
                            // builder.setNegativeButton(R.string.close, null);
                            // builder.show();
                        }
                    }
                });

            } catch (final OutOfMemoryError e) {

                handler.post(new Runnable() {
                    public void run() {
                        Log.e(TAG, "Error: " + e.getMessage());
                        //setLoadingIndicator(false);

                        //if (callBack.callFailed(new Exception(e.getMessage()))) {
                        //  Builder builder = new AlertDialog.Builder(m_context);
                        //  builder.setTitle(R.string.exception_out_of_memory);
                        //  builder.setMessage(e.getMessage());
                        // builder.setNegativeButton(R.string.close, null);
                        // builder.show();
                        // }
                    }
                });

            }
        }


    }

    public String getUsername() {
        return m_sUsername_Maps;
    }

    public String getPassword() {
        return m_sPwd_Maps;
    }

    public int getProtocolVer() {
        return m_iProtocolVer_Maps;
    }

    public int getMemberId() {
        return m_iMemberId_Maps;
    }


    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        m_oClient = new XMLRPCClient(URI.create(m_sUrl_Maps));
    }

}
