package com.example.mon.qrcodetrackingsystem.utils;

import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.Callable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by mon on 10/7/18.
 */

public class RxUtils {

    private RxUtils() {
    }

    public static <T> Observable<T> toObservable(@NonNull final ObservableField<T> observableField) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<T> e) throws Exception {

                final android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(android.databinding.Observable observable, int propertyId) {
                        if (observable == observableField) {
                            e.onNext(observableField.get());
                        }
                    }
                };

                observableField.addOnPropertyChangedCallback(callback);
            }
        });
    }

    public static <T> Flowable<T> toFlowable(@NonNull final ObservableField<T> observableField) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull final FlowableEmitter<T> e) throws Exception {
                final android.databinding.Observable.OnPropertyChangedCallback callback = new android.databinding.Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(android.databinding.Observable observable, int propertyId) {
                        if (observable == observableField) {
                            e.onNext(observableField.get());
                        }
                    }
                };
            }
        }, BackpressureStrategy.LATEST);
    }

    /** wrapping synchronous operation in an RxJava Observable */
    public static Observable<Boolean> wipeContentsFromSharedPreferences(final SharedPreferences sharedPreferences) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return sharedPreferences.edit().clear().commit();
            }
        });
    }

    public static Observable<String> queryTextChanges(@NonNull final SearchView searchView) {
        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
//                subject.onComplete();
                /** hides keyboard */
                searchView.clearFocus();
                subject.onNext(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                subject.onNext(newText);
                return true;
            }
        });
        return subject;
    }

    public static Observable<String> queryTextChanges(@NonNull final EditText editText) {
        final PublishSubject<String> subject = PublishSubject.create();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                subject.onNext(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return subject;
    }

    /** By getting the view's tag, we will be able to know if the view has been clicked */
    public static Observable<View> clicks(@NonNull final View view) {
        return Observable.create(emitter -> {
            view.setOnClickListener(v -> {
                /** register if the view has been clicked before */
                v.setSelected(!v.isSelected());
                emitter.onNext(v);
            });
        });
    }

    public static Observable<View> longClicks(@NonNull final View view) {
        return Observable.create(emitter -> {
            view.setOnLongClickListener(v -> {
                emitter.onNext(v);
                return true;
            });
        });
    }

    public static Observable<CharSequence> textChanges(@NonNull final EditText editText) {
        return Observable.create(emitter -> {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    editText.removeTextChangedListener(this);
                    emitter.onNext(s);
                    editText.addTextChangedListener(this);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        });
    }

    public static Observable<Boolean> setOnFocus(@NonNull final EditText editText) {
        return Observable.create(emitter -> {
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    emitter.onNext(b);
                }
            });
        });
    }
}
