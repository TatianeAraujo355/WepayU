package br.ufal.ic.p2.wepayu.Sistema;

import java.util.ArrayList;
import java.util.List;

import br.ufal.ic.p2.wepayu.Exception.NaoHaComandoaDesfaze;
import br.ufal.ic.p2.wepayu.Exception.NaoPodeDarComandoDeposDeEncerrarSistemaException;

public class Invoker { 
    
    public static final List<Command> ListaUNDO = new ArrayList<>();
    private static final List<Command> ListaREDO = new ArrayList<>();
    public  List<Command> commands = new ArrayList<>();
    public  boolean encerrarSistema = false;

    public void executeCommand(Command command) {
        command.execute();
        ListaUNDO.add(command);
    }

    public void undoLastCommand() throws NaoPodeDarComandoDeposDeEncerrarSistemaException, NaoHaComandoaDesfaze {
        if(encerrarSistema){
            throw new NaoPodeDarComandoDeposDeEncerrarSistemaException();
        }
        if (!ListaUNDO.isEmpty()) {
            Command ultimoComando = ListaUNDO.remove(ListaUNDO.size()-1);
            ListaREDO.add(ultimoComando);
            ultimoComando.undo();
        }else{
            throw new NaoHaComandoaDesfaze("desfazer.");
        }
    }
    public void redoLastUndoneCommand() throws NaoHaComandoaDesfaze {
        if (!ListaREDO.isEmpty()) {
            Command ultimoComando = ListaREDO.remove(ListaREDO.size()-1);
            ultimoComando.execute();
            ListaUNDO.add(ultimoComando);
           
        } else {
            throw new  NaoHaComandoaDesfaze("refazer.");
        }
    }

    public void limparLista() {
        if (!ListaUNDO.isEmpty()) {
            commands.clear();
            commands.addAll(ListaUNDO);
            ListaUNDO.clear();
        }
    }
    public void desfazL(){
        ListaUNDO.addAll(commands);
        commands.clear();
    }

}