package br.ufal.ic.p2.wepayu.folhaPagamento;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import br.ufal.ic.p2.wepayu.Empregados.Assalariado;
import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Empregados.Horista;
import br.ufal.ic.p2.wepayu.Empregados.comissionado.Comissionado;

//
public class GerarFolhaPagamento {

    //variaveis temporarias//
    List<Assalariado> listaAssalariados = new ArrayList<>();
    List<Horista> listaHoristas = new ArrayList<>();
    List<Comissionado> listaComissionados = new ArrayList<>();
    List<Empregado> lista_temp = new ArrayList<>();
    Double TotalFolha = 0.0;
    Double salarioBruto_total = 0.0;
    Double salarioLiquido_total = 0.0;
    Double descontosTotal = 0.0;
    Double fixo_total = 0.0;
    Double comissao_total = 0.0;
    Double vendas_total = 0.0;
    Double horas_total = 0.0;
    Double extras_total = 0.0;
    Double total_folha = 0.00;

    // calcular  todos os empregados pago na data e armazenar nas folhas de pagamento
    public String calcularEmpregados(LocalDate data, LinkedHashMap<String, Empregado> empregados, AgendaPagamentos ag) throws IOException {

        String nome = "folha-" + data + ".txt";

        File arquivo = new File(nome);
        if (!arquivo.exists()) {
            arquivo.createNewFile();
        }

        FileWriter writer = new FileWriter(arquivo);
        LocalDate dt;

        lista_temp = ag.obterEmpregadosPorData(data);
        if(lista_temp != null){

            //separa cada tipo de empregado em listas separadas
        for (Empregado emp : lista_temp) {

            if (emp.getTipo().equals("horista")) {
                Horista emp1 = (Horista) emp;
                listaHoristas.add(emp1);
            } else if (emp.getTipo().equals("assalariado")) {
                Assalariado emp1 = (Assalariado) emp;
                listaAssalariados.add(emp1);
            } else {
                Comissionado emp1 = (Comissionado) emp;
                listaComissionados.add(emp1);
            }
        }
    }
        //percorre as listas de cada tipo de empregado e imprime na folha com o formato correto
        writer.write("FOLHA DE PAGAMENTO DO DIA " + data + "\n");
        writer.write("====================================\n\n");
        
        //ordenar os empregados horistas por nome, e percorrer a lista
        Collections.sort(listaHoristas, Comparator.comparing(Empregado::getNome));
        imprimirforma("HORISTAS", writer);
        for (Horista emp : listaHoristas) {
            if (!listaHoristas.isEmpty()) {
                if (emp.getUltimoPagamento() != null) {
                     dt = emp.getUltimoPagamento();
                horistas(emp, dt, data, writer);
            } else {
                horistas(emp, LocalDate.of(2005, 01, 01), data,  writer);
            }
            emp.setUltimoPagamento(data);
            }
        }
        //imprimir atributos totais dos empregados horista
        writer.write("\n");
        writer.write(String.format("%-35s", "TOTAL HORISTAS "));
        writer.write(String.format("%7s", converterParaString(horas_total).replaceAll("\\,0+$", "")));
        writer.write(String.format("%6s", converterParaString(extras_total).replaceAll("\\,0+$", "")));
        writer.write(String.format("%14s", converterParaString(salarioBruto_total)));
        writer.write(String.format("%10s", converterParaString(descontosTotal)));
        writer.write(String.format("%16s", converterParaString(salarioLiquido_total)));
        writer.write("\n\n");
        total_folha += salarioBruto_total;

        //limpa variaveis temporarias
        horas_total = 0.0;
        extras_total = 0.0;
        salarioBruto_total = 0.0;
        descontosTotal = 0.0;
        salarioLiquido_total = 0.0;

        //Ordena os empregados assalariados por nome, e percorre a lista 
        Collections.sort(listaAssalariados, Comparator.comparing(Empregado::getNome));
        imprimirforma("ASSALARIADOS", writer);

        for (Assalariado emp : listaAssalariados) {
            if (!listaAssalariados.isEmpty()) {
                if (emp.getUltimoPagamento() != null) {
                    dt = emp.getUltimoPagamento();
                    assalariado(emp, dt, data, writer);
                } else {
                    assalariado(emp, LocalDate.of(2005, 01, 01).minusDays(1), data,writer);
                  }
            }
            emp.setUltimoPagamento(data);

        }
        // imprime os atributos totais dos empregados assalariados
        writer.write("\n");
        writer.write(String.format("%-48s", "TOTAL ASSALARIADOS"));
        writer.write(String.format("%14s", converterParaString(salarioBruto_total)));
        writer.write(String.format("%10s", converterParaString(descontosTotal)));
        writer.write(String.format("%16s", converterParaString(salarioLiquido_total)));
        total_folha += salarioBruto_total;

        //limpa variaveis temporarias
        salarioBruto_total = 0.0;
        descontosTotal = 0.0;
        salarioLiquido_total = 0.0;
        writer.write("\n\n");

        //Ordena os comissionados por nome e percorre a lista
        Collections.sort(listaComissionados, Comparator.comparing(Empregado::getNome));
        imprimirforma("COMISSIONADOS", writer);

        for (Comissionado emp : listaComissionados) {
            if (!listaComissionados.isEmpty()) {
                if (emp.getUltimoPagamento() != null) {
                    dt = emp.getUltimoPagamento();
                    comissionado(emp, dt, data, writer);
                } else {
                    comissionado(emp, LocalDate.of(2005, 01, 01), data, writer);
                }
                emp.setUltimoPagamento(data);
            }
        }
        // imprime na folha os atributos totais dos comissionados
        writer.write(String.format("%-20s", "\nTOTAL COMISSIONADOS "));
        writer.write(String.format("%10s", converterParaString(fixo_total)));
        writer.write(String.format("%9s", converterParaString(vendas_total)));
        writer.write(String.format("%9s", converterParaString(comissao_total)));
        writer.write(String.format("%14s", converterParaString(salarioBruto_total)));
        writer.write(String.format("%10s", converterParaString(descontosTotal)));
        writer.write(String.format("%16s", converterParaString(salarioLiquido_total)));
        total_folha += salarioBruto_total;
        //limpa variaveis
        fixo_total = 0.0;
        vendas_total = 0.0;
        comissao_total = 0.0;
        salarioBruto_total = 0.0;
        descontosTotal = 0.0;
        salarioLiquido_total = 0.0;

        // imprime o calculo total da folha do dia especificado
        writer.write("\n\nTOTAL FOLHA: " + converterParaString(total_folha) + "\n");
        writer.close();
        //retorna calculo total da folha
        return converterParaString(total_folha);
    }

    //a funcao percorre cada atributo do empregado horista e imprime na folha 
    public void horistas(Horista emp, LocalDate datainicial, LocalDate datafinal, FileWriter writer)
            throws IOException {
        Double a = converterParaDouble(emp.getSalario());
        Double x = converterParaDouble(emp.getHorasTrabalhadas(datainicial, datafinal));
        Double y = converterParaDouble(emp.getHorasExtrasTrabalhadas(datainicial, datafinal));
        Double salarioBruto = (a * x) + ((a * 1.5) * y);
        Double salarioLiquido = salarioBruto;
        Double descontos = 0.00;

        if (emp.getMembroSindicato() != null) {
            a = converterParaDouble(emp.getMembroSindicato().getTaxaSindical());
            x = converterParaDouble(emp.getMembroSindicato().getTaxaServico(datainicial, datafinal));
            descontos = ((a * 7) + x);
            if (emp.getMembroSindicato().getDescontosDevendo() != null
                    && emp.getMembroSindicato().getDescontosDevendo() > 0.0) {
                descontos += emp.getMembroSindicato().getDescontosDevendo();
            }
            if (descontos > salarioBruto) {
                descontos = descontos - salarioBruto;
                emp.getMembroSindicato().setDescontosDevendo(descontos);
                salarioLiquido = 0.00;
                if (salarioBruto == 0) {
                    descontos = 0.00;
                }
            } else {
                salarioLiquido = salarioBruto - descontos;
            }
        }
        //imprime os atributos 
        writer.write(String.format("%-35s", emp.getNome()));
        writer.write(String.format("%7s", emp.getHorasTrabalhadas(datainicial, datafinal)));
        horas_total += converterParaDouble(emp.getHorasTrabalhadas(datainicial, datafinal));
        writer.write(String.format("%6s", emp.getHorasExtrasTrabalhadas(datainicial, datafinal)));
        extras_total += converterParaDouble(emp.getHorasExtrasTrabalhadas(datainicial, datafinal));
        writer.write(String.format("%14s", converterParaString(salarioBruto)));
        salarioBruto_total += salarioBruto;
        writer.write(String.format("%10s", converterParaString(descontos)));
        descontosTotal += descontos;
        writer.write(String.format("%16s", converterParaString(salarioLiquido)) + " ");
        salarioLiquido_total += salarioLiquido;
        if (emp.getMetodoPagamento().getBanco() == null) {
            if (emp.getMetodoPagamento().getTipo().equals("emMaos")) {
                writer.write("Em maos");
            } else {
                writer.write(emp.getMetodoPagamento().getTipo() + " ");
            }
        } else {
            writer.write(emp.getMetodoPagamento().getBanco().getNomeBanco() + ", ");
            writer.write("Ag. " + emp.getMetodoPagamento().getBanco().getAgencia()); 
            writer.write(" CC " + emp.getMetodoPagamento().getBanco().getContaCorrente());
        }
        writer.write("\n");

    }

    // funcao encontra todos os atributos do empregado comissionado e imprime na folha
    public void comissionado(Comissionado emp, LocalDate datainicial, LocalDate datafinal, FileWriter writer)throws IOException {

        Double vendas = converterParaDouble(emp.getVendasRealizadas(datainicial, datafinal));
        Double fixo = 0.0;
        if (emp.getAgendaPagamento().equals("semanal 5")) {
            fixo = converterParaDouble(emp.getSalario()) * 12 / 52;
        } else if (emp.getAgendaPagamento().equals("semanal 2 5")) {
            fixo = converterParaDouble(emp.getSalario()) * 12 / 52 * 2;
        } else if (emp.getAgendaPagamento().equals("mensal $")) {
            fixo = converterParaDouble(emp.getSalario());
        }
        Double taxa_comissao = converterParaDouble(emp.getComissao());
        Double taxa;
        Double descontos = 0.00;
        Double sind = 0.0;
        vendas = Math.floor(vendas * 100) / 100;
        double comissao = taxa_comissao * vendas;
        comissao = Math.floor(comissao * 100) / 100;
        fixo = Math.floor(fixo * 100) / 100;
        Double salarioBruto = Math.floor((fixo + (vendas * taxa_comissao)) * 100) / 100;
        Double salarioLiquido = salarioBruto;

        if (emp.getMembroSindicato() != null) {
            sind = converterParaDouble(emp.getMembroSindicato().getTaxaSindical());
            taxa = converterParaDouble(emp.getMembroSindicato().getTaxaServico(datainicial, datafinal));
            descontos = ((sind * 14) + taxa);
            if (descontos > salarioBruto) {
                descontos = descontos - salarioBruto;
                emp.getMembroSindicato().setDescontosDevendo(descontos);
                salarioLiquido = 0.00;
            } else {
                salarioLiquido = salarioBruto - descontos;
            }
        }

        // imprime na folha
        writer.write(String.format("%-20s", emp.getNome()));
        writer.write(String.format("%10s", converterParaString(fixo)));
        fixo_total += fixo;
        writer.write(String.format("%9s", converterParaString(vendas)));
        vendas_total += vendas;
        writer.write(String.format("%9s", converterParaString(comissao)));
        comissao_total += comissao;
        writer.write(String.format("%14s", converterParaString(salarioBruto)));
        salarioBruto_total += salarioBruto;
        writer.write(String.format("%10s", converterParaString(descontos)));
        descontosTotal += descontos;
        writer.write(String.format("%16s", converterParaString(salarioLiquido)));
        salarioLiquido_total += salarioLiquido;
        if (emp.getMetodoPagamento().getBanco() == null) {
            if (emp.getMetodoPagamento().getTipo().equals("emMaos")) {
                writer.write("Em maos ");
            } else if (emp.getMetodoPagamento().getTipo().equals("correios")) {
                writer.write(" Correios, ");
                writer.write(emp.getEndereco());
            }
        } else {
            writer.write(emp.getMetodoPagamento().getBanco().getNomeBanco() + " ");
            writer.write("Ag. " + emp.getMetodoPagamento().getBanco().getAgencia() + " ");
            writer.write("CC " + emp.getMetodoPagamento().getBanco().getContaCorrente() + " ");
        }
        writer.write("\n");

    }

    // funcao percorre os atributos do empregado assalariado e imprime na folha
    public void assalariado(Assalariado emp, LocalDate datainicial, LocalDate datafinal, FileWriter writer)
            throws IOException {
        Double a;
        Double z;
        Double salarioBruto = 0.0;

        String[] partes = emp.getAgendaPagamento().split(" ");
        if (partes[0].equals("semanal")) {
            if (partes.length == 3) {
                int num = Integer.parseInt(partes[1]);
                salarioBruto = Math.floor(converterParaDouble(emp.getSalario()) * 12 / 52 * num * 100) / 100;
            } else {
                salarioBruto = Math.floor(converterParaDouble(emp.getSalario()) * 12 / 52 * 100) / 100;
            }
        } else if (partes[0].equals("mensal")) {
            salarioBruto = converterParaDouble(emp.getSalario());
        }
        Double salarioLiquido = salarioBruto;
        Double descontos = 0.00;
        long numDias = ChronoUnit.DAYS.between(datainicial, datafinal);

        if (emp.getMembroSindicato() != null) {
            a = converterParaDouble(emp.getMembroSindicato().getTaxaSindical());
            z = converterParaDouble(emp.getMembroSindicato().getTaxaServico(datainicial, datafinal));

            if (emp.getMembroSindicato().getDescontosDevendo() != null && emp.getDescontosDevendo() > 0.0)
                descontos += emp.getDescontosDevendo();

            emp.setDescontosDevendo(0.0);
            descontos += (a * (numDias)) + z;
            if (descontos >= salarioBruto) {
                descontos = descontos - salarioBruto;
                emp.getMembroSindicato().setDescontosDevendo(descontos);
                salarioLiquido = 0.00;
            } else {
                salarioLiquido = salarioBruto - descontos;
            }
        }
        // imprime na folha
        writer.write(String.format("%-48s", emp.getNome()));
        writer.write(String.format("%14s", emp.getSalario()));
        salarioBruto_total += salarioBruto;
        writer.write(String.format("%10s", converterParaString(descontos)));
        descontosTotal += descontos;
        writer.write(String.format("%16s", converterParaString(salarioLiquido)));
        salarioLiquido_total += salarioLiquido;
        if (emp.getMetodoPagamento().getBanco() == null) {
            if (emp.getMetodoPagamento().getTipo().equals("emMaos")) {
                writer.write(" Em maos");
            } else if (emp.getMetodoPagamento().getTipo().equals("correios")) {
                writer.write(" Correios, ");
                writer.write(emp.getEndereco());
            }
        } else {
            writer.write(emp.getMetodoPagamento().getBanco().getNomeBanco() + " ");
            writer.write("Ag. " + emp.getMetodoPagamento().getBanco().getAgencia() + " ");
            writer.write("CC " + emp.getMetodoPagamento().getBanco().getContaCorrente() + " ");
        }
        writer.write("\n");

    }


    //funoes  para ajudar na conversao de valores
    public static double converterParaDouble(String numeroString) {
        String numeroFormatado = numeroString.replace(',', '.');
        double numeroDouble = Double.parseDouble(numeroFormatado);
        return numeroDouble;
    }

    public static String converterParaString(double numeroDouble) {
        DecimalFormat df = new DecimalFormat("#0.00");
        String numeroString = df.format(numeroDouble);
        numeroString = numeroString.replace('.', ',');
        return numeroString;
    }

    // apenas imprimi os modelos da folha de pagamento
    private void imprimirforma(String tipo, FileWriter writer) throws IOException {
        writer.write(
                "===============================================================================================================================\n");

        if (tipo.equals("HORISTAS")) {
            writer.write("===================== " + tipo
                    + " ================================================================================================\n");
            writer.write(
                    "===============================================================================================================================\n");
            writer.write(
                    "Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo\n");
            writer.write(
                    "==================================== ===== ===== ============= ========= =============== ======================================\n");
        } else if (tipo.equals("COMISSIONADOS")) {
            writer.write("===================== " + tipo
                    + " ===========================================================================================\n");
            writer.write(
                    "===============================================================================================================================\n");
            writer.write(
                    "Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo\n");
            writer.write(
                    "===================== ======== ======== ======== ============= ========= =============== ======================================\n");
        } else {
            writer.write("===================== " + tipo
                    + " ============================================================================================\n");
            writer.write(
                    "===============================================================================================================================\n");
            writer.write(
                    "Nome                                             Salario Bruto Descontos Salario Liquido Metodo\n");
            writer.write(
                    "================================================ ============= ========= =============== ======================================\n");
        }

    }

}
