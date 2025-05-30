package com.shotvalue.analizador_xgot.view;

public interface ViewLifecycle {
    /** Se llama cada vez que la vista pasa a primer plano. */
    default void onShow() {}
    /** Se llama justo antes de que la vista quede oculta. */
    default void onHide() {}
}
