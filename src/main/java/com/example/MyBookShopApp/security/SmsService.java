package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.repo.SmsCodeRepository;
import com.example.MyBookShopApp.security.telegram.bot.JsonResponseSms;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class SmsService {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Random rand = SecureRandom.getInstanceStrong();

  private final SmsCodeRepository smsCodeRepository;

    @Autowired
  private OkHttpClient okHttpClient;

    private String generateRandomSMSCode;

    @Value("${smsru.api_id}")
    private String id;

    @Autowired
    public SmsService(SmsCodeRepository smsCodeRepository) throws NoSuchAlgorithmException {
        this.smsCodeRepository = smsCodeRepository;
    }

    public String sendSecretCodeSms(String contact) {

        String formattedContact = contact.replaceAll("[( )-]", "");

        // 1. Звонок на телефон, последние 4 цифры номера = код
        var request = new Request.Builder()
                .url("https://sms.ru/code/call?phone=" + formattedContact +"&ip=33.22.11.55&api_id="+id)
                .build();
        try {
            var callPrice = 0.4;
            var sb = new StringBuilder();
            var mapper = new ObjectMapper();
            var responseHttpClient = okHttpClient.newCall(request).execute();
            ResponseBody body = responseHttpClient.body();
            if (body != null) {
                var jsonString = body.string();
            logger.info("От СМС сервера получен ответ: " + jsonString);
            JsonResponseSms response = mapper.readValue(jsonString, JsonResponseSms.class);
            String status = response.getStatus();

            if (status.equals("OK")) {
                sb.append(response.getCode());
                sb.insert(2," ");
                generateRandomSMSCode = sb.toString();
                double balance = response.getBalance();
                callPrice = response.getCost();

                logger.info("Ответ при отправке звонка на телефон для передачи кода подтверждения: "
                        + jsonString + "\n" +"Код = " + generateRandomSMSCode + "\n" +"Ваш баланс до этого звонка был = " + balance);
            if (balance < callPrice) {
                logger.info("У вас не достаточно средств на балансе - " + balance);
            }
        } else {
                // 2. Перейдите в Telegram http://t.me/BookshopApp_confirm_user_bot

                generateRandomSMSCode = generateRandomSMSCode(4);

                logger.info("Для контакта " + formattedContact + " код подтверждения отправлен в Telegram - " + generateRandomSMSCode);
            }
            }
        } catch (Exception e) {

            // 2. Перейдите в Telegram http://t.me/BookshopApp_confirm_user_bot
            logger.info("Ошибка звонка на телефон для передачи кода подтверждения (используем автогенерацию) - " + e);

            generateRandomSMSCode = generateRandomSMSCode(4);

            logger.info("Для контакта " + formattedContact + " код подтверждения отправлен в Telegram - " + generateRandomSMSCode);
        }
        return generateRandomSMSCode;
    }

    public String generateRandomSMSCode(int size) {
        var sb = new StringBuilder();

        while (sb.length()<size){
            sb.append(rand.nextInt(9));
        }
        sb.insert(2," ");

        return sb.toString();
    }


    public void saveSmsCode(SmsCode smsCode){
        if (smsCodeRepository.findSmsCodeByCode(smsCode.getCode()) == null) {
            smsCodeRepository.save(smsCode);
        }
    }

    public boolean verifySmsCode(String code){
        var smsCode = smsCodeRepository.findSmsCodeByCode(code);
        return (smsCode != null && !smsCode.isExpired());
    }

    public String getGenerateRandomSMSCode() {
        return generateRandomSMSCode;
    }
}
