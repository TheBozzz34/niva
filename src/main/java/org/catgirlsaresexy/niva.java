package org.catgirlsaresexy;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sentry.Sentry;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.URI;
import java.nio.file.Paths;


public class niva {
    public static void main(String[] args) throws IOException {

        Logger logger = LoggerFactory.getLogger(niva.class);

        Sentry.init(options -> {
            options.setDsn("https://1434d5ba0aff470083175bdba1c69b00@o561860.ingest.sentry.io/4503950208729088");
            options.setTracesSampleRate(1.0);
            options.setDebug(false);
        });
        logger.info("Initialized Sentry");
        logger.info("Logging to ./niva.log");

        File theDir = new File("tmp");
        if (!theDir.exists()){
            theDir.mkdirs();
            logger.info("Created temp folder");
        }



        JFrame f=new JFrame();//creating instance of JFrame

        f.setLayout(new FlowLayout());
        JLabel picLabel = new JLabel(new ImageIcon("tmp/image.jpeg"));
        f.add(picLabel);
        JButton b=new JButton("Get Image");//creating instance of JButton
        JButton b1=new JButton("Open in Explorer");
        b.setBounds(130,100,100, 40);//x axis, y axis, width, height
        b1.setBounds(110, 150, 150, 40);

        f.add(b);//adding button in JFrame
        f.add(b1);


        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        f.setSize(400,500);//400 width and 500 height
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible

        CloseableHttpClient httpclient = HttpClients.createDefault();

        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(new File("tmp"));
                } catch (IOException ex) {
                    Sentry.captureException(ex);
                    throw new RuntimeException(ex);
                }
            }
        });

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                    HttpGet httpGet = new HttpGet("https://api.waifu.pics/nsfw/neko");
                    try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {

                        logger.info(response1.getCode() + " " + response1.getReasonPhrase());

                        HttpEntity entity1 = response1.getEntity();
                        String responseString = EntityUtils.toString(entity1, "UTF-8");

                        JSONParser parser = new JSONParser();
                        JSONObject json = (JSONObject) parser.parse(responseString);
                        JSONObject jo = new JSONObject(json);

                        String imageurl = (String) jo.get("url");


                        String filename = Paths.get(new URI(imageurl).getPath()).getFileName().toString();

                        //logger.info(responseString);
                        logger.info(imageurl);
                        EntityUtils.consume(entity1);


                        try(InputStream in = new URL(imageurl).openStream()){
                            Files.copy(in, Paths.get("tmp/" + filename));
                        }

                    } catch (ParseException ex) {
                        Sentry.captureException(ex);
                        throw new RuntimeException(ex);
                    } catch (org.json.simple.parser.ParseException ex) {
                        Sentry.captureException(ex);
                        throw new RuntimeException(ex);
                    } catch (URISyntaxException ex) {
                        Sentry.captureException(ex);
                        throw new RuntimeException(ex);
                    }

                } catch (IOException ex) {
                    Sentry.captureException(ex);
                    throw new RuntimeException(ex);
                }

            }
        });
    }
}