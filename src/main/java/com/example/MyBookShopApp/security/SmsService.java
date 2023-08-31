package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.repo.SmsCodeRepository;
import com.example.MyBookShopApp.errs.BadRequestException;
import com.example.MyBookShopApp.security.telegram.bot.JsonResponseSms;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class SmsService {

    private Logger logger = Logger.getLogger(this.getClass().getName());

  private final SmsCodeRepository smsCodeRepository;

    @Autowired
  private OkHttpClient okHttpClient;

    private String generateRandomSMSCode;

    @Value("${smsru.api_id}")
    private String id;

    @Autowired
    public SmsService(SmsCodeRepository smsCodeRepository) {
        this.smsCodeRepository = smsCodeRepository;
    }

    public String sendSecretCodeSms(String contact) throws URISyntaxException, BadRequestException {

        String formattedContact = contact.replaceAll("[( )-]", "");

        // далее подключаем сервис отправки СМС c кодом.  2 варианта отправки:

        // 1. Звонок на телефон, последние 4 цифры номера = код
        Request request = new Request.Builder()
                .url("https://sms.ru/code/call?phone=" + formattedContact +"&ip=33.22.11.55&api_id="+id)
//                .url("https://sms.ru/code/call?phone=79012876440&ip=33.22.11.55&api_id=6361EF47-6713-E7CB-5AE5-91FAE11626C9")
                .build();
        try {
            double callPrice = 0.4;
            StringBuilder sb = new StringBuilder();
            ObjectMapper mapper = new ObjectMapper();
            Response responseHttpClient = okHttpClient.newCall(request).execute();
            ResponseBody body = responseHttpClient.body();
            if (body != null) {
                String jsonString = body.string(); // сырой JSON строки ответа
//            String jsonString = "{ \"status\": \"OK\",\"code\": 9175,\"call_id\": \"202330-1000010\",\"balance\": 0.85,\"cost\": 0.4 }";

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
                logger.info("Ошибка звонка на телефон для передачи кода подтверждения (используем автогенерацию). Статус - " + status);

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
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        while (sb.length()<size){
            sb.append(random.nextInt(9));
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
        SmsCode smsCode = smsCodeRepository.findSmsCodeByCode(code);
        return (smsCode != null && !smsCode.isExpired());
    }

    public String getGenerateRandomSMSCode() {
        return generateRandomSMSCode;
    }
}
