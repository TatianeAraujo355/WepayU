package br.ufal.ic.p2.wepayu.Sistema;

public interface Command {

    public void execute();
    
    public void undo();

    public void redo();

}
