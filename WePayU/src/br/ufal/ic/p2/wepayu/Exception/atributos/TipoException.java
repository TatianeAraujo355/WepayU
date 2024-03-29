package br.ufal.ic.p2.wepayu.Exception.atributos;

public class TipoException extends Exception {
    public TipoException(boolean ad){
        super(ad ? "Tipo invalido." : "Tipo nao aplicavel.");
    }
}
