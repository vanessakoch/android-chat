package com.example.android_chat.activities;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android_chat.R;
import com.example.android_chat.adapters.ChatAdapter;
import com.example.android_chat.entities.Message;
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

public class ChatGroupActivity extends Activity  {
    private static String EXCHANGE_NAME = null;
    public static User currentUser;
    private ImageButton btnSend;
    private EditText inputMsg;
    private ListView listView;
    private ChatAdapter adapter;
    private ConnectionFactory factory;
    private User contact;
    private boolean isGroup;
    private int position;
    CircleImageView imgAvatar;
    TextView txtContactName;

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

        isGroup = true;
        EXCHANGE_NAME = currentUser.getGroupList().get(position).getName();
        imgAvatar.setImageResource(currentUser.getGroupList().get(position).getGroupAvatar());
        txtContactName.setText(EXCHANGE_NAME);

        adapter = new ChatAdapter(this, currentUser, EXCHANGE_NAME, isGroup);
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

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
            String message = inputMsg.getText().toString();

            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
            System.out.println("Mensagem enviada para o grupo " + EXCHANGE_NAME);

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

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            System.out.println(" Aguardando mensagens do grupo " + EXCHANGE_NAME);

            channel.basicConsume(queueName, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Mensagem: " + message);

                    Message msg = new Message(currentUser, message);
                    channel.basicAck(envelope.getDeliveryTag(), false);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(currentUser);

                            if (User.chatMessages.get(EXCHANGE_NAME) == null) {
                                currentUser.getMessageList().add(msg);
                                User.chatMessages.put(EXCHANGE_NAME, currentUser.getMessageList());
                            } else {
                                User.chatMessages.get(EXCHANGE_NAME).add(msg);
                            }

                            adapter.insertItem();
                            playBeep();
                            inputMsg.getText().clear();

                        }
                    });
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

    public void updateMapUI(String message) {

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



