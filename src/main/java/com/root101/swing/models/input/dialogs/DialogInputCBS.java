/*
 * Copyright 2021 Root101 (jhernandezb96@gmail.com, +53-5-426-8660).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Or read it directly from LICENCE.txt file at the root of this project.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.root101.swing.models.input.dialogs;

import com.root101.clean.core.app.services.NotificationsGeneralType;
import com.root101.clean.core.app.services.NotificationHandler;
import com.root101.module.util.personalization.core.domain.Personalization;
import com.root101.module.util.personalization.services.PersonalizationHandler;
import com.root101.swing.models.input.panels.BaseModelInputPanel;
import com.root101.swing.models.input.panels.ModelPanel;
import com.root101.swing.material.components.scrollpane._MaterialScrollPaneCore;
import com.root101.swing.util.Utils;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import com.root101.swing.models.input.ModelablePanel;
import com.root101.swing.util.interfaces.BindableComponent;
import com.root101.utils.interfaces.Update;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.ScrollPaneLayout;

/**
 * Dialogo para la creacion de modelos en los combo boxes.<br/>
 * Basicamente cuando termina actualiza el combo box con el elemento nuevo que
 * se creo.
 *
 * Ejemplo: new _DialogModelInput(this, new ModelPanelTest(1));
 *
 * @author Root101 (jhernandezb96@gmail.com, +53-5-426-8660)
 * @author JesusHdezWaterloo@Github
 *
 * @param <T>
 */
public class DialogInputCBS<T, Y extends JComponent & Update & BindableComponent<T>> extends JDialog implements ModelablePanel<T> {

    private final BaseModelInputPanel<T> basePanel;
    private final Y icbs;

    private final _MaterialScrollPaneCore scrollPane = new _MaterialScrollPaneCore();

    public DialogInputCBS(Y icbs, ModelPanel<T> modelPanel) {
        this.icbs = icbs;
        basePanel = new BaseModelInputPanel<>(modelPanel);
        this.setLayout(new BorderLayout());

        //add el scroll
        this.add(scrollPane, BorderLayout.CENTER);

        //add el task pane al scroll
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setViewportView(basePanel);

        Rectangle screen = Utils.getScreenSize();

        int maxWidth = (int) (screen.getWidth() - 75);
        int maxHeight = (int) (screen.getHeight() - 75);
        int width = basePanel.getPreferredSize().width + 15;
        int height = basePanel.getPreferredSize().height + (isUndecorated() ? 0 : 40);

        this.setSize(Math.min(maxWidth, width) + 25, Math.min(maxHeight, height) + 25);
        this.setLocationRelativeTo(null);
        this.setUndecorated(false);
        this.setResizable(true);

        addListeners();
        personalize();
        this.setVisible(true);
    }

    private void addListeners() {
        basePanel.getButtonCancel().addActionListener((java.awt.event.ActionEvent evt) -> {
            onCancelAction();
        });

        basePanel.getButtonAddEdit().addActionListener((java.awt.event.ActionEvent evt) -> {
            onCreateAction();
        });

        globalsKeyListeners(basePanel);
    }

    private void globalsKeyListeners(JPanel panel) {
        for (Component c : panel.getComponents()) {
            addGlobalKeyListeners(c);
            if (c instanceof JPanel) {
                globalsKeyListeners((JPanel) c);
            }
        }
    }

    private void addGlobalKeyListeners(Component c) {
        if (c == null) {
            return;
        }
        //si es text area no hago nada
        if (c instanceof JTextArea) {
            return;
        }

        //si es texto me fijo en la posicion del caret para eliminar
        if (c instanceof JTextComponent) {
            c.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    int len = ((JTextComponent) c).getText().length();
                    int pos = ((JTextComponent) c).getCaretPosition();
                    if (evt.getKeyChar() == KeyEvent.VK_DELETE && len == pos && basePanel.getOldModel() != null) {
                        onDeleteAction();
                    }
                }
            });
        }

        //enter y escape para el resto
        c.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
                    onCreateAction();
                } else if (evt.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    onCancelAction();
                }
            }
        });
    }

    @Override
    public T getNewModel() throws Exception {
        return (T) basePanel.getNewModel();
    }

    @Override
    public T getOldModel() {
        return (T) basePanel.getOldModel();
    }

    @Override
    public void setOldModel(T model) {
        basePanel.setOldModel(model);
    }

    @Override
    public Map<String, Object> bindFields() {
        return basePanel.bindFields();
    }

    public BaseModelInputPanel<T> getBasePanel() {
        return basePanel;
    }

    private void personalize() {
        if (getOldModel() == null) {
            this.setTitle("Crear");
            this.setIconImage(PersonalizationHandler.getDerivableIcon(Personalization.KEY_ICON_BUTTON_ADD).getImage());
        } else {
            this.setTitle("Editar");
            this.setIconImage(PersonalizationHandler.getDerivableIcon(Personalization.KEY_ICON_BUTTON_EDIT).getImage());
        }
    }

    @Override
    public T onDeleteAction() {
        return null;
    }

    @Override
    public T onCreateAction() {
        T obj = null;
        try {
            if (NotificationHandler.showConfirmDialog(NotificationsGeneralType.CONFIRM_CREATE)) {
                obj = basePanel.onCreateAction();
                if (obj != null) {
                    NotificationHandler.showConfirmDialog(NotificationsGeneralType.NOTIFICATION_CREATE, obj);
                }
            }
        } catch (Exception e) {
        }
        return onPostCreateAction(obj);
    }

    @Override
    public boolean onCancelAction() {
        try {
            if (NotificationHandler.showConfirmDialog(NotificationsGeneralType.CONFIRM_CANCEL)) {
                if (basePanel.onCancelAction()) {
                    dispose();
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public T onPostCreateAction(T obj) {
        try {
            obj = basePanel.onPostCreateAction(obj);
        } catch (Exception e) {
        }
        if (obj != null) {
            icbs.setObject(obj);
            icbs.revalidate();
            dispose();
        }
        return obj;
    }

    @Override
    public T onPostDeleteAction(T obj) {
        return (T) basePanel.onPostCreateAction(obj);
    }
}
