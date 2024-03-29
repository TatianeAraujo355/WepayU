package br.ufal.ic.p2.wepayu.Empregados.metodoPagamento;

public class MetodoPagamento {
    String tipo;
    Banco banco;

    public MetodoPagamento (){
        this.tipo = "emMaos";
    }

    public String getTipo() {
        return this.tipo;
    }

    public Banco getBanco() {
        return this.banco;
    }

    public void setTipo (String tipo){
        if(tipo.equals("emMaos")){
            banco = null;
            this.tipo = "emMaos";
        }
        else if(tipo.equals("correios")){
            banco = null;
            this.tipo = "correios";
        }
    }

    public void setBanco(Banco bank){
        this.banco = bank;
        this.tipo = "banco";
    }

  

    
}
