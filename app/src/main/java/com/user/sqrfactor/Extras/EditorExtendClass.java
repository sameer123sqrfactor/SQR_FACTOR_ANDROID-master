package com.user.sqrfactor.Extras;

import android.content.Context;
import android.util.AttributeSet;

import com.github.irshulx.Components.InputExtensions;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorCore;

public class EditorExtendClass extends Editor {
    protected Editor editor;
    protected String name;


    public EditorExtendClass(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public void renderEditorFromHtml(String content) {
//        super.renderEditorFromHtml(content);
//    }


//    @Override
//    public InputExtensions getInputExtensions() {
//        return super.getInputExtensions();
//    }

    public InputExtensions SetContent(){
        return getInputExtensions();
    }

}
