package br.ufal.ic.p2.wepayu.Empregados.metodoPagamento;

public class Banco {
    private String agencia;
    private String contaCorrente;
    private String nomeBanco;

    public Banco(String nomeBanco, String agencia, String contaCorrente) {
        this.nomeBanco = nomeBanco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    public void setBanco(String nomeBanco, String agencia, String contaCorrente) {
        this.nomeBanco = nomeBanco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public String getNomeBanco() {
        return nomeBanco;
    }
}
