package br.ufal.ic.p2.wepayu.Exception;

public class NaoHaComandoaDesfaze extends Exception{
    public NaoHaComandoaDesfaze (String atributo){
        super("Nao ha comando a " + atributo);
    }
}
