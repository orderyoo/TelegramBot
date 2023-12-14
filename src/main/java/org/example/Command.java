package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Command {
    private static String line;
    public static String hollyToDay() throws IOException {

        String stringUrl= "https://kakoysegodnyaprazdnik.ru";
        URL url1 = new URL(stringUrl);
        URLConnection connection = url1.openConnection();

        try(BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String temp;
            while ((temp = in.readLine()) != null){
                if(temp.contains("itemprop=\"acceptedAnswer")){
                    line = temp;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Pattern pattern = Pattern.compile("<span itemprop=\"text\">(.+?)</span>");
        Matcher matcher = pattern.matcher(line);
        String result = "";
        int num = 1;
        while(matcher.find()){
            result += String.valueOf(num) + ". " +  matcher.group(1) + "\n" + "\n";
            num++;
        }

        return result;
    }
}
