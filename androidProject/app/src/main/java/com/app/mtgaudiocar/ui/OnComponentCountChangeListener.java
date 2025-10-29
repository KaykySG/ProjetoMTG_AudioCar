package com.app.mtgaudiocar.ui;

/**
 * Interface para comunicação de eventos entre o Adapter (onde a quantidade muda)
 * e a Activity (que precisa saber da mudança para disparar a validação).
 */
public interface OnComponentCountChangeListener {
    /**
     * Chamado sempre que a quantidade de um componente é alterada (adicionada ou removida).
     */
    void onCountChanged();
}