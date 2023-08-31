package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.struct.book.book.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class PaymentServise {

    @Value("${robokassa.merchant.login}")
    private String merchantLogin;

    @Value("${robokass.pass.first.test}")
    private String firstTestPass;

    public String getPaymentUrl(String sum, String userHash) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((merchantLogin + ":" + sum + ":" + userHash + ":" + firstTestPass).getBytes());
        String signatureValue= DatatypeConverter.printHexBinary(md.digest()).toUpperCase();

        System.out.println("createSignatureValue="+ signatureValue); //

        String REQUEST_URL = "https://auth.robokassa.ru/Merchant/Index.aspx" +
                "?MerchantLogin=" + merchantLogin +
                "&InvId=" + userHash +
                "&Culture=ru" +
                "&Encoding=utf-8" +
                "&OutSum=" + sum +
                "&SignatureValue=" + signatureValue +
                "&IsTest=1";

        return REQUEST_URL;
    }

    public String createSignatureValue(String sum, String userHash) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((sum + ":" + userHash + ":" + firstTestPass).getBytes());
        return DatatypeConverter.printHexBinary(md.digest()).toUpperCase();
    }

}
