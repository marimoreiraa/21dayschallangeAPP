package com.ifmg.a21dayschallangeapp;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.List;

// Esta classe converte o valor X (que é um float/int, o índice da lista) na string da data.
public class DateAxisFormatter extends ValueFormatter {

    private final List<String> dates; // Lista de strings de data (Ex: "2025-12-13")

    public DateAxisFormatter(List<String> dates) {
        this.dates = dates;
    }

    @Override
    public String getFormattedValue(float value) {
        // Converte o float/int (índice) para String da data
        int index = (int) value;
        if (index >= 0 && index < dates.size()) {
            // Retorna a data no formato DD/MM (simplificado para o gráfico)
            String fullDate = dates.get(index);

            // Retorna apenas DD/MM
            if (fullDate.length() >= 10) {
                return fullDate.substring(8, 10) + "/" + fullDate.substring(5, 7);
            }
            return fullDate;
        }
        return "";
    }
}