package ru.anroidapp.plannerwork.intface_procedure;

// Gives index bar view touched Y axis value, position of section and preview text value to list view
public interface IIndexBarFilterProcedure {
    void filterList(float sideIndexY, int position, String previewText);
}
