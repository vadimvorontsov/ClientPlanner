package ru.anroidapp.plannerwork.record.contact_choose.intface;

// Gives index bar view touched Y axis value, position of section and preview text value to list view
public interface IIndexBarFilter {
    void filterList(float sideIndexY, int position, String previewText);
}
