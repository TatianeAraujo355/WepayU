package br.ufal.ic.p2.wepayu.Exception.atributos;

public class AtributoDeveSerNumericoException extends Exception {
    public AtributoDeveSerNumericoException(String parametro, boolean addA){
        super(parametro + (addA ? " deve ser numerica." : " deve ser numerico."));
    }
}
