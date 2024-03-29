package br.ufal.ic.p2.wepayu.Empregados.comissionado;

import java.time.LocalDate;

public class Venda {
    String Idvendedor;
    LocalDate data;
    String valor;

   public Venda(String id, LocalDate data, String valor){
        this.Idvendedor = id;
        this.data = data;
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public String getValor() {
        return valor;
    }
}
