package ru.integrationmonitoring.monetasdkapp;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by dmo on 13.04.2016.
 */
public class MonetaSdk {

    String TAG = "Response";

    public void showPaymentFrom(String mntOrderId, Double mntAmount, String mntCurrency, String mntPaymentSystem, WebView WbView, Context context) {

        MonetaSdkConfig sdkConfig = new MonetaSdkConfig();
        sdkConfig.load(context);

        String mntPaymentSystemAccountId = sdkConfig.get(mntPaymentSystem + "_accountId");
        String mntPaymentSystemUnitId    = sdkConfig.get(mntPaymentSystem + "_unitId");
        String mntAcountId   = sdkConfig.get("monetasdk_account_id");
        String mntAcountCode = sdkConfig.get("monetasdk_account_code");
        String mntDemoMode   = sdkConfig.get("monetasdk_demo_mode");
        String mntTestMode   = sdkConfig.get("monetasdk_test_mode");
        String mntDemoUrl    = sdkConfig.get("monetasdk_demo_url");
        String mntProdUrl    = sdkConfig.get("monetasdk_production_url");
        String mntWidgLink   = sdkConfig.get("monetasdk_assistant_widget_link");

        String mntAmountString = String.format("%.2f", mntAmount).replace(",", ".");
        String mntWidgUrl = (mntDemoMode.equals("1")) ? mntDemoUrl : mntProdUrl;
        mntWidgUrl = mntWidgUrl + mntWidgLink;

        String queryString = mntWidgUrl + "?MNT_ID=" + mntAcountId + "&MNT_TRANSACTION_ID=" + mntOrderId + "&MNT_CURRENCY_CODE=" + mntCurrency
                            + "&MNT_AMOUNT=" + mntAmountString + "&followup=true&javascriptEnabled=true&payment_method=" + mntPaymentSystem
                            + "&paymentSystem.unitId=" + mntPaymentSystemUnitId + "&paymentSystem.limitIds=" + mntPaymentSystemUnitId
                            + "&paymentSystem.accountId=" + mntPaymentSystemAccountId + "&MNT_TEST_MODE=" + mntTestMode;

        if (!mntAcountCode.equals("")) {
            queryString = queryString + "&MNT_SIGNATURE=" + md5(mntAcountId + mntOrderId + mntAmountString + mntCurrency + mntTestMode + mntAcountCode);
        }

        // queryString
        Log.e(TAG, "DBG_showPaymentFrom: " + queryString);

        WbView.getSettings().setJavaScriptEnabled(true);
        WbView.getSettings().setDisplayZoomControls(true);
        WbView.getSettings().setLoadWithOverviewMode(true);
        WbView.getSettings().setUseWideViewPort(true);
        WbView.setInitialScale(50);
        WbView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        WbView.setWebViewClient(new WebViewClient());

        WbView.loadUrl(queryString);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        String result = "";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            result = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getOrderId() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString() + this.getRandom(10, 99);
        return ts;
    }

    public String getRandom(int min, int max) {
        Random r = new Random();
        int rnd = r.nextInt(max - min + 1) + min;
        return Integer.toString(rnd);
    }

    private static MonetaSdk ourInstance = new MonetaSdk();

    public static MonetaSdk getInstance() {
        return ourInstance;
    }

    public MonetaSdk() {
    }

}
