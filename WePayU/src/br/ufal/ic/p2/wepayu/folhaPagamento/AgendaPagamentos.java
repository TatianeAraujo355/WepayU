package br.ufal.ic.p2.wepayu.folhaPagamento;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufal.ic.p2.wepayu.Empregados.Empregado;
import br.ufal.ic.p2.wepayu.Exception.AgendaPagamento.DescricaoAgendaInvalida;

public class AgendaPagamentos {
    //mantem os tipo de pagamentos
    public static List<String> listaTiposdePagamentos = new ArrayList<>();
    private static HashMap<String, String> tipoPagamentoDosEmpregados = new HashMap<>();
    private  static HashMap<LocalDate, List<Empregado>> CalendarioDePagamentos = new HashMap<>();

    public AgendaPagamentos() {
        //forma padrao
        listaTiposdePagamentos.add("mensal $");
        listaTiposdePagamentos.add("semanal 5");
        listaTiposdePagamentos.add("semanal 2 5");
        
    }

    public void limparAgenda(){
        CalendarioDePagamentos.clear();
        tipoPagamentoDosEmpregados.clear();
        listaTiposdePagamentos.clear();
    }

    public HashMap<LocalDate, List<Empregado>> getCalendarioDePagamentos() {
        return CalendarioDePagamentos;
    }

    // retorna a lista de empregados que recebem em uma determinada data
    public List<Empregado> obterEmpregadosPorData(LocalDate data) {
        if (CalendarioDePagamentos.containsKey(data)) {
            return CalendarioDePagamentos.get(data);
        } else {
            return null;
        }
    }

    public void agendaEmp(Empregado emp, String agenda) {
        if (verificarlistaTiposdePagamentos(agenda)) {

            //mantem o tipo de pagamento dos empregados para facilitar na busca do empregado no calendario
            if(tipoPagamentoDosEmpregados.get(emp.getId()) != null){
                excluirEmpregadoDoCalendario(emp);
            }
            tipoPagamentoDosEmpregados.put(emp.getId(), agenda);
            criarTipoAgendadePagamentosDoEmpregado(emp, agenda);
            return;
        }

        throw new IllegalArgumentException("Agenda de pagamento nao esta disponivel");
    }


    // cria tipo de agenda de pagamento com as especificacoes pedidas
    public boolean criarTipoAgenda(String entrada) throws DescricaoAgendaInvalida {

        if (verificarlistaTiposdePagamentos(entrada)) {
            throw new IllegalArgumentException("Agenda de pagamentos ja existe");
        }
        String[] partes = entrada.split(" ");
        if (partes.length == 2) {
            int numero1 = Integer.parseInt(partes[1]);
            if (partes[0].equals("semanal")) {
                if (numero1 >= 1 && numero1 <= 7) {
                    listaTiposdePagamentos.add(entrada);
                    return true;
                } else {
                    throw new DescricaoAgendaInvalida();
                }
            } else if (partes[0].equals("mensal")) {
                if (numero1 >= 1 && numero1 <= 28) {
                    listaTiposdePagamentos.add(entrada);
                    return true;
                } else {
                    throw new DescricaoAgendaInvalida();
                }
            } else {
                throw new DescricaoAgendaInvalida();
            }
        } else if (partes.length == 3) {
            int numero1 = Integer.parseInt(partes[1]);
            int numero2 = Integer.parseInt(partes[2]);
            if (partes[0].equals("semanal")) {
                if (numero1 >= 1 && numero1 <= 52 && numero2 >= 1 && numero2 <= 7) {
                    listaTiposdePagamentos.add(entrada);
                    return true;
                } else {
                    throw new DescricaoAgendaInvalida();
                }
            } else {
                throw new DescricaoAgendaInvalida();
            }
        } else {
            throw new DescricaoAgendaInvalida();
        }
    }

    // verifica se o tipo de pagamento ja existe
    public boolean verificarlistaTiposdePagamentos(String v) {
        if (listaTiposdePagamentos.isEmpty()){
            return false;
        }
        for (String item : listaTiposdePagamentos) {
            if (item.equals(v)) {
                return true;
            }
        }
        return false;
    }


    //cria a agenda de pagamento do empregado e adiciona no calendario de pagamentos
    public LocalDate criarTipoAgendadePagamentosDoEmpregado(Empregado emp, String tipo) {

        if (tipo.equals("mensal #") || tipo.equals("mensal $")) {
            List<LocalDate> lista = ultimoDiaUtilPorMes(2005, 31);
            adicionarEmpregadoAoCalendariodePagamentos(lista, emp);
        } else {
            String[] partes = tipo.split(" ");

            if (partes.length == 2) {

                int dia = Integer.parseInt(partes[1]);
                if (partes[0].equals("mensal")) {
                    List<LocalDate> lista = ultimoDiaUtilPorMes(2005, dia);
                    adicionarEmpregadoAoCalendariodePagamentos(lista, emp);
                } else {
                    List<LocalDate> lista = criarTipoAgendaSemanal(dia);
                    adicionarEmpregadoAoCalendariodePagamentos(lista, emp);
                }
            } else if (partes.length == 3) { // data e semanal a cada semana n semanas nos dias m da semana
                int numSemanas = Integer.parseInt(partes[1]);
                int diaDaSemana = Integer.parseInt(partes[2]);

                List<LocalDate> lista = criarAgendaSemanalComPeriodicidade(diaDaSemana, numSemanas, 2005);
                adicionarEmpregadoAoCalendariodePagamentos(lista, emp);
            }

        }

        return null;
    }

    //limpar todas as data do empregado quando ele altera o tipo de agenda
    public void excluirEmpregadoDoCalendario(Empregado emp) {
        String nomeEmpregado = emp.getId(); 
        // Percorrer todas as chaves do mapa CalendarioDePagamentos e remove as que contem o empregado
        for (LocalDate data : CalendarioDePagamentos.keySet()) {
            CalendarioDePagamentos.get(data).removeIf(e -> e.getId().equals(nomeEmpregado));
        }
    }

    //Adiciona as listas de datas do empregado ao calendario de datas. 
    public  void adicionarEmpregadoAoCalendariodePagamentos(List<LocalDate> datas, Empregado empregado) {
        for (LocalDate data : datas) {
            if (CalendarioDePagamentos.containsKey(data)) {
                CalendarioDePagamentos.get(data).add(empregado);
            } else {
                List<Empregado> novaLista = new ArrayList<>();
                novaLista.add(empregado);
                CalendarioDePagamentos.put(data, novaLista);
            }
        }
    }

    //verifica se e o ultimo dia ultil do mes 
    public  List<LocalDate> ultimoDiaUtilPorMes(int ano, int diaDoMes) {
        List<LocalDate> ultimoDias = new ArrayList<>();
        LocalDate ultimoDia;
        for (int mes = 1; mes <= 12; mes++) {
            if (diaDoMes == 31) {
                ultimoDia = LocalDate.of(ano, mes % 12 + 1, 1).minusDays(1);
                while (ultimoDia.getDayOfWeek() == DayOfWeek.SATURDAY || ultimoDia.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    ultimoDia = ultimoDia.minusDays(1);
                }
            } else {
                ultimoDia = LocalDate.of(ano, mes, diaDoMes);
            }
            ultimoDias.add(ultimoDia);
        }
        return ultimoDias;
    }

    // cria uma lista com as datas de pagamento semanal
    public  List<LocalDate> criarTipoAgendaSemanal(int diaSemana) {
        List<LocalDate> dias = new ArrayList<>();
        int anoAtual = 2005;

        for (int mes = 1; mes <= 12; mes++) {
            LocalDate primeiroDiaDoMes = LocalDate.of(anoAtual, mes, 1);
            DayOfWeek diaDaSemana = primeiroDiaDoMes.getDayOfWeek();
            int diff = (diaSemana - diaDaSemana.getValue() + 7) % 7;
            LocalDate diaDesejado = primeiroDiaDoMes.plusDays(diff);
            while (diaDesejado.getMonthValue() == mes) {
                dias.add(diaDesejado);
                diaDesejado = diaDesejado.plusDays(7);
            }
        }

        return dias;
    }

    // cria lista com datas de pagamento a cada n semamas no dia m exemplo "semanal 2 5"
    public  List<LocalDate> criarAgendaSemanalComPeriodicidade(int diaSemana, int periodicidade, int anoAtual) {
        List<LocalDate> dias = new ArrayList<>();

        // Encontrar a primeira ocorrência após o primeiro dia do ano
        LocalDate primeiroDiaDoAno = LocalDate.of(anoAtual, 1, 1);
        LocalDate diaDesejado = primeiroDiaDoAno.with(TemporalAdjusters.nextOrSame(DayOfWeek.of(diaSemana)));
        diaDesejado = diaDesejado.plusWeeks(periodicidade - 1);

        // Adicionar o dia com a periodicidade desejada a partir da segunda semana do
        // ano
        while (diaDesejado.getYear() == anoAtual) {
            dias.add(diaDesejado);
            diaDesejado = diaDesejado.plusWeeks(periodicidade);
        }

        return dias;
    }

}
