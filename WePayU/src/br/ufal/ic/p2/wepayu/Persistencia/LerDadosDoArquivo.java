package br.ufal.ic.p2.wepayu.Persistencia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import br.ufal.ic.p2.wepayu.Empregados.Assalariado;
import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Empregados.Horista;
import br.ufal.ic.p2.wepayu.Empregados.MembroSindicato;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Comissionado;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Venda;
import br.ufal.ic.p2.wepayu.Empregados.metodoPagamento.Banco;
import br.ufal.ic.p2.wepayu.Empregados.metodoPagamento.MetodoPagamento;

// ler dados dos arquivos//
public class LerDadosDoArquivo {

    public LinkedHashMap<String, Empregado> carregarEmpregadosDoArquivo(String nomeArquivo) throws IOException {
        LinkedHashMap<String, Empregado> empregados = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {

            if (reader.ready()) {
                String linha;
                Empregado empregado = null;
                // variaveis pra armazenar informacoes temporarias
                String Temporario = null;
                String Temp = null;
                String varT = null;
                String Tipotemporario = null;

                while ((linha = reader.readLine()) != null) {
                    if (linha.isEmpty()) { // Nova linha em branco indica o fim de um registro
                        if (empregado != null) {
                            empregados.put(empregado.getId(), empregado);
                        }
                        empregado = null; // Reinicia para o proximo registro
                    } else {
                        String[] partes = linha.split(": ");
                        if (partes.length < 2) {
                            continue; // Ignorar linhas que nao contem o delimitador esperado
                        }
                        String chave = partes[0].trim();
                        String valor = partes[1].trim();

                        if ("id".equals(chave)) {
                            Temporario = valor;
                        } else if ("nome".equals(chave)) {
                            Temp = valor;
                        } else if ("endereco".equals(chave)) {
                            varT = valor;
                        } else if ("tipo".equals(chave)) {
                            Tipotemporario = valor;
                        } else if ("salario".equals(chave)) {
                            if (Tipotemporario.equals("horista")) {
                                empregado = new Horista(Temporario, Temp, varT, valor);
                            } else if (Tipotemporario.equals("comissionado")) {
                                empregado = new Comissionado(Temporario, Temp, varT, valor, "0");
                            } else if (Tipotemporario.equals("assalariado")) {
                                empregado = new Assalariado(Temporario, Temp, varT, valor);
                            }
                            Temporario = null;
                            Temp = null;
                            varT = null;
                            Tipotemporario = null;
                        } else if ("Idsindicato".equals(chave)) {
                            if (!valor.isEmpty()) {
                                Temporario = valor;
                            }
                        } else if ("taxaSindical".equals(chave)) {
                            if (!valor.isEmpty() && empregado != null) {
                                MembroSindicato membro = new MembroSindicato(Temporario, valor);
                                empregado.setMembroSindicato(membro);
                                Temporario = null;
                            }
                        } else if ("comissao".equals(chave)) {
                            if (!valor.isEmpty() && empregado instanceof Comissionado) {
                                Comissionado emp = (Comissionado) empregado;
                                emp.setComissao(valor);
                            }
                        } else if ("MetodoPagamento".equals(chave)) {
                            MetodoPagamento metodoPagamento = new MetodoPagamento();
                            metodoPagamento.setTipo(valor);
                            empregado.setMetodoPagamento(metodoPagamento);
                        } else if ("banco".equals(chave)) {
                            Temporario = valor; // armazena nome banco
                        } else if ("agencia".equals(chave)) {
                            Tipotemporario = valor; // armazena agencia
                        } else if ("contaCorrente".equals(chave)) {
                            Banco b = new Banco(Temporario, Tipotemporario, valor);
                            empregado.getMetodoPagamento().setBanco(b);
                            Temporario = null;
                            Tipotemporario = null;
                        } else if ("agendaPagamento".equals(chave)) {
                            empregado.setAgendaPagamento(valor);
                        }
                    }
                }

                // Adiciona o ultimo empregado, caso exista
                if (empregado != null) {
                    empregados.put(empregado.getId(), empregado);
                }
            }
        }
       // limparArquivo(nomeArquivo);
        return empregados;
    }

    public LinkedHashMap<String, Empregado> lerHoras(LinkedHashMap<String, Empregado> empregados) throws IOException {

        String nomeArquivo = "./Database/HorasTrabalhadas.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            LocalDate data = null;
            Horista emp = null;
            String id;

            while ((linha = reader.readLine()) != null) {
                if (linha.isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(": ");
                if ("Id".equals(partes[0])) {
                    id = partes[1].trim();
                    emp = (Horista) empregados.get(id);
                } else {
                    partes = linha.split(" / ");
                    for (String parte : partes) {
                        if (parte.startsWith("Data")) {
                            String[] dataPartes = parte.split(": ");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            data = LocalDate.parse(dataPartes[1], formatter);
                        } else if (parte.startsWith(" Horas normais")) {
                            String[] horasNormaisPartes = parte.split(": ");
                            emp.setHorasNormaisTrabalhadas(data, horasNormaisPartes[1]);
                        } else if (parte.startsWith("Horas Extras")) {
                            String[] horasExtrasPartes = parte.split(": ");
                            emp.setHorasExtrasTrabalhadas(data, horasExtrasPartes[1]);
                        }
                    }
                }
            }
           // limparArquivo(nomeArquivo);
            return empregados;
        }
    }

    public LinkedHashMap<String, Empregado> lerVendas(LinkedHashMap<String, Empregado> empregados) throws IOException {

        String nomeArquivo = "./Database/Vendas.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            LocalDate data = null;
            Comissionado emp = null;
            String id;

            while ((linha = reader.readLine()) != null) {
                if (linha.isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(": ");
                if ("Id".equals(partes[0])) {
                    id = partes[1].trim();
                    emp = (Comissionado) empregados.get(id);
                } else {
                    partes = linha.split(" / ");
                    for (String parte : partes) {
                        if (parte.startsWith("Data:")) {
                            String[] dataPartes = parte.split(": ");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            data = LocalDate.parse(dataPartes[1], formatter);
                        } else if (parte.startsWith("Valor:")) {
                            String[] part = parte.split(": ");
                            String valor = part[1]; // Obtendo o valor como String
                            Venda v = new Venda(emp.getId(), data, valor);
                            emp.adicionarVenda(v);

                        }
                    }
                }
            }
           // limparArquivo(nomeArquivo);
            return empregados;
        }
    }

    public LinkedHashMap<String, Empregado> lerTaxas(LinkedHashMap<String, Empregado> empregados) throws IOException {

        String nomeArquivo = "./Database/TaxaDeServico.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            LocalDate data = null;
            Empregado emp = null;
            String id;

            while ((linha = reader.readLine()) != null) {
                if (linha.isEmpty()) {
                    continue;
                }
                String[] partes = linha.split(": ");
                if ("Id".equals(partes[0])) {
                    id = partes[1].trim();
                    emp = empregados.get(id);
                } else {
                    partes = linha.split(" / ");
                    for (String parte : partes) {
                        if (parte.startsWith("Data:")) {
                            String[] dataPartes = parte.split(": ");
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            data = LocalDate.parse(dataPartes[1], formatter);
                        } else if (parte.startsWith("Valor:")) {
                            String[] part = parte.split(": ");
                            String valor = part[1]; // Obtendo o valor como String
                            emp.getMembroSindicato().setTaxaServico(data, valor);
                        }
                    }
                }
            }
           // limparArquivo(nomeArquivo);
            return empregados;
        }
    }

    public List<String> lerListaAgendasDoArquivo() throws IOException {
        String nomeArquivo = "./Database/Agenda.txt";
        List<String> agenda = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.equals("mensal $") && !linha.equals("semanal 5") && !linha.equals("semanal 2 5")) {
                    agenda.add(linha);
                }
            }
        }
        return agenda;
    }

    
    public void limparArquivo(String nomeArquivo) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo));
       // writer.print("");
        writer.write("");
        writer.flush();
        writer.close();
    }


}
