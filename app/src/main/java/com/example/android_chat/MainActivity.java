package com.example.android_chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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



public class MainActivity extends AppCompatActivity {
    private static final String QUEUE_NAME = "Vanessa";
    private Button btnSend;
    private EditText inputMsg;
    private RecyclerView recyclerView;
    private ArrayList<String> messageList;
    private MessagesListAdapter adapter;
    private ConnectionFactory factory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        messageList = new ArrayList<String>();

        recyclerView.setAdapter(adapter);

        try {
            initFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        initViews();


    }


    private void initViews() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User user = new User();
                user.setName("Vanessa");
                user.setPhone(18217430061L);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pushMessage(user);
                    }
                }).start();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void pushMessage(User user) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(user.getName(), false, false, false, null);
            String message = inputMsg.getText().toString();
            channel.basicPublish("", user.getName(), null, message.getBytes("UTF-8"));
            System.out.println("Mensagem enviada para a fila " + user.getName());

            Message newMsg = new Message(message, user.getName());
            adapter.insertItem(newMsg);

            channel.close();
            connection.close();
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
                    System.out.println(" Mensagem: " + message);
                }
            };
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

