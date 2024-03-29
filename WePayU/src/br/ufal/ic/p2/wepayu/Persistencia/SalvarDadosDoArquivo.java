package br.ufal.ic.p2.wepayu.Persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Empregados.Horista;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Comissionado;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Venda;

//salva dados nos arquivos//
public class SalvarDadosDoArquivo {
    public SalvarDadosDoArquivo(LinkedHashMap<String, Empregado> empregados, List<String> Agenda) throws IOException {
        salvarEmpregadosEmArquivo(empregados);
        salvarHorasTrabalhadasEmArquivo(empregados);
        salvarTaxaServicoEmArquivo(empregados);
        salvarVendasEmArquivo(empregados);
        if(!Agenda.isEmpty()){
            salvarAgenda(Agenda);
        }
    }

    public void salvarEmpregadosEmArquivo(LinkedHashMap<String, Empregado> empregados) throws IOException {
        String nome = "./database/Empregados.txt";

        File arquivo = new File(nome);
        if (!arquivo.exists()) {
            arquivo.createNewFile();
        }
        limparArquivo(nome);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(nome))) {

            for (Map.Entry<String, Empregado> entrada : empregados.entrySet()) {
                String chave = entrada.getKey();
                Empregado empregado = entrada.getValue();
                writer.write("id: " + chave + "\n");
                writer.write("nome: " + empregado.getNome() + "\n");
                writer.write("endereco: " + empregado.getEndereco() + "\n");
                writer.write("tipo: " + empregado.getTipo() + "\n");
                writer.write("salario: " + empregado.getSalario() + "\n");
                writer.write("Idsindicato: "
                        + (empregado.getMembroSindicato() != null ? empregado.getMembroSindicato().getIdSindicato()
                                : "")
                        + "\n");
                writer.write("taxaSindical: "
                        + (empregado.getMembroSindicato() != null ? empregado.getMembroSindicato().getTaxaSindical()
                                : "")
                        + "\n");
                if (empregado.getTipo().equals("comissionado")) {
                    Comissionado emp1 = (Comissionado) empregado;
                    writer.write("comissao: " + emp1.getComissao() + "\n");
                }
                writer.write("MetodoPagamento: "
                        + (empregado.getMetodoPagamento() != null ? empregado.getMetodoPagamento().getTipo() : "")
                        + "\n");
                if (empregado.getMetodoPagamento() != null && empregado.getMetodoPagamento().getBanco() != null) {
                    writer.write("banco: " +
                            empregado.getMetodoPagamento().getBanco().getNomeBanco() + "\n");
                    writer.write("agencia: " +
                            empregado.getMetodoPagamento().getBanco().getAgencia() + "\n");
                    writer.write("contaCorrente: " +
                            empregado.getMetodoPagamento().getBanco().getContaCorrente() + "\n");
                }
                writer.write("agendaPagamento: " + empregado.getAgendaPagamento() + "\n");
                writer.write("\n");
            }
        }
    }

    public void salvarHorasTrabalhadasEmArquivo(LinkedHashMap<String, Empregado> empregados) throws IOException {

        String nome = "./database/HorasTrabalhadas.txt";
        limparArquivo(nome);

        try (FileWriter arq = new FileWriter(nome);
                PrintWriter writer = new PrintWriter(arq)) {


            Empregado empregado;

            for (String chave : empregados.keySet()) {
                empregado = empregados.get(chave);
                if (empregado.getTipo().equals("horista")) {
                    Horista emp = (Horista) empregado;
                    if (!(emp.ListaHorasNormais().isEmpty())) {

                        Map<LocalDate, String> horasnormais = emp.ListaHorasNormais();
                        Map<LocalDate, String> horasextras = emp.ListaHorasExtras();

                        writer.write("Id: " + chave + "\n");

                        for (LocalDate dt : horasnormais.keySet()) {
                            String x = horasnormais.get(dt);
                            String y = horasextras.get(dt);

                            writer.write("Data: " + dt + " /  Horas normais: " + x + " / ");
                            writer.write("Horas Extras: " + (y != null ? y : "0") + "\n");
                        }
                        writer.write("\n");
                    }
                }
            }
        }
    }

    public void salvarVendasEmArquivo(LinkedHashMap<String, Empregado> empregados) throws IOException {

        String nome = "./database/Vendas.txt";
        limparArquivo(nome);

        try (FileWriter arq = new FileWriter(nome);
                PrintWriter writer = new PrintWriter(arq)) {

            Empregado empregado;

            for (String chave : empregados.keySet()) {
                empregado = empregados.get(chave);
                if (empregado.getTipo().equals("comissionado")) {
                    Comissionado emp = (Comissionado) empregado;
                    if (!(emp.listaVendas().isEmpty())) {

                        List<Venda> Vendas = emp.listaVendas();

                        writer.write("Id: " + chave + "\n");

                        for (Venda v : Vendas) {
                            LocalDate x = v.getData();
                            String y = v.getValor();

                            writer.write("Data: " + x + " / Valor: " + y + "\n");
                        }
                        writer.write("\n");
                    }
                }
            }
        }
    }

    public void salvarTaxaServicoEmArquivo(LinkedHashMap<String, Empregado> empregados) throws IOException {

        String nome = "./database/TaxaDeServico.txt";
        limparArquivo(nome);

        try (FileWriter arq = new FileWriter(nome);
                PrintWriter writer = new PrintWriter(arq)) {

            Empregado empregado;

            for (String chave : empregados.keySet()) {
                empregado = empregados.get(chave);
                if (empregado.getMembroSindicato() != null
                        && empregado.getMembroSindicato().listaTaxaServico() != null) {

                    if (!(empregado.getMembroSindicato().listaTaxaServico().isEmpty())) {

                        Map<LocalDate, String> taxas = empregado.getMembroSindicato().listaTaxaServico();

                        writer.write("Id: " + chave + "\n");

                        for (LocalDate dt : taxas.keySet()) {
                            String tx = taxas.get(dt);

                            writer.write("Data: " + dt + " / Valor: " + tx + "\n");
                        }
                        writer.write("\n");
                    }
                }
            }
        }
    }

    public void salvarAgenda(List<String> agenda) throws IOException {

        String nome = "./database/Agenda.txt";
        limparArquivo(nome);

        try (FileWriter arq = new FileWriter(nome);
                PrintWriter writer = new PrintWriter(arq)) {

            for (String i : agenda) {
                writer.write(i + "\n");
            }
        }
                   
    }

    public void limparArquivo(String nomeArquivo) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo));
       // writer.print("");
        writer.write("");
        writer.flush();
        writer.close();
    }

}
