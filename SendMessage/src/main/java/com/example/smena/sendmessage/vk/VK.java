package com.example.smena.sendmessage.vk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.smena.sendmessage.R;
import com.perm.kate.api.Api;
import com.perm.kate.api.User;

import java.util.ArrayList;

public class VK extends Activity {

    private final int REQUEST_LOGIN = 1;

    Button authorizeButton;
    Button logoutButton;
    Button wallButton;
    Button msgButton;
    EditText messageEditText;
    Spinner spinner;

    Account account = new Account();
    Api api;
    private static long uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setupUI();

        //Восстановление сохранённой сессии
        account.restore(this);

        //Если сессия есть создаём API для обращения к серверу
        if (account.access_token != null)
            api = new Api(account.access_token, Constants.API_ID);

        showButtons();
    }

    private void setupUI() {
        authorizeButton = (Button) findViewById(R.id.authorize);
        logoutButton = (Button) findViewById(R.id.logout);
        wallButton = (Button) findViewById(R.id.wall);
        msgButton = (Button) findViewById(R.id.msg);
        messageEditText = (EditText) findViewById(R.id.message);
        setSpinner();

        authorizeButton.setOnClickListener(authorizeClick);
        logoutButton.setOnClickListener(logoutClick);
        wallButton.setOnClickListener(wallClick);
        msgButton.setOnClickListener(msgClick);

    }

    private OnClickListener authorizeClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startLoginActivity();
        }
    };

    private OnClickListener logoutClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            logOut();
        }
    };

    private OnClickListener wallClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            postToWall();
        }
    };

    private OnClickListener msgClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            sendMessage();
        }
    };

    private void startLoginActivity() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                //авторизовались успешно
                account.access_token = data.getStringExtra("token");
                account.user_id = data.getLongExtra("user_id", 0);
                account.save(VK.this);
                api = new Api(account.access_token, Constants.API_ID);
                showButtons();
            }
        }
    }

    private void postToWall() {
        //Общение с сервером в отдельном потоке чтобы не блокировать UI поток
        new Thread() {
            @Override
            public void run() {
                try {
                    String text = messageEditText.getText().toString();
                    api.createWallPost(uid, text, null, null, false, false,
                            false, null, null, null, 0L, null, null);
                    //Показать сообщение в UI потоке
                    runOnUiThread(successRunnableWall);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void sendMessage() {
        //Общение с сервером в отдельном потоке чтобы не блокировать UI поток
        new Thread() {
            @Override
            public void run() {
                try {
                    String text = messageEditText.getText().toString();
                    api.sendMessage(uid, 0, "Сообщение", "Заголовок", null, null, null,
                            null, null, null, null);
                    //Показать сообщение в UI потоке
                    runOnUiThread(successRunnableMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private ArrayList<User> getFriends() {
        try {
            return api.getFriends(account.user_id, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Runnable successRunnableWall = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "Запись успешно добавлена",
                    Toast.LENGTH_LONG).show();
        }
    };

    Runnable successRunnableMsg = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "Сообщение успешно отправлено",
                    Toast.LENGTH_LONG).show();
        }
    };

    private void logOut() {
        api = null;
        account.access_token = null;
        account.user_id = 0;
        account.save(VK.this);
        showButtons();
    }

    void showButtons() {
        if (api != null) {
            authorizeButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            wallButton.setVisibility(View.VISIBLE);
            messageEditText.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            msgButton.setVisibility(View.VISIBLE);
        } else {
            authorizeButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            wallButton.setVisibility(View.GONE);
            messageEditText.setVisibility(View.GONE);
            msgButton.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        }
    }

    private void setSpinner() {

        ArrayList<User> friends = getFriends();
        final ArrayList<Long> friend_id = new ArrayList<>();
        ArrayList<String> friend_name = new ArrayList<>();

        for (int i = 0; i < friends.size(); i++) {
            friend_id.add(friends.get(i).uid);
            friend_name.add(friends.get(i).first_name + " " + friends.get(i).last_name);
        }

        String[] data = new String[friends.size()];
        data = friend_name.toArray(data);

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент
        spinner.setSelection(2);

        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                uid = friend_id.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}