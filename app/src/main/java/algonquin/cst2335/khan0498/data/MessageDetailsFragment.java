package algonquin.cst2335.khan0498.data;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import algonquin.cst2335.khan0498.databinding.DetailsLayoutBinding;

public class MessageDetailsFragment extends Fragment {
    ChatMessage selected;

    public MessageDetailsFragment(){

    }
    public MessageDetailsFragment(ChatMessage m) {
        this.selected = m;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        DetailsLayoutBinding binding = DetailsLayoutBinding.inflate(inflater);
        binding.messagetext.setText(selected.message);
        binding.timetext.setText(selected.timeSent);
        binding.databaseText.setText("Id = "+selected.id);
        return binding.getRoot();
    }

    public void displayMessage(ChatMessage newValue) {
    }
}
