package com.example.MyBookShopApp.data.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PaymentServise {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokass.pass.first.test}")
    private String firstTestPass;

    public String getPaymentUrl(String sum, String userHash) throws NoSuchAlgorithmException {

        var md = MessageDigest.getInstance("MD5");
        md.update((merchantLogin + ":" + sum + ":" + userHash + ":" + firstTestPass).getBytes());
        String signatureValue= DatatypeConverter.printHexBinary(md.digest()).toUpperCase();

        return  "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + userHash +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + sum +
                "&SignatureValue=" + signatureValue +
                "&IsTest=1";
    }

    public String createSignatureValue(String sum, String userHash) throws NoSuchAlgorithmException {
        var md = MessageDigest.getInstance("MD5");
        md.update((sum + ":" + userHash + ":" + firstTestPass).getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
    }

}
