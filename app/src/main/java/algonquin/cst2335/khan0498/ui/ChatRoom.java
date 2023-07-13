package algonquin.cst2335.khan0498.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.khan0498.R;
import algonquin.cst2335.khan0498.data.ChatMessage;
import algonquin.cst2335.khan0498.data.ChatMessageDAO;
import algonquin.cst2335.khan0498.data.ChatRoomViewModel;
import algonquin.cst2335.khan0498.data.MessageDatabase;
import algonquin.cst2335.khan0498.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.khan0498.databinding.ReceiveMessageBinding;
import algonquin.cst2335.khan0498.databinding.SentMessageBinding;


public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    ChatRoomViewModel chatModel ;
    ArrayList<ChatMessage> messages;
    private RecyclerView.Adapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        ChatMessageDAO mDAO = db.cmDAO();
        chatModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EditText textInput=findViewById(R.id.textInput);
        messages = chatModel.messages.getValue();
        if(messages == null)
        {
            chatModel.messages.setValue(messages = new ArrayList<>());
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database
                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }
        if(messages == null)
        {
            chatModel.messages.postValue( messages = new ArrayList<ChatMessage>());
        }

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.sendbutton.setOnClickListener(click->{

            String typedMessage = String.valueOf(textInput.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");

            String currentDateAndTime = sdf.format(new Date());

            ChatMessage chatMessage = new ChatMessage(typedMessage,currentDateAndTime,true);

            synchronized (messages) {
                messages.add(chatMessage);
            }
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(() ->{
                chatMessage.id =(int)  mDAO.insertMessage(chatMessage);//add to database;

            });
            myAdapter.notifyItemInserted(messages.size()-1);
            binding.textInput.setText("");

        });

        binding.receiveButton.setOnClickListener(click->{

            String typedMessage = String.valueOf(textInput.getText());
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");

            String currentDateAndTime = sdf.format(new Date());

            ChatMessage chatMessage = new ChatMessage(typedMessage,currentDateAndTime,false);

            synchronized (messages) {
                messages.add(chatMessage);
            }
            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(() ->{
                chatMessage.id =(int)  mDAO.insertMessage(chatMessage);//add to database;

            });
            myAdapter.notifyItemInserted(messages.size()-1);
            binding.textInput.setText("");

        });

        binding.recycleView.setAdapter(myAdapter=new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                if (viewType == 0) {
                    // Inflating the send_message layout
                    View view = inflater.inflate(R.layout.sent_message, parent, false);
                    return new MyRowHolder(view);
                } else {
                    // Inflating the receive_message layout
                    View view = inflater.inflate(R.layout.receive_message, parent, false);
                    return new MyRowHolder(view);
                }
            }
            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                ChatMessage chatMessage = messages.get(position);
                holder.messageText.setText(chatMessage.getMessage());
                holder.timeText.setText(chatMessage.getTimeSent());
            }
            @Override
            public int getItemCount() {
                return messages.size();
            }
            @Override
            public int getItemViewType(int position){
                ChatMessage chatMessage = messages.get(position);
                if (chatMessage.IsSentButton()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }
    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
            ChatMessageDAO mDAO = db.cmDAO();
            itemView.setOnClickListener(clk->{
                int position = getAbsoluteAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete the message: "+messageText.getText())
                        .setTitle("Question:")
                        .setNegativeButton("No", (dialog,cl)->{})
                        .setPositiveButton("Yes", (dialog,cl)->{
                            ChatMessage removedMessage = messages.remove(position);
                            Executor thread1 = Executors.newSingleThreadExecutor();
                            thread1.execute(() ->{
                                mDAO.deleteMessage(removedMessage);//add to database;
                            });
                            myAdapter.notifyItemRemoved(position);
                            Snackbar.make(messageText,"You deleted message #"+position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo",click->{
                                        messages.add(position, removedMessage);
                                        myAdapter.notifyItemInserted(position);
                                        Executor thread2 = Executors.newSingleThreadExecutor();
                                        thread1.execute(() ->{
                                            mDAO.insertMessage(removedMessage);

                                        });
                                    })
                                    .show();
                        })
                        .create().show();
            });
            messageText = itemView.findViewById(R.id.messageText);
            timeText = itemView.findViewById(R.id.timeText);
        }
    }
}