package com.anadolstudio.core.interfaces;

public interface EditState {

    boolean backClick(); // Возвращает false, если перехватывается, иначе(стандартное поведение выполняется после метода) - true

    boolean isReadyToBackClick();

    boolean isReadyToApply();

    boolean apply();
}
