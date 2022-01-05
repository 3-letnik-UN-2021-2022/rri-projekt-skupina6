package com.badlogic.nonogram.dialog;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class GameDialog extends Dialog {

    public enum Options {
        back,retry,enter
    }

    public GameDialog(String title, Skin skin) {
        super(title, skin);
    }
    public GameDialog(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
    }
    public GameDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
    }

    public Cell addButton(TextButton button, Object object) {
        setObject(button, object);
        return getButtonTable().add(button); //Return table cell
    }
    public Cell addTextField(TextField textField) {
        //setObject(textField);
        return getButtonTable().add(textField).colspan(2); //Return table cell
    }
}

