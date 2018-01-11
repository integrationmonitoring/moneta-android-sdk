package ru.integrationmonitoring.monetasdkapp;

import android.content.Context;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;

/**
 * Created by dmo on 13.04.2016.
 */
public class MonetaSdk {
    private static final String TAG = "Response";

    private final WebView webView;
    private final String mntAcountId;
    private final String mntAcountCode;
    private final String mntTestMode;
    private final MonetaSdkConfig sdkConfig;
    private final String mntUrl;

    /**
     * @param webView WebView для отображения страницы
     * @param context контекст приложения
     */
    public MonetaSdk(WebView webView, Context context) {
        this.webView = webView;
        sdkConfig = new MonetaSdkConfig();
        sdkConfig.load(context);

        mntAcountId = sdkConfig.get("monetasdk_account_id");
        mntAcountCode = sdkConfig.get("monetasdk_account_code");
        mntTestMode = sdkConfig.get("monetasdk_test_mode");


        if (sdkConfig.get("monetasdk_demo_mode").equals("1")) {
            mntUrl = sdkConfig.get("monetasdk_demo_url");
        } else {
            mntUrl = sdkConfig.get("monetasdk_production_url");
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(50);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webView.setWebViewClient(new WebViewClient());
    }

    /**
     * Вспомогательная функция для создания случайного номера заказа
     *
     * @return строковое представление сгенерированного случайного номера
     */
    public static String getOrderId() {
        Long tsLong = System.currentTimeMillis();
        Random r = new Random();
        int rnd = r.nextInt(99 - 10 + 1) + 10;

        return tsLong.toString() + Integer.toString(rnd);
    }

    /**
     * Генерирует строковое представление хэша MD5
     *
     * @param s строка для подсчета хэша
     * @return строковое представление хэша
     */
    private String md5(final String s) {
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
                hexString.append(String.format("%02X", 0xFF & aMessageDigest));
            }
            result = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Открывает страницу оплаты
     *
     * @param mntOrderId       Идентификатор, по которому магазин сможет понять, что это данные именно этого заказа
     * @param mntAmount        Сумма оплаты
     * @param mntCurrency      Валюта платежа
     * @param mntPaymentSystem Платежная система
     */
    public void showPaymentFrom(String mntOrderId, Double mntAmount, Currency mntCurrency, String mntPaymentSystem) {
        String mntPaymentSystemAccountId = sdkConfig.get(mntPaymentSystem + "_accountId");
        String mntPaymentSystemUnitId = sdkConfig.get(mntPaymentSystem + "_unitId");
        String mntWidgLink = sdkConfig.get("monetasdk_assistant_widget_link");

        String mntAmountString = String.format(Locale.ROOT, "%.2f", mntAmount);

        String queryString = mntUrl + mntWidgLink +
                "?MNT_ID=" + mntAcountId +
                "&MNT_TRANSACTION_ID=" + mntOrderId +
                "&MNT_CURRENCY_CODE=" + mntCurrency.toString() +
                "&MNT_AMOUNT=" + mntAmountString +
                "&followup=true&javascriptEnabled=true&payment_method=" + mntPaymentSystem +
                "&paymentSystem.unitId=" + mntPaymentSystemUnitId +
                "&paymentSystem.limitIds=" + mntPaymentSystemUnitId +
                "&paymentSystem.accountId=" + mntPaymentSystemAccountId +
                "&MNT_TEST_MODE=" + mntTestMode;

        if (!mntAcountCode.equals("")) {
            queryString = queryString + "&MNT_SIGNATURE=" +
                    md5(mntAcountId + mntOrderId + mntAmountString +
                            mntCurrency + mntTestMode + mntAcountCode);
        }

        // queryString
        Log.e(TAG, "DBG_showPaymentFrom: " + queryString);

        webView.loadUrl(queryString);
    }

    /**
     * Открывает страницу оплаты с последующей привязкой карты
     *
     * @param mntOrderId             Идентификатор, по которому магазин сможет понять, что это данные именно этого заказа
     * @param mntAmount              Сумма оплаты
     * @param mntCurrency            Валюта платежа
     * @param mntPaymentSystem       Платежная система
     * @param publicId               Значение из личного кабинета (Мой счет -> Безопасность -> Публичный идентификатор)
     * @param cardNumberRequired     флаг, указывающий, что номер карты нужен на форме оплаты
     * @param cardExpirationRequired флаг, указывающий, что срок действия карты нужен на форме  оплаты
     * @param cardCVV2Required       флаг, указывающий, что CVV2 карты нужен на форме оплаты
     */
    public void showPaymentFormAndSaveCard(String mntOrderId,
                                           Double mntAmount,
                                           String mntCurrency,
                                           String mntPaymentSystem,
                                           String publicId,
                                           boolean cardNumberRequired,
                                           boolean cardExpirationRequired,
                                           boolean cardCVV2Required) {

        String mntPaymentSystemAccountId = sdkConfig.get(mntPaymentSystem + "_accoundId");
        String mntPaymentSystemUnitId = sdkConfig.get(mntPaymentSystem + "_unitId");
        String mntSCDLink = sdkConfig.get("monetasdk_secure_card_data_link");

        String mntAmountString = String.format(Locale.ROOT, "%.2f", mntAmount);

        String queryString = mntUrl + mntSCDLink +
                "?MNT_ID=" + mntAcountId +
                "&MNT_TRANSACTION_ID=" + mntOrderId +
                "&MNT_CURRENCY_CODE=" + mntCurrency.toString() +
                "&MNT_AMOUNT=" + mntAmountString +
                "&followup=true&javascriptEnabled=true&payment_method=" + mntPaymentSystem +
                "&paymentSystem.unitId=" + mntPaymentSystemUnitId +
                "&paymentSystem.limitIds=" + mntPaymentSystemUnitId +
                "&paymentSystem.accountId=" + mntPaymentSystemAccountId +
                "&MNT_TEST_MODE=" + mntTestMode +
                "&publicId=" + publicId +
                "&secure[CARDNUMBER]=" + (cardNumberRequired ? "required" : "") +
                "&secure[CARDEXPIRATION]=" + (cardExpirationRequired ? "required" : "") +
                "&secure[CARDCVV2]=" + (cardCVV2Required ? "required" : "");

        if (!mntAcountCode.equals("")) {
            queryString += "&MNT_SIGNATURE=" +
                    md5(mntAcountId + mntOrderId + mntAmountString +
                            mntCurrency + mntTestMode + mntAcountCode);
        }

        Log.e(TAG, "DBG_showPaymentFormAndSaveCard: " + queryString);

        webView.loadUrl(queryString);
    }

    /**
     * Открывает страницу оплаты с последующей привязкой карты
     *
     * @param mntOrderId       Идентификатор, по которому магазин сможет понять, что это данные именно этого заказа
     * @param mntAmount        Сумма оплаты
     * @param mntCurrency      Валюта платежа
     * @param mntPaymentSystem Платежная система
     * @param publicId         Значение из личного кабинета (Мой счет -> Безопасность -> Публичный идентификатор)
     */
    public void showPaymentFormAndSaveCard(String mntOrderId,
                                           Double mntAmount,
                                           String mntCurrency,
                                           String mntPaymentSystem,
                                           String publicId) {
        showPaymentFormAndSaveCard(mntOrderId, mntAmount, mntCurrency,
                mntPaymentSystem, publicId, true, true, true);
    }

    public enum Currency {
        RUB, EUR, USD;
    }
}
