package com.example.tcapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.model.AlertDialog

public open class ViewModel {
    protected var _isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    protected var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
    protected var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
    public val isLoading:LiveData<Boolean>
        get() = _isLoading
    public val error:LiveData<AlertDialog.Error?>
        get() = _error
    public val notification:LiveData<AlertDialog.Notification?>
        get() = _notification
    constructor(){}
}