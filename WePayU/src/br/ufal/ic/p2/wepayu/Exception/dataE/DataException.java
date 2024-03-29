package br.ufal.ic.p2.wepayu.Exception.dataE;

public class DataException extends Exception{
   public DataException(){
    super("Data inicial nao pode ser posterior aa data final.");
   }
}
