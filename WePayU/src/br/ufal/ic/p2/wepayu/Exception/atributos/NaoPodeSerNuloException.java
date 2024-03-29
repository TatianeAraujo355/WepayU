package br.ufal.ic.p2.wepayu.Exception.atributos;

public class NaoPodeSerNuloException extends Exception{
    public NaoPodeSerNuloException(String parametro, boolean addA){
        super(parametro + (addA ? " nao pode ser nula." : " nao pode ser nulo."));
    }
    
}
