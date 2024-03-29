package br.ufal.ic.p2.wepayu.Exception.empregado;

public class EmpregadoNaoEhDoTipoException extends Exception {
    public EmpregadoNaoEhDoTipoException(String atributo){
        super("Empregado nao eh " + atributo);
    }
}
