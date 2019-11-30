package com.szr.android.data.models

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry

/**
 * Mirror representation of [BaseObservable][androidx.databinding.BaseObservable]. Intended to be
 * used as delegatable implementation of [NotifiableObservable].
 *
 * NOTE: appropriate implementation will require updating [sender].
 */
class NotifiableObservableImpl : NotifiableObservable {

    /**
     * In order for data binding to function, sender must match the view model being referenced. Set
     * this value to the parent class after construction.
     */
    var sender: Observable = this

    @Transient
    private var callbacks: PropertyChangeRegistry? = null

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    override fun notifyChange() {
        synchronized(this) {
            if (callbacks == null) return
        }
        callbacks?.notifyCallbacks(sender, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property that
     * changes should be marked with {@link Bindable} to generate a field in <code>BR</code> to be
     * used as <code>fieldId</code>.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    override fun notifyPropertyChanged(fieldId: Int) {
        synchronized(this) {
            if (callbacks == null) return
        }
        callbacks?.notifyCallbacks(sender, fieldId, null)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            if (callbacks == null) return
        }
        callbacks?.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        synchronized(this) {
            if (callbacks == null) callbacks = PropertyChangeRegistry()
        }
        callbacks?.add(callback)
    }
}