package algonquin.cst2335.khan0498.data;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public String theText;
    public String buttonText;
    public MutableLiveData<String> editString =new MutableLiveData();
    public MutableLiveData<Boolean> isSelected=new MutableLiveData<>();
}
