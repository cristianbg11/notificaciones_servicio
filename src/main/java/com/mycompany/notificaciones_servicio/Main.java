/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.notificaciones_servicio;

import com.google.gson.Gson;

import static j2html.TagCreator.article;
import static j2html.TagCreator.b;
import static j2html.TagCreator.p;
import static j2html.TagCreator.span;
import static java.rmi.server.LogStream.log;

import java.util.ArrayList;
import java.util.Date;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import spark.ModelAndView;

import java.sql.*;

import spark.Filter;
import spark.Spark;

import java.util.*;
import java.text.*;
import java.util.concurrent.ConcurrentHashMap;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@SpringBootApplication
@EnableEurekaClient
public class Main {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/notificaciones";

    //  Database credenciales
    static final String USER = "root";
    static final String PASS = "";

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        port(4566);
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");

        });

        post("/notificacion", (req, res) -> {
            System.out.println("llegue aqui...");
            String mensaje = req.queryParams("mensaje");
            String idped = req.queryParams("id");
            String correo = req.queryParams("correo");;
            //System.out.println(mensaje);
            //int id=Integer.parseInt(idped);

            Connection conn = null;
            Statement stmt = null;

            try {
                // Registrar JDBC driver
                Class.forName("com.mysql.jdbc.Driver");

                // Crear una conexión
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //Ejecutar un query
                System.out.println("Creating statement...");
                stmt = conn.createStatement();
                String sql;
                sql = "insert into notificacion (idpedido,correo,mensaje) values ("
                        + idped + ",'" + correo + "','" + mensaje + "')";
                //ResultSet rs = stmt.executeQuery(sql);
                stmt.executeUpdate(sql);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Recipient's email ID needs to be mentioned.
            String to = correo;

            // Sender's email ID needs to be mentioned
            String from = "medusascompany@gmail.com";

            // Assuming you are sending email from through gmails smtp
            String host = "smtp.gmail.com";

            // Get system properties
            Properties props = System.getProperties();

            // Setup mail server
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");

            props.put("mail.smtp.auth", "true");

            props.put("mail.smtp.starttls.enable", "true");

            props.put("mail.debug", "true");
            props.put("mail.store.protocol", "pop3");
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            // Get the Session object.// and pass username and password
            Session session = Session.getInstance(props, new javax.mail.Authenticator() {

                protected PasswordAuthentication getPasswordAuthentication() {

                    return new PasswordAuthentication("medusascompany@gmail.com", "cristianfa");

                }

            });

            // Used to debug SMTP issues
            session.setDebug(true);

            try {
                // Create a default MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Set From: header field of the header.
                message.setFrom(new InternetAddress(from));

                // Set To: header field of the header.
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                // Set Subject: header field
                message.setSubject("Pedido #"+idped);

                // Now set the actual message
                message.setText(mensaje);

                System.out.println("sending...");
                // Send message
                Transport.send(message);
                System.out.println("Sent message successfully....");
            } catch (MessagingException mex) {
                mex.printStackTrace();
            }

            return "";


        });
    }
}
