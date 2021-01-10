package com.example.android_chat;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ChatActivity extends Activity {
    private static String QUEUE_NAME = null;
    private Button btnSend;
    private EditText inputMsg;
    private ListView listView;
    private ArrayList<Message> messageList;
    private ChatAdapter adapter;
    private ConnectionFactory factory;
    private User currentUser, contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = (ListView) findViewById(R.id.list_view);
        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);

        messageList = new ArrayList<Message>();
        adapter = new ChatAdapter(this, messageList);
        listView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        currentUser = (User) extras.getSerializable("user");
        contact = (User) extras.getSerializable("contact");

        QUEUE_NAME = contact.getName();

        try {
            initFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        startThreads();
    }


    private void startThreads() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pushMessage();
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getMessage();
                    }
                }).start();
            }
        });
    }

    private void initFactory() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        factory = new ConnectionFactory();
        factory.setUri("amqps://yooduxpm:CCzkQt40lWfEN8hWhdMeejS_SGcAKVrF@owl.rmq.cloudamqp.com/yooduxpm");
    }

    private void pushMessage() {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = inputMsg.getText().toString();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            System.out.println("Mensagem enviada para a fila " + QUEUE_NAME);

            channel.close();
            connection.close();

            // tive que fazer essa função porque somente a thread principal que gerou a view pode mudar ela
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playBeep();
                    messageList.add(new Message(currentUser.getName(), message, true));
                    adapter.notifyDataSetChanged();
                    inputMsg.setText("");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMessage() {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Aguardando mensagens da fila " + QUEUE_NAME);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Mensagem: " + message);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playBeep() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}