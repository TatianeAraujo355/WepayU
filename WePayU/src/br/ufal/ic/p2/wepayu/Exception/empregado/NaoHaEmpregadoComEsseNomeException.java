package br.ufal.ic.p2.wepayu.Exception.empregado;

public class NaoHaEmpregadoComEsseNomeException extends Exception{
    public NaoHaEmpregadoComEsseNomeException (){
        super("Nao ha empregado com esse nome.");
    }
}
