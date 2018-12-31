package com.pache.weixin.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义菜单按钮
 */
public class Button {

    private List<AbstractButton> button = new ArrayList<>();

    public List<AbstractButton> getButton() {
        return button;
    }

    public void setButton(List<AbstractButton> button) {
        this.button = button;
    }
}
