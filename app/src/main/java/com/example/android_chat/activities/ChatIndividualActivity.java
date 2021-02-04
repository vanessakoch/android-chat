package com.example.android_chat.activities;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_chat.adapters.ChatAdapter;
import com.example.android_chat.entities.Message;
import com.example.android_chat.R;
import com.example.android_chat.entities.User;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
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
import java.util.concurrent.TimeoutException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatIndividualActivity extends Activity {
    private static String QUEUE_NAME = null;
    public static User currentUser;
    private ImageButton btnSend;
    private EditText inputMsg;
    private ListView listView;
    private ChatAdapter adapter;
    private ConnectionFactory factory;
    private User contact;
    private int position;
    private boolean isGroup;
    CircleImageView imgAvatar;
    TextView txtContactName;
    String queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = (ListView) findViewById(R.id.list_view);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        txtContactName = (TextView) findViewById(R.id.txtContactName);
        imgAvatar = (CircleImageView) findViewById(R.id.imgAvatar);

        Bundle extras = getIntent().getExtras();
        currentUser = (User) extras.getSerializable("user");
        contact = (User) extras.getSerializable("contact");
        position = extras.getInt("position");

        isGroup = false;
        QUEUE_NAME = contact.getName();
        txtContactName.setText(QUEUE_NAME);
        imgAvatar.setImageResource(contact.getUserAvatar());
        queue = QUEUE_NAME;

        adapter = new ChatAdapter(this, currentUser, queue, isGroup);
        listView.setAdapter(adapter);

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
        factory.setUri("amqps://iaglihcb:ef01KeU6jxNdrjgf6aSHLU_4FTJ5ES0O@barnacle.rmq.cloudamqp.com/iaglihcb");
    }

    private void pushMessage() {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String message = inputMsg.getText().toString();

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
            System.out.println("Mensagem enviada para a fila " + QUEUE_NAME);

            channel.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMessage() {
        String queueName = QUEUE_NAME;

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            System.out.println("Aguardando mensagens da fila " + queueName);

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");

                    System.out.println("Mensagem: " + message);
                    updateMapUI(message);
                }
            };
            channel.basicConsume(queueName, true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void updateMapUI(String message) {
        Message msg = new Message(currentUser, message);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(currentUser);
                if (User.chatMessages.get(queue + currentUser.getName()) == null) {
                    if (User.chatMessages.get(currentUser.getName() + queue) == null) {
                        currentUser.getMessageList().add(msg);
                        User.chatMessages.put(currentUser.getName() + queue, currentUser.getMessageList());
                    } else {
                        User.chatMessages.get(currentUser.getName() + queue).add(msg);
                    }
                } else {
                    User.chatMessages.get(queue + currentUser.getName()).add(msg);
                }

                adapter.insertItem();
                playBeep();
                inputMsg.getText().clear();

            }
        });

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

    public void onClickReturn(View view) {
        finish();
    }
}

