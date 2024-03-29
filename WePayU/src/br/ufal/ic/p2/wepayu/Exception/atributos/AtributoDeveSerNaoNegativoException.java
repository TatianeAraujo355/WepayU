package br.ufal.ic.p2.wepayu.Exception.atributos;

public class AtributoDeveSerNaoNegativoException extends Exception{
    public AtributoDeveSerNaoNegativoException(String atributo,  boolean Ad){
        super(atributo + (Ad ? " deve ser nao-negativa." : " deve ser nao-negativo."));
    }
}
