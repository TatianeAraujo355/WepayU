package br.ufal.ic.p2.wepayu.Empregados;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MembroSindicato {

    private String idSindicato;
    private String taxaSindical;
    public Double descontosDevendo;
    private Map<LocalDate, String> taxaServico =  new HashMap<>();;

    public MembroSindicato(String idSindicato, String taxa) {
        this.idSindicato = idSindicato;
        this.taxaSindical = taxa;
    }

    public String getIdSindicato() {
        return idSindicato;
    }

    public String getTaxaSindical() {
        return taxaSindical;
    }

    public void removeTaxaServico(LocalDate data){
        if (!taxaServico.isEmpty()) {
            taxaServico.remove(data);
        }
    }

    public Double getDescontosDevendo() {
        return descontosDevendo;
    }

    public void setDescontosDevendo(Double descontosDevendo) {
        this.descontosDevendo = descontosDevendo;
    }

    public void setTaxaServico(LocalDate data, String taxa) {
        if (taxaServico == null) {
            taxaServico = new HashMap<>();
        }
        taxaServico.put(data, taxa);
    }

    public String getTaxaServico(LocalDate dataInicial, LocalDate dataFinal) {
        double total = 0;
        LocalDate data;
        Double valor;

        if(taxaServico != null){
            for (Map.Entry<LocalDate, String> entry : taxaServico.entrySet()) {
                 data = entry.getKey();
                String value = entry.getValue();
                valor = Double.parseDouble(value.replace(",", "."));
                // Verifica se a data esta dentro do intervalo especificado
                if (!data.isBefore(dataInicial) && data.isBefore(dataFinal)) {
                    total += valor;
                }
            }
    
            //define total vendas em string e retorna//
            String totalString = String.format("%.2f", total);
            totalString = totalString.replace('.', ',');
    
            return totalString;
        }
        return "0,00";
    }

    public Map<LocalDate, String> listaTaxaServico(){
        return this.taxaServico;
    }
}
