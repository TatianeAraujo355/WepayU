package br.ufal.ic.p2.wepayu.Exception;

public class NaoPodeDarComandoDeposDeEncerrarSistemaException extends Exception {
    public NaoPodeDarComandoDeposDeEncerrarSistemaException(){
        super("Nao pode dar comandos depois de encerrarSistema.");
    }
}
    
