package br.ufal.ic.p2.wepayu.Exception.atributos;

public class AtributoNaoExisteException extends Exception {
    public AtributoNaoExisteException(String parametro){
        super(parametro + " nao existe.");
    }
}
