package br.ufal.ic.p2.wepayu.Exception.dataE;

public class DataInvalidaException extends Exception {
   public  DataInvalidaException(String atributo){
      super("Data" + atributo + "invalida.");
   }
}
