package br.ufal.ic.p2.wepayu.Exception.atributos;

public class AtributoDeveSerPosivitoException extends Exception {
    public AtributoDeveSerPosivitoException(String parametro){
        super(parametro + " deve ser positivo.");
    }
}
