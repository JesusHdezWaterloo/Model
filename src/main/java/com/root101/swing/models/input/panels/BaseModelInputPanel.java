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
package com.root101.swing.models.input.panels;

import com.root101.swing.material.components.container.panel._PanelGradient;
import java.awt.Color;
import com.root101.utils.interfaces.Update;
import com.root101.swing.models.input.ModelablePanel;
import com.root101.module.util.personalization.core.domain.Personalization;
import com.root101.module.util.personalization.services.PersonalizationHandler;
import com.root101.swing.material.components.button.MaterialButton;
import com.root101.swing.material.components.button.MaterialButtonIcon;
import com.root101.swing.material.components.button.MaterialButtonsFactory;
import com.root101.swing.material.components.container.MaterialContainersFactory;
import com.root101.swing.material.components.container.layout.HorizontalLayoutContainer;
import com.root101.swing.material.components.container.panel.MaterialPanelBorder;
import com.root101.swing.material.standards.MaterialColors;
import com.root101.swing.material.standards.MaterialIcons;
import com.root101.swing.prepared.button.MaterialButtonAddEdit;
import com.root101.swing.prepared.button.MaterialPreparedButtonsFactory;
import java.awt.BorderLayout;
import java.util.Map;
import javax.swing.JPanel;

/**
 *
 * @author Root101 (jhernandezb96@gmail.com, +53-5-426-8660)
 * @author JesusHdezWaterloo@Github
 */
public class BaseModelInputPanel<T> extends _PanelGradient implements Update, ModelablePanel<T> {

    private final ModelPanel modelPanel;

    public BaseModelInputPanel(ModelPanel modelPanel) {
        initComponents();
        this.modelPanel = modelPanel;
        this.panelModelCore.add(this.modelPanel);
        personalize();
    }

    private void initComponents() {
        panelButtons = MaterialContainersFactory.buildPanelTransparent();
        buttonCancel = MaterialButtonsFactory.buildButton();
        buttonDelete = MaterialButtonsFactory.buildIconTransparent();
        panelModelCore = MaterialContainersFactory.buildPanelComponent();

        buttonAddEdit = MaterialPreparedButtonsFactory.buildAddEdit();

        this.setLayout(new BorderLayout());
        this.add(panelModelCore);

        panelButtons.setLayout(new BorderLayout());
        panelButtons.add(buttonDelete, BorderLayout.WEST);

        buttonCancel.setText("Cancelar");
        buttonCancel.setPreferredSize(new java.awt.Dimension(125, 50));

        HorizontalLayoutContainer.builder hlc = HorizontalLayoutContainer.builder((int) buttonAddEdit.getPreferredSize().getHeight());
        hlc.add(buttonAddEdit);
        hlc.add(buttonCancel);

        panelButtons.add(hlc.build(), BorderLayout.EAST);

        this.add(panelButtons, BorderLayout.SOUTH);
    }// </editor-fold>                        

    // Variables declaration - do not modify//:variables
    private MaterialButtonAddEdit buttonAddEdit;
    private MaterialButton buttonCancel;
    private MaterialButtonIcon buttonDelete;
    private JPanel panelButtons;
    private MaterialPanelBorder panelModelCore;
    // End of variables declaration                   

    private void personalize() {
        buttonAddEdit.isCreated(modelPanel.getOldModel() == null);
        buttonDelete.setIcon(MaterialIcons.DELETE_FOREVER);

        buttonDelete.setVisible(modelPanel.getOldModel() != null);
        buttonDelete.setEnabled(modelPanel.getOldModel() != null);

        this.setPrimaryColor(MaterialColors.WHITE);
        this.setSecundaryColor(MaterialColors.WHITE);

        buttonCancel.setBackground(PersonalizationHandler.getColor(Personalization.KEY_COLOR_BUTTON_CANCEL));

        this.setBackground(PersonalizationHandler.getColor(Personalization.KEY_COLOR_BACKGROUND_PANEL));
    }

    @Override
    public void update() {
        modelPanel.update();
    }

    @Override
    public T getNewModel() throws Exception {
        return (T) modelPanel.getNewModel();
    }

    @Override
    public T getOldModel() {
        return (T) modelPanel.getOldModel();
    }

    @Override
    public void setOldModel(T model) {
        modelPanel.setOldModel(model);
    }

    @Override
    public T onDeleteAction() {
        return (T) modelPanel.onDeleteAction();
    }

    @Override
    public T onCreateAction() {
        return (T) modelPanel.onCreateAction();
    }

    @Override
    public T onPostCreateAction(T obj) {
        return (T) modelPanel.onPostCreateAction(obj);
    }

    @Override
    public T onPostDeleteAction(T obj) {
        return (T) modelPanel.onPostDeleteAction(obj);
    }

    @Override
    public boolean onCancelAction() {
        return modelPanel.onCancelAction();
    }

    @Override
    public Map<String, Object> bindFields() {
        return modelPanel.bindFields();
    }

    public void setOkColor(Color okColor) {
        buttonAddEdit.setBackground(okColor);
        personalize();
    }

    public void setCancelColor(Color cancelColor) {
        buttonCancel.setBackground(cancelColor);
        personalize();
    }

    public Color getOkColor() {
        return buttonAddEdit.getBackground();
    }

    public Color getCancelColor() {
        return buttonCancel.getBackground();
    }

    public ModelPanel getModelPanel() {
        return modelPanel;
    }

    public MaterialButton getButtonAddEdit() {
        return buttonAddEdit;
    }

    public MaterialButton getButtonCancel() {
        return buttonCancel;
    }

    public MaterialButtonIcon getButtonDelete() {
        return buttonDelete;
    }

}
