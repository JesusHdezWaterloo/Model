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
package com.root101.swing.models.detail;

import com.root101.clean.core.app.services.ExceptionHandler;
import com.root101.clean.core.domain.DomainObject;
import com.root101.export.utils.DomainListFileReader;
import com.root101.swing.material.components.button.MaterialButton;
import com.root101.swing.material.components.button.MaterialButtonsFactory;
import com.root101.swing.material.components.filechooser.FileDropHandler;
import com.root101.swing.material.components.table.Column;
import com.root101.swing.material.standards.MaterialIcons;
import com.root101.swing.models.utils.DefaultExportableConfig;
import com.root101.swing.util.AbstractActionUtils;
import com.root101.utils.services.ConverterService;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 *
 * @author Root101 (jhernandezb96@gmail.com, +53-5-426-8660)
 * @author JesusHdezWaterloo@Github
 */
public abstract class _MaterialPanelDetailDragDrop<T extends DomainObject> extends _MaterialPanelDetail<T> {

    private final DomainListFileReader<T> reader;

    private DefaultExportableConfig exportConfig;

    public _MaterialPanelDetailDragDrop(DomainListFileReader<T> reader) {
        this(reader, new Column[]{});
    }

    public _MaterialPanelDetailDragDrop(Column... arr) {
        this(null, arr);
    }

    public _MaterialPanelDetailDragDrop(DomainListFileReader<T> reader, Column... arr) {
        super(arr);
        this.reader = reader;
        personalize();
    }

    public void setButtonAddTransferConsumer(DomainListFileReader<T> reader) {
        header.getButtonAdd().setTransferHandler(new FileDropHandler((List<File> t) -> {
            try {
                insertAll(reader.read(t));
            } catch (Exception e) {
                ExceptionHandler.handleException(e);
            }
        }));
        header.getButtonAdd().setToolTipText("Arrastre algún fichero para importar");
    }

    public void insertAll(List<T> newDomains) {
        JOptionPane.showMessageDialog(null, "Importando multiples objetos.\n" + newDomains);
    }

    private void personalize() {
        MaterialButton exportButton = MaterialButtonsFactory.buildPopup(getPopupAllExportSupported());
        exportButton.setText("Exportar");
        exportButton.setIconTextGap(10);
        exportButton.setIcon(MaterialIcons.EXPORT);
        exportButton.setToolTipText("Exportar a diferentes formato. Click para desplegar TODAS las opciones de exportación.");
        addOptionElement(exportButton);

        //reader para el boton de add
        if (reader != null) {
            setButtonAddTransferConsumer(reader);
        }

        //config por defecto para exportar
        exportConfig = new DefaultExportableConfig(this);
    }

    public DefaultExportableConfig getExportConfig() {
        return exportConfig;
    }

    public void setExportConfig(DefaultExportableConfig expConfig) {
        this.exportConfig = expConfig;
    }

    private void onExportToExcelAction() {
        try {
            exportConfig.exportExcelBuilder().write().open();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void onExportToExcelSelectedAction() {
        try {
            exportConfig.exportExcelBuilder()
                    .values(ConverterService.convert(getSelectedList(), exportConfig::getRowObjectExport))
                    .write().open();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void onExportToJSONAction() {
        try {
            exportConfig.exportJSONBuilder().write().open();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private void onExportToJSONSelectedAction() {
        try {
            exportConfig.exportJSONBuilder()
                    .values(ConverterService.convert(exportConfig.getColumnNamesExport(), getSelectedList(), exportConfig::getRowObjectExport))
                    .write().open();
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private List<Action> getPopupAllExportSupported() {
        List<Action> actions = new ArrayList<>();
        
        //accion de exportarlo todo a excel
        actions.add(AbstractActionUtils.from(
                "Todo",
                MaterialIcons.EXCEL.deriveIcon(24f),
                "Exportar toda la lista a Excel",
                (ActionEvent e) -> {
                    onExportToExcelAction();
                }));
        //accion de exportar lo seleccionado a excel
        actions.add(AbstractActionUtils.from(
                "Selec.",
                MaterialIcons.EXCEL.deriveIcon(24f),
                "Exportar los elementos seleccionados a Excel",
                (ActionEvent e) -> {
                    onExportToExcelSelectedAction();
                }));

        //accion de exportarlo todo a JSON
        actions.add(AbstractActionUtils.from("Todo",
                MaterialIcons.TEC_JSON.deriveIcon(24f),
                "Exportar toda la lista a JSON",
                (ActionEvent e) -> {
                    onExportToJSONAction();
                }));
        //accion de exportar lo seleccionado a JSON
        actions.add(AbstractActionUtils.from("Selec.",
                MaterialIcons.TEC_JSON.deriveIcon(24f),
                "Exportar los elementos seleccionados a JSON",
                (ActionEvent e) -> {
                    onExportToJSONSelectedAction();
                }));

        return actions;
    }

}
