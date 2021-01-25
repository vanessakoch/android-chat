package com.example.android_chat.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
import java.io.Serializable;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends Activity {
    private static String QUEUE_NAME = null;
    private static String EXCHANGE_NAME = null;
    private ImageButton btnSend;
    private EditText inputMsg;
    private ListView listView;
    private ChatAdapter adapter;
    private ConnectionFactory factory;
    private User currentUser, contact;
    private int position;
    private boolean isGroup;
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

        if(contact != null) {
            isGroup = false;
            QUEUE_NAME = contact.getName();
            txtContactName.setText(QUEUE_NAME);
            imgAvatar.setImageResource(contact.getUserAvatar());
            adapter = new ChatAdapter(this, currentUser, QUEUE_NAME);
        } else {
            isGroup = true;
            EXCHANGE_NAME = currentUser.getGroupList().get(position).getName();
            imgAvatar.setImageResource(currentUser.getGroupList().get(position).getGroupAvatar());
            txtContactName.setText(EXCHANGE_NAME);
            adapter = new ChatAdapter(this, currentUser, EXCHANGE_NAME);
        }

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
        factory.setUri("amqps://yooduxpm:CCzkQt40lWfEN8hWhdMeejS_SGcAKVrF@owl.rmq.cloudamqp.com/yooduxpm");
    }

    private void pushMessage() {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String message = inputMsg.getText().toString();

            if(!isGroup) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
                System.out.println("Mensagem enviada para a fila " + QUEUE_NAME);
            } else {
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println(" Mensagem enviada para o grupo " + EXCHANGE_NAME);
            }

            channel.close();
            connection.close();

            // tive que fazer essa função porque somente a thread principal que gerou a view pode mudar ela
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playBeep();
                    currentUser.getMessageList().add(new Message(currentUser, message));
                    if(!isGroup) {
                        User.chatMessages.put(QUEUE_NAME, currentUser.getMessageList());
                    } else {
                        User.chatMessages.put(EXCHANGE_NAME, currentUser.getMessageList());
                    }
                    adapter.notifyDataSetChanged();
                    inputMsg.setText("");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMessage() {
        String queueName = QUEUE_NAME;

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            if(!isGroup) {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                System.out.println("Aguardando mensagens da fila " + QUEUE_NAME);

            } else {
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
                queueName = channel.queueDeclare().getQueue();
                channel.queueBind(queueName, EXCHANGE_NAME, "");
                System.out.println(" Aguardando mensagens do grupo " + EXCHANGE_NAME);
            }

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                           byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Mensagem: " + message);
                }
            };

            channel.basicConsume(queueName, true, consumer);


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

    public void onClickReturn(View view) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", (Serializable) currentUser);
        Intent returnIntent = new Intent();
        returnIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}