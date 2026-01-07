import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class LegendFoot extends JFrame {
    // ===== DADOS =====
    static ArrayList<Clube> clubes = new ArrayList<>();
    static ArrayList<Clube> serieA = new ArrayList<>();
    static ArrayList<Clube> serieB = new ArrayList<>();
    static ArrayList<Clube> serieC = new ArrayList<>();
    static ArrayList<Clube> serieD = new ArrayList<>();
    static Clube clubeJogador;
    static int rodada = 1;
    static int rodadaBrasileiro = 1;
    static int rodadaCopa = 1;
    static boolean proximaPartidaBrasileirao = true;
    static int contadorAlternancia = 0;
    static CopaDoB brasil;
    static CopaLibertadores libertadores;
    static SupercopaBrasil supercopa;
    static ArrayList<Partida> partidasDisponiveis = new ArrayList<>();
    static Partida proximaPartida = null;

    // ===== CONSTRUTOR =====
    public LegendFoot() {
        setTitle("LegendFoot 2026");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        menuPrincipal();
        setVisible(true);
    }

    // ===== MENU PRINCIPAL =====
    void menuPrincipal() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("LEGENDFOOT 2026", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        add(titulo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton novo = new JButton("Novo Jogo");
        JButton carregar = new JButton("Carregar");
        JButton editor = new JButton("Editor");
        JButton sair = new JButton("Sair");

        botoes.add(novo);
        botoes.add(carregar);
        botoes.add(editor);
        botoes.add(sair);

        add(botoes, BorderLayout.CENTER);

        novo.addActionListener(e -> escolherTime());
        carregar.addActionListener(e -> escolherSaveParaCarregar());
        editor.addActionListener(e -> editor());
        sair.addActionListener(e -> System.exit(0));

        revalidate();
        repaint();
    }

    // ===== ESCOLHA DE TIME =====
    void escolherTime() {
        criarClubes();
        brasil = new CopaDoB(new ArrayList<>(clubes));
        libertadores = new CopaLibertadores();

        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JLabel lbl = new JLabel("Escolha seu time", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 24));
        add(lbl, BorderLayout.NORTH);

        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JComboBox<String> filtroSerie = new JComboBox<>(new String[]{"Todos", "Série A", "Série B", "Série C", "Série D"});
        JTextField buscaField = new JTextField(15);
        buscaField.setToolTipText("Buscar time...");
        filtrosPanel.add(new JLabel("Série:"));
        filtrosPanel.add(filtroSerie);
        filtrosPanel.add(new JLabel("Busca:"));
        filtrosPanel.add(buscaField);
        add(filtrosPanel, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> lista = new JList<>(model);
        
        for (Clube c : clubes) model.addElement(c.nome + " - " + c.serie + " (" + c.estadio + ")");

        JScrollPane scrollPane = new JScrollPane(lista);
        add(scrollPane, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout());
        JButton escolher = new JButton("Confirmar");
        JButton info = new JButton("Info Time");
        botoes.add(escolher);
        botoes.add(info);
        add(botoes, BorderLayout.SOUTH);

        // Filtro em tempo real
        filtroSerie.addActionListener(e -> {
            model.clear();
            String serie = (String) filtroSerie.getSelectedItem();
            String busca = buscaField.getText().toLowerCase();
            for (Clube c : clubes) {
                if ((serie.equals("Todos") || c.serie.equals(serie)) && c.nome.toLowerCase().contains(busca)) {
                    model.addElement(c.nome + " - " + c.serie + " (" + c.estadio + ")");
                }
            }
        });

        buscaField.addActionListener(e -> {
            model.clear();
            String serie = (String) filtroSerie.getSelectedItem();
            String busca = buscaField.getText().toLowerCase();
            for (Clube c : clubes) {
                if ((serie.equals("Todos") || c.serie.equals(serie)) && c.nome.toLowerCase().contains(busca)) {
                    model.addElement(c.nome + " - " + c.serie + " (" + c.estadio + ")");
                }
            }
        });

        escolher.addActionListener(e -> {
            if (lista.getSelectedIndex() != -1) {
                String selecionado = lista.getSelectedValue();
                String nomeClubeAtual = selecionado.split(" - ")[0];
                for (Clube c : clubes) {
                    if (c.nome.equals(nomeClubeAtual)) {
                        clubeJogador = c;
                        telaPrincipal();
                        return;
                    }
                }
            }
        });

        info.addActionListener(e -> {
            if (lista.getSelectedIndex() != -1) {
                String selecionado = lista.getSelectedValue();
                String nomeClubeAtual = selecionado.split(" - ")[0];
                for (Clube c : clubes) {
                    if (c.nome.equals(nomeClubeAtual)) {
                        mostrarInfoTempo(c);
                        return;
                    }
                }
            }
        });

        revalidate();
        repaint();
    }

    void mostrarInfoTempo(Clube c) {
        StringBuilder sb = new StringBuilder();
        sb.append("INFORMAÇÕES DO CLUBE\n\n");
        sb.append("Nome: ").append(c.nome).append("\n");
        sb.append("Série: ").append(c.serie).append("\n");
        sb.append("Estádio: ").append(c.estadio).append("\n");
        sb.append("Treinador: ").append(c.treinador).append("\n");
        sb.append("Dinheiro: $").append(c.dinheiro).append("\n\n");
        sb.append("JOGADORES:\n");
        for (int i = 0; i < Math.min(5, c.jogadores.size()); i++) {
            Jogador j = c.jogadores.get(i);
            sb.append("- ").append(j.nome).append(" (Habilidade: ").append(j.habilidade).append(")\n");
        }
        if (c.jogadores.size() > 5) {
            sb.append("... e ").append(c.jogadores.size() - 5).append(" mais jogadores");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Info do Time", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== TELA PRINCIPAL =====
    void telaPrincipal() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Painel superior com informações
        JPanel painelTopo = new JPanel(new BorderLayout());
        
        JLabel topo = new JLabel(
                "Clube: " + clubeJogador.nome +
                " | Dinheiro: R$" + clubeJogador.dinheiro +
                " | Rodada: " + rodada,
                JLabel.LEFT
        );
        topo.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel confianca = new JLabel(
                "🎯 Torcida: " + criarBarraConfianca(clubeJogador.confiancaTorcida) + " " + clubeJogador.confiancaTorcida + "% | " +
                "👔 Diretoria: " + criarBarraConfianca(clubeJogador.confiancaDiretoria) + " " + clubeJogador.confiancaDiretoria + "%" +
                (clubeJogador.debito > 0 ? " | 💰 Débito: R$" + clubeJogador.debito : ""),
                JLabel.RIGHT
        );
        confianca.setFont(new Font("Arial", Font.BOLD, 11));
        
        painelTopo.add(topo, BorderLayout.WEST);
        painelTopo.add(confianca, BorderLayout.EAST);
        add(painelTopo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(5, 2, 10, 10));

        JButton elenco = new JButton("Elenco");
        JButton tabela = new JButton("Tabela");
        JButton jogar = new JButton("Próxima Rodada");
        JButton copa = new JButton("Copa do Brasil");
        JButton supercopa = new JButton("Supercopa Brasil");
        JButton salvar = new JButton("Salvar");
        JButton config = new JButton("Configurações");
        JButton menu = new JButton("Menu");
        JButton gestao = new JButton("Gestão");
        JButton sair = new JButton("Sair");

        botoes.add(elenco);
        botoes.add(tabela);
        botoes.add(jogar);
        botoes.add(copa);
        botoes.add(supercopa);
        botoes.add(salvar);
        botoes.add(config);
        botoes.add(menu);
        botoes.add(gestao);
        botoes.add(sair);

        add(botoes, BorderLayout.CENTER);

        elenco.addActionListener(e -> mostrarElenco());
        tabela.addActionListener(e -> mostrarTabela());
        jogar.addActionListener(e -> {
            String[] opcoes = {"Simular Todas", "Simular Uma", "Cancelar"};
            int escolha = JOptionPane.showOptionDialog(this,
                    "Como deseja simular a rodada?",
                    "Próxima Rodada",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);
            
            if (escolha == 0) {
                simularProximaRodadaTodas();
            } else if (escolha == 1) {
                jogarRodada();
            }
        });
        copa.addActionListener(e -> menuCopaDoB());
        supercopa.addActionListener(e -> mostrarSupercopa());
        salvar.addActionListener(e -> escolherSaveParaSalvar());
        config.addActionListener(e -> menuConfiguracoes());
        menu.addActionListener(e -> menuPrincipal());
        gestao.addActionListener(e -> mostrarMenuJogo());
        sair.addActionListener(e -> System.exit(0));

        revalidate();
        repaint();
    }

    String criarBarraConfianca(int valor) {
        String[] barras = {"▁", "▂", "▃", "▄", "▅", "▆", "▇", "█"};
        int nivel = (valor / 13); // 100/8 = 12.5, então 13
        if (nivel > 7) nivel = 7;
        if (nivel < 0) nivel = 0;
        
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < 8; i++) {
            if (i < nivel) barra.append("█");
            else barra.append("░");
        }
        barra.append("]");
        return barra.toString();
    }

    // ===== ELENCO =====
    void mostrarElenco() {
        JFrame frameElenco = new JFrame("Elenco - " + clubeJogador.nome);
        frameElenco.setSize(1000, 600);
        frameElenco.setLocationRelativeTo(null);
        frameElenco.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea areaElenco = new JTextArea();
        areaElenco.setEditable(false);
        areaElenco.setFont(new Font("Courier New", Font.PLAIN, 10));
        areaElenco.setLineWrap(false);

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║                        ELENCO DO CLUBE                                         ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════════════════╝\n\n");

        sb.append(String.format("%-4s%-20s%-12s%-4s%-15s%-15s%-15s\n",
                "#", "NOME", "POS", "HAB", "NACIONALIDADE", "CAR1", "CAR2"));
        sb.append("─".repeat(84)).append("\n");

        for (int i = 0; i < clubeJogador.jogadores.size(); i++) {
            Jogador j = clubeJogador.jogadores.get(i);
            String status = i < 11 ? "⭐" : i < 23 ? "📋" : "❌";
            
            sb.append(String.format("%s%-3d%-20s%-12s%-4d%-15s%-15s%-15s\n",
                    status,
                    i + 1,
                    j.nome.substring(0, Math.min(20, j.nome.length())),
                    j.posicao,
                    j.habilidade,
                    j.nacionalidade.substring(0, Math.min(15, j.nacionalidade.length())),
                    j.caracteristica1.substring(0, Math.min(15, j.caracteristica1.length())),
                    j.caracteristica2.substring(0, Math.min(15, j.caracteristica2.length()))
            ));
        }

        sb.append("\n" + "─".repeat(84)).append("\n");
        sb.append("⭐ = Titular | 📋 = Reserva | ❌ = Não Relacionado\n");

        areaElenco.setText(sb.toString());
        areaElenco.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(areaElenco);
        frameElenco.add(scrollPane);
        frameElenco.setVisible(true);
    }

    // ===== TABELA =====
    void mostrarTabela() {
        String[] options = {"Série A", "Série B", "Série C", "Série D", "Copa do Brasil", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this, "Escolha a série:", "Tabela",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        ArrayList<Clube> tabela = new ArrayList<>();
        String titulo = "";

        switch (sel) {
            case 0: tabela = serieA; titulo = "Série A"; break;
            case 1: tabela = serieB; titulo = "Série B"; break;
            case 2: tabela = serieC; titulo = "Série C"; break;
            case 3: tabela = serieD; titulo = "Série D"; break;
            case 4: mostrarTabelaCopa(); return;
            default: return;
        }

        tabela.sort((a, b) -> {
            if (b.pontos != a.pontos) return b.pontos - a.pontos;
            int saldoA = a.golsFeitos - a.golsSofridos;
            int saldoB = b.golsFeitos - b.golsSofridos;
            return saldoB - saldoA;
        });

        StringBuilder sb = new StringBuilder(titulo + " - Rodada " + rodadaBrasileiro + "\n");
        sb.append("═════════════════════════════════════════\n");
        sb.append("Pos | Time              | P | J | V | E | D | GP | GC | SG | Pts\n");
        sb.append("─────────────────────────────────────────\n");

        int pos = 1;
        for (Clube c : tabela) {
            int jogos = c.vitorias + c.empates + c.derrotas;
            int saldo = c.golsFeitos - c.golsSofridos;
            sb.append(String.format("%2d | %-16s | %d | %2d | %2d | %2d | %2d | %2d | %2d | %+2d | %2d\n",
                    pos++, c.nome, c.pontos, jogos, c.vitorias, c.empates, c.derrotas, c.golsFeitos, c.golsSofridos, saldo, c.pontos));
        }

        sb.append("\n═════════════════════════════════════════\n");
        if (sel == 0) {
            sb.append("🏆 Libertadores (1-4)\n");
            sb.append("🔴 Rebaixamento (17-20)\n");
        } else if (sel == 1) {
            sb.append("📈 Acesso Série A (1-4)\n");
            sb.append("🔴 Rebaixamento (17-20)\n");
        } else if (sel == 2) {
            sb.append("📈 Acesso Série B (1-4)\n");
            sb.append("🔴 Rebaixamento (17-20)\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    void mostrarTabelaCopa() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔═════════════════════════════════════════════════════╗\n");
        sb.append("║        COPA DO BRASIL 2026 - TABELA              ║\n");
        sb.append("╚═════════════════════════════════════════════════════╝\n\n");
        
        if (brasil.finalizado) {
            sb.append("🏆 CAMPEÃO: ").append(brasil.campeao.nome).append("\n");
            sb.append("🥈 VICE: ").append(brasil.viceCampeao.nome).append("\n");
            sb.append("💫 Ambos classificados para a Copa Libertadores 2027!\n\n");
            sb.append("═════════════════════════════════════════════════════\n");
        } else {
            sb.append("📍 Rodada: ").append(brasil.getNomeRodada()).append("\n");
            sb.append("📊 Status: Competição em andamento\n\n");
            sb.append("Times Participantes:\n");
            sb.append("─────────────────────────────────────────────────────\n");
            int contador = 1;
            for (ArrayList<Integer> c : brasil.chaves) {
                if (c != null && !c.isEmpty()) {
                    Clube time = brasil.times.get(c.get(0));
                    if (time != null) {
                        sb.append(String.format("%2d. %s (%s)\n", contador++, time.nome, time.serie));
                    }
                }
            }
            sb.append("─────────────────────────────────────────────────────\n");
            sb.append("⚽ Próxima rodada em breve...\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Tabela Copa do Brasil", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== JOGAR RODADA =====
    void jogarRodada() {
        Random r = new Random();
        
        if (proximaPartida == null) {
            // Criar partidas da próxima rodada
            contadorAlternancia++;
            boolean jogarBrasileiro = (contadorAlternancia % 4) != 0;

            if (jogarBrasileiro) {
                // Gerar partidas do Brasileirão
                criarPartidasBrasileiro(r);
                rodadaBrasileiro++;
            } else {
                // Partida da Copa do Brasil
                if (!brasil.finalizado) {
                    brasil.jogarPartida();
                    String resultado = brasil.getResultadoUltimaPartida();
                    JOptionPane.showMessageDialog(this, resultado, "Copa do Brasil - Rodada " + brasil.getNomeRodada(), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Copa do Brasil finalizada!");
                }
                telaPrincipal();
                return;
            }
        }

        if (!partidasDisponiveis.isEmpty()) {
            proximaPartida = partidasDisponiveis.remove(0);
            
            String msg = proximaPartida.mandante.nome + " vs " + proximaPartida.visitante.nome + 
                        "\n\nClube do Jogador: " + 
                        (clubeJogador.nome.equals(proximaPartida.mandante.nome) || clubeJogador.nome.equals(proximaPartida.visitante.nome) ? "SIM - Escalar time?" : "Não (simulada automaticamente)");
            
            int resposta = JOptionPane.showConfirmDialog(this, msg, "Próxima Partida", JOptionPane.YES_NO_OPTION);
            
            if (clubeJogador.nome.equals(proximaPartida.mandante.nome) || clubeJogador.nome.equals(proximaPartida.visitante.nome)) {
                if (resposta == JOptionPane.YES_OPTION) {
                    escalarTime();
                    return;
                }
            }
            
            simularPartidaComCronometro(proximaPartida, new Random());
            proximaPartida = null;
            
            if (!partidasDisponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Partida simulada! Próximas partidas disponíveis.");
            } else {
                JOptionPane.showMessageDialog(this, "Rodada concluída!");
                proximaPartida = null;
            }
        }

        telaPrincipal();
    }

    void simularProximaRodadaTodas() {
        Random r = new Random();
        ArrayList<Partida> todasAsPartidas = new ArrayList<>();
        
        // Gerar todas as partidas das 4 séries
        ArrayList<ArrayList<Clube>> series = new ArrayList<>();
        series.add(serieA);
        series.add(serieB);
        series.add(serieC);
        series.add(serieD);
        
        for (ArrayList<Clube> serie : series) {
            for (int i = 0; i < serie.size(); i += 2) {
                if (i + 1 < serie.size()) {
                    Partida p = new Partida(serie.get(i), serie.get(i + 1));
                    todasAsPartidas.add(p);
                }
            }
        }
        
        // Simular todas as partidas
        StringBuilder resultado = new StringBuilder();
        resultado.append("╔════════════════════════════════════════════╗\n");
        resultado.append("║   RESULTADOS DA RODADA ").append(String.format("%2d", rodadaBrasileiro)).append("   ║\n");
        resultado.append("╚════════════════════════════════════════════╝\n\n");
        
        for (Partida p : todasAsPartidas) {
            simularPartida(p, r);
            resultado.append(String.format("%s %d x %d %s\n",
                    p.mandante.nome.substring(0, Math.min(15, p.mandante.nome.length())),
                    p.golsMandante,
                    p.golsVisitante,
                    p.visitante.nome.substring(0, Math.min(15, p.visitante.nome.length()))
            ));
        }
        
        rodadaBrasileiro++;
        resultado.append("\n✅ RODADA COMPLETA!\n");
        
        JOptionPane.showMessageDialog(this, resultado.toString(), "Próxima Rodada", JOptionPane.INFORMATION_MESSAGE);
        telaPrincipal();
    }

    void criarPartidasBrasileiro(Random r) {
        partidasDisponiveis.clear();
        ArrayList<ArrayList<Clube>> series = new ArrayList<>();
        series.add(serieA);
        series.add(serieB);
        series.add(serieC);
        series.add(serieD);

        for (ArrayList<Clube> serie : series) {
            for (int i = 0; i < serie.size(); i += 2) {
                if (i + 1 < serie.size()) {
                    Partida p = new Partida(serie.get(i), serie.get(i + 1));
                    partidasDisponiveis.add(p);
                }
            }
        }
    }

    void simularPartida(Partida p, Random r) {
        int gols1 = r.nextInt(5);
        int gols2 = r.nextInt(5);

        p.golsMandante = gols1;
        p.golsVisitante = gols2;

        p.mandante.golsFeitos += gols1;
        p.mandante.golsSofridos += gols2;
        p.visitante.golsFeitos += gols2;
        p.visitante.golsSofridos += gols1;

        if (gols1 > gols2) {
            p.mandante.pontos += 3;
            p.mandante.vitorias++;
            p.visitante.derrotas++;
        } else if (gols2 > gols1) {
            p.visitante.pontos += 3;
            p.visitante.vitorias++;
            p.mandante.derrotas++;
        } else {
            p.mandante.pontos += 1;
            p.visitante.pontos += 1;
            p.mandante.empates++;
            p.visitante.empates++;
        }
    }

    void simularPartidaComCronometro(Partida partida, Random r) {
        JFrame framePartida = new JFrame("Simulação - " + partida.mandante.nome + " vs " + partida.visitante.nome);
        framePartida.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        framePartida.setSize(1000, 650);
        framePartida.setLocationRelativeTo(null);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        painelPrincipal.setBackground(java.awt.Color.WHITE);
        
        // === PAINEL SUPERIOR (PLACAR E CRONÔMETRO) ===
        JPanel painelSuperior = new JPanel(new BorderLayout());
        painelSuperior.setBackground(new java.awt.Color(34, 139, 34));
        painelSuperior.setPreferredSize(new java.awt.Dimension(1000, 120));
        
        JLabel labelMandante = new JLabel(partida.mandante.nome, JLabel.CENTER);
        labelMandante.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        labelMandante.setForeground(java.awt.Color.WHITE);
        
        JLabel labelPlacar = new JLabel("0 x 0", JLabel.CENTER);
        labelPlacar.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
        labelPlacar.setForeground(java.awt.Color.WHITE);
        
        JLabel labelMinuto = new JLabel("0'", JLabel.CENTER);
        labelMinuto.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        labelMinuto.setForeground(java.awt.Color.YELLOW);
        
        JLabel labelVisitante = new JLabel(partida.visitante.nome, JLabel.CENTER);
        labelVisitante.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        labelVisitante.setForeground(java.awt.Color.WHITE);
        
        JPanel painelTimes = new JPanel(new java.awt.GridLayout(2, 1));
        painelTimes.setBackground(new java.awt.Color(34, 139, 34));
        painelTimes.add(labelMandante);
        painelTimes.add(labelVisitante);
        
        painelSuperior.add(painelTimes, BorderLayout.WEST);
        painelSuperior.add(labelPlacar, BorderLayout.CENTER);
        painelSuperior.add(labelMinuto, BorderLayout.EAST);
        
        // === PAINEL DE EVENTOS ===
        JTextArea areaEventos = new JTextArea();
        areaEventos.setEditable(false);
        areaEventos.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 11));
        areaEventos.setLineWrap(true);
        areaEventos.setWrapStyleWord(true);
        JScrollPane scrollEventos = new JScrollPane(areaEventos);
        scrollEventos.setBorder(new javax.swing.border.TitledBorder("Eventos da Partida"));
        
        // === PAINEL DE CONTROLES ===
        JPanel painelControles = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));
        painelControles.setBackground(java.awt.Color.WHITE);
        
        JButton btnPausarRetomar = new JButton("| | Pausar");
        JButton btnPular = new JButton(">> Pular");
        JButton btnFechar = new JButton("X Fechar");
        
        painelControles.add(btnPausarRetomar);
        painelControles.add(btnPular);
        painelControles.add(btnFechar);
        
        // === PAINEL DE VELOCIDADE ===
        JPanel painelVelocidade = new JPanel();
        painelVelocidade.setBackground(java.awt.Color.WHITE);
        painelVelocidade.setBorder(new javax.swing.border.TitledBorder("Velocidade"));
        
        JLabel labelVel = new JLabel("1x");
        JSlider sliderVelocidade = new JSlider(1, 10, 5);
        sliderVelocidade.setMajorTickSpacing(1);
        sliderVelocidade.setPaintTicks(true);
        sliderVelocidade.setPaintLabels(true);
        sliderVelocidade.addChangeListener(e -> labelVel.setText(sliderVelocidade.getValue() + "x"));
        
        painelVelocidade.add(new JLabel("Lenta"));
        painelVelocidade.add(sliderVelocidade);
        painelVelocidade.add(labelVel);
        
        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(painelVelocidade, BorderLayout.WEST);
        painelInferior.add(painelControles, BorderLayout.EAST);
        
        painelPrincipal.add(painelSuperior, BorderLayout.NORTH);
        painelPrincipal.add(scrollEventos, BorderLayout.CENTER);
        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);
        
        framePartida.add(painelPrincipal);
        framePartida.setVisible(true);
        
        // === CONTROLES ===
        final boolean[] pausado = {false};
        final boolean[] pular = {false};
        
        btnPausarRetomar.addActionListener(e -> {
            pausado[0] = !pausado[0];
            btnPausarRetomar.setText(pausado[0] ? "> Retomar" : "| | Pausar");
        });
        
        btnPular.addActionListener(e -> pular[0] = true);
        
        btnFechar.addActionListener(e -> framePartida.dispose());
        
        // === SIMULAÇÃO EM THREAD ===
        Thread threadSim = new Thread(() -> {
            SimuladorPartida simulador = new SimuladorPartida(partida);
            Random rand = new Random();
            
            for (int minuto = 0; minuto <= 90; minuto++) {
                if (pular[0]) break;
                
                while (pausado[0] && !pular[0]) {
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
                
                int velocidade = sliderVelocidade.getValue();
                long delay = 150 - (velocidade * 10);
                try { Thread.sleep(Math.max(20, delay)); } catch (InterruptedException e) {}
                
                final int minutoFinal = minuto;
                
                // Atualizar cronômetro
                SwingUtilities.invokeLater(() -> {
                    if (minutoFinal == 45) {
                        labelMinuto.setText("45' + INT");
                    } else if (minutoFinal > 45 && minutoFinal <= 90) {
                        labelMinuto.setText((minutoFinal) + "'");
                    } else {
                        labelMinuto.setText(minutoFinal + "'");
                    }
                });
                
                // Eventos aleatórios
                if (minuto == 45) {
                    areaEventos.append("\n========== INTERVALO ==========\n");
                } else if (minuto == 91) {
                    areaEventos.append("\n========== FIM DE JOGO ==========\n");
                }
                
                // Gols (8% de chance)
                if (rand.nextInt(100) < 8) {
                    boolean ehMandante = rand.nextBoolean();
                    if (ehMandante) {
                        simulador.golsMandante++;
                        if (partida.mandante.jogadoresEscalados.size() > 0) {
                            int idx = rand.nextInt(Math.min(11, partida.mandante.jogadoresEscalados.size()));
                            String jogador = partida.mandante.jogadoresEscalados.get(idx).nome;
                            areaEventos.append(minuto + "' ⚽ GOL! " + partida.mandante.nome + " - " + jogador + "\n");
                        }
                    } else {
                        simulador.golsVisitante++;
                        if (partida.visitante.jogadoresEscalados.size() > 0) {
                            int idx = rand.nextInt(Math.min(11, partida.visitante.jogadoresEscalados.size()));
                            String jogador = partida.visitante.jogadoresEscalados.get(idx).nome;
                            areaEventos.append(minuto + "' ⚽ GOL! " + partida.visitante.nome + " - " + jogador + "\n");
                        }
                    }
                    SwingUtilities.invokeLater(() -> labelPlacar.setText(simulador.golsMandante + " x " + simulador.golsVisitante));
                    areaEventos.setCaretPosition(areaEventos.getDocument().getLength());
                }
                
                // Cartões e Lesões (3% de chance)
                if (rand.nextInt(100) < 3) {
                    String time = rand.nextBoolean() ? partida.mandante.nome : partida.visitante.nome;
                    int tipo = rand.nextInt(3);
                    if (tipo == 0) {
                        areaEventos.append(minuto + "' 🟨 CARTÃO AMARELO - " + time + "\n");
                    } else if (tipo == 1) {
                        areaEventos.append(minuto + "' 🔴 CARTÃO VERMELHO - " + time + "\n");
                    } else {
                        areaEventos.append(minuto + "' 🚑 LESÃO - " + time + "\n");
                    }
                    areaEventos.setCaretPosition(areaEventos.getDocument().getLength());
                }
                
                // Escanteios (5% de chance)
                if (rand.nextInt(100) < 5) {
                    String time = rand.nextBoolean() ? partida.mandante.nome : partida.visitante.nome;
                    areaEventos.append(minuto + "' 🚩 ESCANTEIO - " + time + "\n");
                    areaEventos.setCaretPosition(areaEventos.getDocument().getLength());
                }
                
                // Chutes (20% de chance)
                if (rand.nextInt(100) < 20) {
                    if (rand.nextBoolean()) {
                        if (rand.nextInt(100) < 40) simulador.chutesForaMandante++;
                        else simulador.chutesMandante++;
                    } else {
                        if (rand.nextInt(100) < 40) simulador.chutesForaVisitante++;
                        else simulador.chutesVisitante++;
                    }
                }
                
                // Posse
                if (rand.nextBoolean()) simulador.posse_mandante++;
                else simulador.posse_visitante++;
            }
            
            partida.golsMandante = simulador.golsMandante;
            partida.golsVisitante = simulador.golsVisitante;
            partida.posse_mandante = simulador.posse_mandante;
            partida.posse_visitante = simulador.posse_visitante;
            partida.chutesMandante = simulador.chutesMandante;
            partida.chutesVisitante = simulador.chutesVisitante;
            partida.chutesForaMandante = simulador.chutesForaMandante;
            partida.chutesForaVisitante = simulador.chutesForaVisitante;
            
            partida.mandante.atualizarEstatisticas(partida);
            partida.visitante.atualizarEstatisticas(partida);
            
            // Resultado final
            SwingUtilities.invokeLater(() -> {
                // Calcular posse de bola em %
                int totalPosse = partida.posse_mandante + partida.posse_visitante;
                int posseMandante = totalPosse > 0 ? (partida.posse_mandante * 100) / totalPosse : 50;
                int posseVisitante = 100 - posseMandante;
                
                String resultado = "╔════════════════════════════════════════════╗\n" +
                                   "║         RESULTADO FINAL DA PARTIDA           ║\n" +
                                   "╚════════════════════════════════════════════╝\n\n" +
                                 partida.mandante.nome.toUpperCase() + " " + simulador.golsMandante + " x " + 
                                 simulador.golsVisitante + " " + partida.visitante.nome.toUpperCase() + "\n\n" +
                                 "📊 ESTATÍSTICAS:\n" +
                                 "┌────────────────────────────────────────────┐\n" +
                                 String.format("│ Posse de Bola:    %3d%% x %-3d%%               │\n", posseMandante, posseVisitante) +
                                 String.format("│ Chutes:           %3d x %3d                    │\n", simulador.chutesMandante, simulador.chutesVisitante) +
                                 String.format("│ Chutes no Alvo:   %3d x %3d                    │\n", 
                                     (simulador.chutesMandante - simulador.chutesForaMandante), 
                                     (simulador.chutesVisitante - simulador.chutesForaVisitante)) +
                                 String.format("│ Chutes para Fora: %3d x %3d                    │\n", 
                                     simulador.chutesForaMandante, simulador.chutesForaVisitante) +
                                 String.format("│ Escanteios:       %3d x %3d                    │\n", 
                                     simulador.escarniomMandante, simulador.escarnosVisitante) +
                                 String.format("│ Desarmes:         %3d x %3d                    │\n", 
                                     simulador.desarmesMandante, simulador.desarmesVisitante) +
                                 "└────────────────────────────────────────────┘\n";
                
                JOptionPane.showMessageDialog(framePartida, resultado, 
                    "Fim da Partida - " + (simulador.golsMandante > simulador.golsVisitante ? partida.mandante.nome + " Venceu!" : 
                                           simulador.golsVisitante > simulador.golsMandante ? partida.visitante.nome + " Venceu!" : "Empate!"), 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Mostrar notas dos jogadores
                mostrarNotasJogadores(partida);
                framePartida.dispose();
            });
        });
        
        threadSim.start();
    }

    void mostrarNotasJogadores(Partida partida) {
        Random r = new Random();
        
        // Calcular diferença de gols para determinar desempenho
        int diferenca = partida.golsMandante - partida.golsVisitante;
        
        // Gerar notas para jogadores do mandante (mais realistas)
        for (int i = 0; i < Math.min(11, partida.mandante.jogadoresEscalados.size()); i++) {
            int notaBase;
            
            if (partida.golsMandante > partida.golsVisitante) {
                // Time venceu: notas mais altas
                notaBase = 72 + r.nextInt(18); // 72-90
            } else if (partida.golsMandante == partida.golsVisitante) {
                // Empate: notas medianas
                notaBase = 68 + r.nextInt(15); // 68-83
            } else {
                // Time perdeu: notas mais baixas
                notaBase = 60 + r.nextInt(18); // 60-78
            }
            
            // Bônus/penalidade por posse de bola
            int totalPosse = partida.posse_mandante + partida.posse_visitante;
            if (totalPosse > 0) {
                int posseMandante = (partida.posse_mandante * 100) / totalPosse;
                if (posseMandante > 55) notaBase += 2;
                else if (posseMandante < 45) notaBase -= 2;
            }
            
            partida.notasMandante[i] = Math.max(40, Math.min(95, notaBase));
        }
        
        // Gerar notas para jogadores do visitante (mais realistas)
        for (int i = 0; i < Math.min(11, partida.visitante.jogadoresEscalados.size()); i++) {
            int notaBase;
            
            if (partida.golsVisitante > partida.golsMandante) {
                // Time venceu: notas mais altas
                notaBase = 72 + r.nextInt(18); // 72-90
            } else if (partida.golsVisitante == partida.golsMandante) {
                // Empate: notas medianas
                notaBase = 68 + r.nextInt(15); // 68-83
            } else {
                // Time perdeu: notas mais baixas
                notaBase = 60 + r.nextInt(18); // 60-78
            }
            
            // Bônus/penalidade por posse de bola
            int totalPosse = partida.posse_mandante + partida.posse_visitante;
            if (totalPosse > 0) {
                int posseVisitante = (partida.posse_visitante * 100) / totalPosse;
                if (posseVisitante > 55) notaBase += 2;
                else if (posseVisitante < 45) notaBase -= 2;
            }
            
            partida.notasVisitante[i] = Math.max(40, Math.min(95, notaBase));
        }
        
        // Exibir notas
        StringBuilder notas = new StringBuilder();
        notas.append("╔════════════════════════════════════════════╗\n");
        notas.append("║        NOTAS DOS JOGADORES - DETALHES      ║\n");
        notas.append("╚════════════════════════════════════════════╝\n\n");
        
        notas.append("🏠 ").append(partida.mandante.nome.toUpperCase()).append(":\n");
        notas.append("┌────────────────────────────────────────────┐\n");
        for (int i = 0; i < Math.min(11, partida.mandante.jogadoresEscalados.size()); i++) {
            Jogador j = partida.mandante.jogadoresEscalados.get(i);
            int nota = partida.notasMandante[i];
            String emoji = nota >= 80 ? "⭐⭐" : nota >= 75 ? "⭐" : nota >= 70 ? "👍" : nota >= 65 ? "👌" : "⚠️";
            notas.append(String.format("│ %-25s %s %2d │\n", j.nome.substring(0, Math.min(25, j.nome.length())), emoji, nota));
        }
        notas.append("└────────────────────────────────────────────┘\n\n");
        
        notas.append("✈️  ").append(partida.visitante.nome.toUpperCase()).append(":\n");
        notas.append("┌────────────────────────────────────────────┐\n");
        for (int i = 0; i < Math.min(11, partida.visitante.jogadoresEscalados.size()); i++) {
            Jogador j = partida.visitante.jogadoresEscalados.get(i);
            int nota = partida.notasVisitante[i];
            String emoji = nota >= 80 ? "⭐⭐" : nota >= 75 ? "⭐" : nota >= 70 ? "👍" : nota >= 65 ? "👌" : "⚠️";
            notas.append(String.format("│ %-25s %s %2d │\n", j.nome.substring(0, Math.min(25, j.nome.length())), emoji, nota));
        }
        notas.append("└────────────────────────────────────────────┘\n");
        
        JTextArea areaNotas = new JTextArea(notas.toString());
        areaNotas.setEditable(false);
        areaNotas.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 10));
        JScrollPane scrollNotas = new JScrollPane(areaNotas);
        
        JOptionPane.showMessageDialog(null, scrollNotas, "Notas dos Jogadores", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== MENU COPA DO BRASIL =====
    void menuCopaDoB() {
        if (brasil.finalizado) {
            StringBuilder sb = new StringBuilder("Copa do Brasil Finalizada!\n\n");
            sb.append("Campeão: ").append(brasil.campeao.nome).append("\n");
            sb.append("Vice: ").append(brasil.viceCampeao.nome).append("\n\n");
            sb.append("Ambos ganharam vaga para a Copa Libertadores!");
            JOptionPane.showMessageDialog(this, sb.toString(), "Copa do Brasil", JOptionPane.INFORMATION_MESSAGE);
            telaPrincipal();
            return;
        }

        String[] options = {"Ver Chaveamento", "Jogar Próxima Partida", "Voltar"};
        int sel = JOptionPane.showOptionDialog(this, "Copa do Brasil - " + brasil.getNomeRodada(), "Copa do Brasil",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        if (sel == 0) {
            JOptionPane.showMessageDialog(this, brasil.getChaveamento(), "Chaveamento", JOptionPane.INFORMATION_MESSAGE);
            menuCopaDoB();
        } else if (sel == 1) {
            brasil.jogarPartida();
            JOptionPane.showMessageDialog(this, brasil.getResultadoUltimaPartida(), "Resultado", JOptionPane.INFORMATION_MESSAGE);
            menuCopaDoB();
        } else {
            telaPrincipal();
        }
    }

    // ===== SUPERCOPA DO BRASIL =====
    void mostrarSupercopa() {
        if (supercopa == null || supercopa.campeaoBrasileiro == null || supercopa.campeaoCopa == null) {
            JOptionPane.showMessageDialog(this, 
                "Supercopa indisponível.\nAguarde o término do Brasileirão e Copa do Brasil!", 
                "Supercopa do Brasil", JOptionPane.INFORMATION_MESSAGE);
            telaPrincipal();
            return;
        }

        if (supercopa.finalizado) {
            JOptionPane.showMessageDialog(this, 
                "SUPERCOPA DO BRASIL\n\n" + supercopa.getResultado(), 
                "Resultado da Supercopa", JOptionPane.INFORMATION_MESSAGE);
            telaPrincipal();
            return;
        }

        int opcao = JOptionPane.showConfirmDialog(this,
            "🏆 SUPERCOPA DO BRASIL 2026\n\n" +
            "Campeão Brasileiro: " + supercopa.campeaoBrasileiro.nome + "\n" +
            "Campeão Copa do Brasil: " + supercopa.campeaoCopa.nome + "\n\n" +
            "Deseja simular esta partida?",
            "Supercopa do Brasil", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            supercopa.simularPartida();
            JOptionPane.showMessageDialog(this, supercopa.getResultado(), "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }

        telaPrincipal();
    }

    // ===== CONFIGURAÇÕES =====
    void menuConfiguracoes() {
        JFrame frameConfig = new JFrame("Configurações do Jogo");
        frameConfig.setSize(600, 400);
        frameConfig.setLocationRelativeTo(null);
        frameConfig.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new javax.swing.border.EmptyBorder(15, 15, 15, 15));
        
        // Seção de velocidade de simulação
        JPanel painelVelocidade = new JPanel(new BorderLayout(10, 10));
        painelVelocidade.setBorder(new javax.swing.border.TitledBorder("Velocidade de Simulação"));
        
        JLabel labelVelAtual = new JLabel("Velocidade Atual: " + clubeJogador.velocidadeSimulacao + "x");
        labelVelAtual.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        JSlider sliderVel = new JSlider(1, 10, clubeJogador.velocidadeSimulacao);
        sliderVel.setMajorTickSpacing(1);
        sliderVel.setPaintTicks(true);
        sliderVel.setPaintLabels(true);
        sliderVel.addChangeListener(e -> {
            labelVelAtual.setText("Velocidade Atual: " + sliderVel.getValue() + "x");
            clubeJogador.velocidadeSimulacao = sliderVel.getValue();
        });
        
        painelVelocidade.add(labelVelAtual, BorderLayout.NORTH);
        painelVelocidade.add(sliderVel, BorderLayout.CENTER);
        
        // Seção de informações
        JPanel painelInfo = new JPanel(new BorderLayout());
        painelInfo.setBorder(new javax.swing.border.TitledBorder("Informações"));
        
        JTextArea areaInfo = new JTextArea();
        areaInfo.setEditable(false);
        areaInfo.setLineWrap(true);
        areaInfo.setWrapStyleWord(true);
        areaInfo.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        areaInfo.setText(
            "⚙️ CONFIGURAÇÕES DO JOGO\n\n" +
            "📊 Velocidade da Simulação:\n" +
            "  • Controla a velocidade do cronômetro durante as partidas\n" +
            "  • 1x = Mais lento (tempo para analisar)\n" +
            "  • 10x = Mais rápido (visualização rápida)\n\n" +
            "🎮 Recursos de Simulação:\n" +
            "  • Cronômetro em tempo real\n" +
            "  • Pausar/Retomar partida\n" +
            "  • Pular simulação\n" +
            "  • Eventos ao vivo (gols, cartões, lesões)\n" +
            "  • Estatísticas detalhadas\n" +
            "  • Notas dos jogadores após partida"
        );
        painelInfo.add(new JScrollPane(areaInfo), BorderLayout.CENTER);
        
        // Botões
        JPanel painelBotoes = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));
        
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> frameConfig.dispose());
        
        painelBotoes.add(btnVoltar);
        
        painelPrincipal.add(painelVelocidade, BorderLayout.NORTH);
        painelPrincipal.add(painelInfo, BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);
        
        frameConfig.add(painelPrincipal);
        frameConfig.setVisible(true);
    }

    // ===== MENU JOGO =====
    void mostrarMenuJogo() {
        String[] options = {"Escalar Time", "Táticas e Formação", "Gestão de Elenco", "Voltar"};
        int sel = JOptionPane.showOptionDialog(this, "Menu de Gestão", "Gestão",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        switch(sel) {
            case 0: escalarTime(); break;
            case 1: menuTaticas(); break;
            case 2: menuElenco(); break;
            default: telaPrincipal();
        }
    }

    void escalarTime() {
        JFrame frameEscalacao = new JFrame("Escalação - " + clubeJogador.nome);
        frameEscalacao.setSize(1000, 700);
        frameEscalacao.setLocationRelativeTo(null);
        frameEscalacao.setLayout(new BorderLayout(10, 10));

        // Painel do campo
        JPanel campoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(34, 139, 34));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
                g2.drawLine(getWidth() / 2, 10, getWidth() / 2, getHeight() - 10);

                // Desenhar jogadores titulares
                desenharFormacao(g2);
            }

            private void desenharFormacao(Graphics2D g) {
                String[] formacao = clubeJogador.formacao.split("-");
                int defesa = Integer.parseInt(formacao[0]);
                int meio = Integer.parseInt(formacao[1]);
                int ataque = Integer.parseInt(formacao[2]);

                int y = 80;
                int x = getWidth() / 2;

                // GR
                drawJogador(g, x, 30, clubeJogador.jogadoresEscalados.get(0).nome, 1);

                // Defesa
                int defesaPorLado = defesa / 2;
                for (int i = 0; i < defesa; i++) {
                    int xPos = i < defesaPorLado ? x - 150 - (i * 60) : x + 150 + ((i - defesaPorLado) * 60);
                    drawJogador(g, xPos, y, clubeJogador.jogadoresEscalados.get(i + 1).nome, i + 1);
                }

                // Meio
                y += 150;
                int meioPorLado = meio / 2;
                for (int i = 0; i < meio; i++) {
                    int xPos = i < meioPorLado ? x - 150 - (i * 60) : x + 150 + ((i - meioPorLado) * 60);
                    drawJogador(g, xPos, y, clubeJogador.jogadoresEscalados.get(defesa + i + 1).nome, defesa + i + 1);
                }

                // Ataque
                y += 150;
                for (int i = 0; i < ataque; i++) {
                    int xPos = x - (ataque * 60) / 2 + (i * 120);
                    drawJogador(g, xPos, y, clubeJogador.jogadoresEscalados.get(defesa + meio + i + 1).nome, defesa + meio + i + 1);
                }
            }

            private void drawJogador(Graphics2D g, int x, int y, String nome, int numero) {
                g.setColor(Color.BLUE);
                g.fillOval(x - 20, y - 20, 40, 40);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(String.valueOf(numero), x - 5, y + 5);
            }
        };

        campoPanel.setBackground(new Color(34, 139, 34));
        frameEscalacao.add(campoPanel, BorderLayout.CENTER);

        // Painel lateral com jogadores
        JPanel lateralPanel = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        // Aba Titulares
        DefaultListModel<String> modelTitulares = new DefaultListModel<>();
        for (int i = 0; i < 11; i++) {
            Jogador j = clubeJogador.jogadores.get(i);
            modelTitulares.addElement((i+1) + ". " + j.nome + " (" + j.posicao + ") - " + j.habilidade);
        }
        JList<String> listaTitulares = new JList<>(modelTitulares);
        tabs.addTab("Titulares (11)", new JScrollPane(listaTitulares));

        // Aba Reservas
        DefaultListModel<String> modelReservas = new DefaultListModel<>();
        for (int i = 11; i < 23; i++) {
            Jogador j = clubeJogador.jogadores.get(i);
            modelReservas.addElement((i+1) + ". " + j.nome + " (" + j.posicao + ") - " + j.habilidade);
        }
        JList<String> listaReservas = new JList<>(modelReservas);
        tabs.addTab("Reservas (12)", new JScrollPane(listaReservas));

        // Aba Não Relacionados
        DefaultListModel<String> modelNaoRelacionados = new DefaultListModel<>();
        for (int i = 23; i < clubeJogador.jogadores.size(); i++) {
            Jogador j = clubeJogador.jogadores.get(i);
            modelNaoRelacionados.addElement((i+1) + ". " + j.nome + " (" + j.posicao + ") - " + j.habilidade);
        }
        JList<String> listaNaoRelacionados = new JList<>(modelNaoRelacionados);
        tabs.addTab("Não Relacionados", new JScrollPane(listaNaoRelacionados));

        lateralPanel.add(tabs, BorderLayout.CENTER);

        JButton confirmar = new JButton("Confirmar Escalação");
        confirmar.addActionListener(e -> {
            frameEscalacao.dispose();
            telaPrincipal();
        });
        lateralPanel.add(confirmar, BorderLayout.SOUTH);

        frameEscalacao.add(lateralPanel, BorderLayout.EAST);
        frameEscalacao.setVisible(true);
    }

    void menuTaticas() {
        String[] formacoes = {"4-3-3", "4-2-3-1", "3-5-2", "5-3-2", "4-4-2"};
        String[] marcacoes = {"Homem a Homem", "Zona", "Misto"};
        String[] ataques = {"Ala Direita", "Ala Esquerda", "Centro", "Balanced"};
        String[] estilos = {"Ofensivo", "Equilibrado", "Defensivo", "Contra-ataque"};

        String formacao = (String) JOptionPane.showInputDialog(this, "Escolha a Formação:", "Formação", 
                JOptionPane.QUESTION_MESSAGE, null, formacoes, clubeJogador.formacao);
        if (formacao != null) clubeJogador.formacao = formacao;

        String marcacao = (String) JOptionPane.showInputDialog(this, "Escolha a Marcação:", "Marcação", 
                JOptionPane.QUESTION_MESSAGE, null, marcacoes, clubeJogador.marcacao);
        if (marcacao != null) clubeJogador.marcacao = marcacao;

        String ataque = (String) JOptionPane.showInputDialog(this, "Escolha Aonde Atacar:", "Ataque", 
                JOptionPane.QUESTION_MESSAGE, null, ataques, clubeJogador.ataqueLocal);
        if (ataque != null) clubeJogador.ataqueLocal = ataque;

        String estilo = (String) JOptionPane.showInputDialog(this, "Escolha o Estilo de Jogo:", "Estilo", 
                JOptionPane.QUESTION_MESSAGE, null, estilos, clubeJogador.estiloJogo);
        if (estilo != null) clubeJogador.estiloJogo = estilo;

        JOptionPane.showMessageDialog(this, "Táticas atualizadas com sucesso!");
        telaPrincipal();
    }

    void menuElenco() {
        StringBuilder sb = new StringBuilder("ELENCO - " + clubeJogador.nome + "\n\n");
        sb.append("Total de Jogadores: ").append(clubeJogador.jogadores.size()).append("\n\n");
        
        for (int i = 0; i < clubeJogador.jogadores.size(); i++) {
            Jogador j = clubeJogador.jogadores.get(i);
            String status = "";
            if (i < 11) status = " [TITULAR]";
            else if (i < 23) status = " [RESERVA]";
            else status = " [NÃO RELACIONADO]";
            sb.append(String.format("%2d. %-20s Hab: %2d%s\n", i+1, j.nome, j.habilidade, status));
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Elenco Completo", JOptionPane.INFORMATION_MESSAGE);
        telaPrincipal();
    }

    // ===== SALVAR =====
    void salvar() {
        escolherSaveParaSalvar();
    }

    // ===== CARREGAR =====
    void carregar() {
        escolherSaveParaCarregar();
    }

    // ===== EDITOR SIMPLES =====
    void editor() {
        JOptionPane.showMessageDialog(this, "Editor será expandido futuramente.");
    }

    // ===== CRIA CLUBES =====
    void criarClubes() {
        clubes.clear();
        serieA.clear();
        serieB.clear();
        serieC.clear();
        serieD.clear();

        // SÉRIE A - 20 times
        String[][] serieAData = {
            {"Atlético-PR", "Estádio Joaquim Américo (Couto Pereira)", "Antonio Carlos Zago", "Série A"},
            {"Atlético-MG", "Estádio Mineirão", "Gabriel Milito", "Série A"},
            {"Bahia", "Estádio Fonte Nova", "Rogério Ceni", "Série A"},
            {"Botafogo", "Estádio General Severiano", "Artur Jorge", "Série A"},
            {"Bragantino", "Estádio Nabi Abi Chedid", "Pedro Caixinha", "Série A"},
            {"Chapecoense", "Arena Condá", "Jair Ventura", "Série A"},
            {"Corinthians", "Estádio Neo Química Arena", "António Oliveira", "Série A"},
            {"Coritiba", "Estádio Couto Pereira", "Gustavo Morínigo", "Série A"},
            {"Cruzeiro", "Estádio Mineirão", "Fernando Diniz", "Série A"},
            {"Flamengo", "Estádio Maracanã", "Tite", "Série A"},
            {"Fluminense", "Estádio do Maracanã", "Marcão", "Série A"},
            {"Grêmio", "Estádio Arena do Grêmio", "Renato Portaluppi", "Série A"},
            {"Internacional", "Beira-Rio", "Mano Menezes", "Série A"},
            {"Mirassol", "Estádio José Maria de Campos Maia", "Eduardo Baptista", "Série A"},
            {"Palmeiras", "Allianz Parque", "Abel Ferreira", "Série A"},
            {"Remo", "Estádio Baenão", "Hélio dos Anjos", "Série A"},
            {"Santos", "Estádio Urbano Caldeira", "Fábio Carille", "Série A"},
            {"São Paulo", "Estádio Morumbi", "Dorival Júnior", "Série A"},
            {"Vasco da Gama", "Estádio São Januário", "Álvaro Pacheco", "Série A"},
            {"Vitória", "Estádio Barradão", "Carpegiani", "Série A"}
        };

        for (String[] info : serieAData) {
            Clube c = criarClubeComDados(info[0], info[1], info[2], info[3]);
            serieA.add(c);
            clubes.add(c);
        }

        // SÉRIE B - 20 times
        String[][] serieBData = {
            {"América-MG", "Estádio Independência", "Lisca", "Série B"},
            {"Athletic Club", "Estádio Estadual Nilton Santos", "Guto Ferreira", "Série B"},
            {"Atlético-GO", "Estádio Antônio Accioly", "Vagner Mancini", "Série B"},
            {"Avaí", "Estádio da Ressacada", "Fabio Matias", "Série B"},
            {"Botafogo-SP", "Estádio Santa Cruz", "Mazola Júnior", "Série B"},
            {"Ceará", "Estádio Governador Plácido Castelo", "Lucho González", "Série B"},
            {"CRB", "Estádio Gerson de Oliveira Martins", "Elton Amorim", "Série B"},
            {"Criciúma", "Estádio Heriberto Hülse", "Claudio Tencati", "Série B"},
            {"Cuiabá", "Estádio Arena Pantanal", "Jorginho", "Série B"},
            {"Fortaleza", "Estádio Castelão", "Juan Pablo Vojvoda", "Série B"},
            {"Goiás", "Estádio da Serrinha", "Jair Ventura", "Série B"},
            {"Juventude", "Estádio Alfeu Moreira", "Luizinho Vieira", "Série B"},
            {"Londrina", "Estádio do Café", "Felipe Conceição", "Série B"},
            {"Náutico", "Estádio dos Aflitos", "Marquinhos Santos", "Série B"},
            {"Novorizontino", "Estádio Ecag", "Eduardo Baptista", "Série B"},
            {"Operário-PR", "Estádio Germano Krüger", "Rafael Guanaes", "Série B"},
            {"Ponte Preta", "Estádio Moisés Lucarelli", "Jorginho", "Série B"},
            {"Sport", "Estádio da Ilha do Retiro", "Guto Ferreira", "Série B"},
            {"São Bernardo", "Estádio 1º de Maio", "Ricardo Catalá", "Série B"},
            {"Vila Nova", "Estádio OBA", "Wolff", "Série B"}
        };

        for (String[] info : serieBData) {
            Clube c = criarClubeComDados(info[0], info[1], info[2], info[3]);
            serieB.add(c);
            clubes.add(c);
        }

        // SÉRIE C - 20 times
        String[][] serieCData = {
            {"Apeceense", "Estádio Municipal", "Técnico C", "Série C"},
            {"Campinense", "Estádio Pereira de Almeida", "Técnico C", "Série C"},
            {"Ferroviário", "Estádio O Estadium", "Técnico C", "Série C"},
            {"Itabuna", "Estádio Domingão", "Técnico C", "Série C"},
            {"Jacuipense", "Estádio Albertão", "Técnico C", "Série C"},
            {"Moto Club", "Estádio Centro de Treinamento", "Técnico C", "Série C"},
            {"Murici", "Estádio Murici", "Técnico C", "Série C"},
            {"Paysandu", "Estádio Souza Motta", "Técnico C", "Série C"},
            {"Remo", "Estádio Baenão", "Técnico C", "Série C"},
            {"Santa Cruz", "Estádio do Arruda", "Técnico C", "Série C"},
            {"Sampaio Corrêa", "Estádio Castelão", "Técnico C", "Série C"},
            {"Sousa", "Estádio Nilton Santos", "Técnico C", "Série C"},
            {"Treze", "Estádio Soedjelmaço", "Técnico C", "Série C"},
            {"Volta Redonda", "Estádio Raulino de Oliveira", "Técnico C", "Série C"},
            {"ABC", "Estádio Frasquetrão", "Técnico C", "Série C"},
            {"Altos", "Estádio Albertão", "Técnico C", "Série C"},
            {"Amazônia FC", "Estádio Ismael Benigno", "Técnico C", "Série C"},
            {"Anápolis", "Estádio Jonas Duarte", "Técnico C", "Série C"},
            {"Caxias do Sul", "Estádio Centenário", "Técnico C", "Série C"},
            {"Gama", "Estádio Bezerrão", "Técnico C", "Série C"}
        };

        for (String[] info : serieCData) {
            Clube c = criarClubeComDados(info[0], info[1], info[2], info[3]);
            serieC.add(c);
            clubes.add(c);
        }

        // SÉRIE D - 64 clubes
        for (int i = 1; i <= 64; i++) {
            Clube c = criarClubeComDados("Série D - Regional " + i, "Estádio Municipal " + i, "Técnico Regional " + i, "Série D");
            serieD.add(c);
            clubes.add(c);
        }
    }

    Clube criarClubeComDados(String nome, String estadio, String treinador, String serie) {
        Clube c = new Clube(nome, estadio, treinador, serie);
        adicionarJogadoresReais(c, nome);
        return c;
    }

    void adicionarJogadoresReais(Clube clube, String nomeClubes) {
        String[] nomes = new String[0];
        
        if (nomeClubes.equals("Flamengo")) {
            nomes = new String[]{"Pedro Rocha", "Gerson", "Vinícius Jr", "Michael", "Arrascaeta", "Léo Pereira", "Fabricio Bruno", "David Luiz", "Varela", "Matheus Martins", "Thiaguinho", "Chiquinho", "Eustaquio", "Renato Tapia", "Filipe Luís", "Wesley", "Otamendi", "Matheuzinho", "Pulgar"};
        } else if (nomeClubes.equals("Palmeiras")) {
            nomes = new String[]{"Endrick", "Richard Ríos", "Estêvão", "Rony", "Raphael Veiga", "Bruno Henrique", "Zé Rafael", "Gabriel Mendes", "Marcos Rocha", "Piquerez", "Murilo", "Gómez", "Caio Paulista", "Dudu", "Scarpa", "Brunno", "Jhon Vychodil", "Pedrinho", "Moisés"};
        } else if (nomeClubes.equals("São Paulo")) {
            nomes = new String[]{"Luciano", "Ferreira", "Miranda", "Alves", "Lucas Moura", "Patryck", "Welington", "Sabino", "Igor Vinícius", "Wellington", "Calleri", "Rato", "Bobadilla", "Martínez", "Rogério Ceni", "Caboclo", "Jaílson", "Tiago Volpi", "Anderson"};
        } else if (nomeClubes.equals("Atlético-MG")) {
            nomes = new String[]{"Keno", "Nacho Fernández", "Alan Franco", "Hulk", "Paulista", "Igor Rabello", "Guilherme Arana", "Dodô", "Alisson", "Otávio", "Lyanco", "Jemerson", "Sasha", "Alan Kardec", "Rubens", "Éverson", "Fidelís", "Saravia", "Fuchs"};
        } else if (nomeClubes.equals("Atlético-PR")) {
            nomes = new String[]{"Fernandinho", "Esquivel", "Madson", "Thiago Heleno", "Mycael", "Lucas Halter", "Nico Hernández", "Tomas Machado", "Geuvânio", "Godín", "Pedro Henrique", "Abner", "Antônio Carlos", "Tomás", "Vitor Bueno"};
        } else if (nomeClubes.equals("Corinthians")) {
            nomes = new String[]{"Suárez", "Memphis Depay", "Carrillo", "André Ramalho", "Gómez", "Yuri Alberto", "Otero", "Gustavo Silva", "Raniel", "Hugo Souza", "Robson Bambu", "Cássio", "Raúl Gustavo", "Fabra", "Matheuzinho"};
        } else if (nomeClubes.equals("Botafogo")) {
            nomes = new String[]{"Almada", "Tchê Tchê", "Marçal", "Victor Cley", "Bastos", "Adryelson", "Cuiabano", "Jefferson Freitas", "Danilo", "Gatito Fernández", "Tiquinho Soares", "Alexis Mac Allister", "Afazelier", "Patrick de Paula"};
        } else if (nomeClubes.equals("Vasco da Gama")) {
            nomes = new String[]{"Vegetti", "Rossi", "Raniel", "David", "Estrella", "Léo Matos", "Léo Jacó", "Martinez", "Nestor", "Quatroz", "Pablo Fernández", "Maicon", "Ortiz", "Pauleta", "Peixoto"};
        } else if (nomeClubes.equals("Cruzeiro")) {
            nomes = new String[]{"Barreal", "Matheus Pereira", "Villalba", "Rafael Silva", "Zé Ivaldo", "Edson", "Álvaro Barral", "Matheus Jussa", "Kaio Jorge", "Ariel Cabral", "Marlon", "Guedes", "Stênio", "Laurindo"};
        } else if (nomeClubes.equals("Internacional")) {
            nomes = new String[]{"Yuri Alberto", "Alemão", "Taison", "Enner Valencia", "Boschilia", "Meza", "Néstor", "Bustos", "Vicentini", "Renzo Saravia", "Guerrero", "Lucão", "Lucas Ribeiro", "Joaquín"};
        } else if (nomeClubes.equals("Grêmio")) {
            nomes = new String[]{"Suárez", "Arezo", "Cristaldo", "Edenílson", "Carballo", "Nathan", "Kannemann", "Geromel", "Villasanti", "Nández", "João Pedro", "Rochet", "Marés", "Maidana"};
        } else if (nomeClubes.equals("Fluminense")) {
            nomes = new String[]{"Cano", "Germán", "Arias", "Kauã Elias", "Ganso", "Bornay", "Thiago Silva", "Nino", "Diogo Barbosa", "Marcelo", "Nonato", "Samuel Xavier", "Panenka", "Felipe Melo"};
        } else if (nomeClubes.equals("Bahia")) {
            nomes = new String[]{"Ricardinho", "Biel", "Rafael Ratão", "Cauly", "Luiz Otávio", "Gabriel Xavier", "Rezende", "Jemerson", "Germán Conti", "Santiago Arias", "Gilberto", "Sandoval", "David Duarte"};
        } else if (nomeClubes.equals("Vitória")) {
            nomes = new String[]{"Osvaldo", "Machado", "Chiquinho", "Léo Bonatini", "Bruno Miranda", "Camutanga", "Raúl", "Rezende", "Ruan Ribeiro", "Wagner Leonardo", "Alex Muralha", "Iago", "Patric"};
        } else if (nomeClubes.equals("Bragantino")) {
            nomes = new String[]{"Helinho", "Ytalo", "Sorriso", "Juninho", "Thiago Brito", "Fabricio", "Crespan", "Nattan", "Luan Cândido", "Realpe", "Cleiton", "Nathan", "Luis Miranda"};
        } else if (nomeClubes.equals("RB Bragantino")) {
            nomes = new String[]{"Helinho", "Ytalo", "Sorriso", "Juninho", "Thiago Brito", "Fabricio", "Crespan", "Nattan", "Luan Cândido", "Realpe", "Cleiton", "Nathan", "Luis Miranda"};
        } else if (nomeClubes.equals("Chapecoense")) {
            nomes = new String[]{"Ronei", "Tiago Volpi", "Lumenis", "Felipe Caravolante", "Danilson", "Figueiredo", "Neris", "João Pedro", "Héctor Zumárraga", "Léo Mota", "Pietro Alves"};
        } else if (nomeClubes.equals("Mirassol")) {
            nomes = new String[]{"Danielzinho", "Felipe Ferreira", "Isidro Pitta", "Yuriel Celi", "Negueba", "Lucas Lourenço", "Appelt", "Fabricio", "Joaquín", "Éder Ferreira", "Rafael Santos"};
        } else if (nomeClubes.equals("Coritiba")) {
            nomes = new String[]{"Mattheus", "Robbie", "Vinicius Teotônio", "Roscaldo", "Jonatan Cristo", "William", "Yan Couto", "Egídio", "Godinho", "Reggie Cannon", "Ferreira"};
        } else if (nomeClubes.equals("Santos")) {
            nomes = new String[]{"Miguelito", "Guilherme", "Bruninho", "Soteldo", "Vitor José", "Rincón", "Ayrton Lucas", "Gil", "Bauermann", "Peixoto", "Léo Baptistão"};
        } else {
            for (int i = 1; i <= 23; i++) {
                nomes = java.util.Arrays.copyOf(nomes, nomes.length + 1);
                nomes[nomes.length - 1] = "Jogador " + clube.nome.substring(0, Math.min(3, clube.nome.length())).toUpperCase() + " " + i;
            }
        }
        
        for (String nome : nomes) {
            int habilidade = 60 + new java.util.Random().nextInt(30);
            String posicao = escolherPosicaoAleatoria();
            int idade = 18 + new java.util.Random().nextInt(20);
            Jogador j = new Jogador(nome, habilidade, posicao, idade);
            clube.jogadores.add(j);
        }
    }
    
    String escolherPosicaoAleatoria() {
        String[] posicoes = {"GK", "LB", "CB", "RB", "LM", "CM", "RM", "CF", "ST"};
        return posicoes[new java.util.Random().nextInt(posicoes.length)];
    }

    // ===== SALVAR / CARREGAR POR SLOTS =====
    void escolherSaveParaSalvar() {
        String[] options = {"Slot 1", "Slot 2", "Slot 3", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this, "Escolha um slot para salvar:", "Salvar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (sel >= 0 && sel <= 2) {
            String filename = "save_slot_" + (sel + 1) + ".lfoot";
            salvarEmArquivo(filename);
        }
    }

    void escolherSaveParaCarregar() {
        String[] options = {"Slot 1", "Slot 2", "Slot 3", "Cancelar"};
        int sel = JOptionPane.showOptionDialog(this, "Escolha um slot para carregar:", "Carregar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (sel >= 0 && sel <= 2) {
            String filename = "save_slot_" + (sel + 1) + ".lfoot";
            File f = new File(filename);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "Save não encontrado: " + filename);
                return;
            }
            carregarArquivo(filename);
        }
    }

    void salvarEmArquivo(String filename) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
            oos.writeObject(clubes);
            oos.writeInt(rodada);
            int idx = clubes.indexOf(clubeJogador);
            if (idx < 0) idx = 0;
            oos.writeInt(idx);
            oos.writeObject(brasil);
            oos.writeObject(libertadores);
            oos.close();
            JOptionPane.showMessageDialog(this, "Jogo salvo em: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    void carregarArquivo(String filename) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
            clubes = (ArrayList<Clube>) ois.readObject();
            rodada = ois.readInt();
            int idx = ois.readInt();
            if (idx >= 0 && idx < clubes.size()) clubeJogador = clubes.get(idx);
            else if (!clubes.isEmpty()) clubeJogador = clubes.get(0);
            brasil = (CopaDoB) ois.readObject();
            libertadores = (CopaLibertadores) ois.readObject();
            ois.close();
            telaPrincipal();
            JOptionPane.showMessageDialog(this, "Save carregado: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar: " + e.getMessage());
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        new LegendFoot();
    }

    // ===== CLASSES INTERNAS =====
    static class Clube implements Serializable {
        String nome;
        String serie;
        String estadio;
        String treinador;
        int pontos = 0;
        int vitorias = 0;
        int empates = 0;
        int derrotas = 0;
        int dinheiro = 5000000;
        int debito = 0;
        int confiancaTorcida = 100; // 0-100
        int confiancaDiretoria = 100; // 0-100
        ArrayList<Jogador> jogadores = new ArrayList<>();
        ArrayList<Jogador> jogadoresEscalados = new ArrayList<>();
        int golsFeitos = 0, golsSofridos = 0;
        boolean emLibertadores = false;
        String formacao = "4-3-3";
        String marcacao = "Zona";
        String ataqueLocal = "Balanced";
        String estiloJogo = "Equilibrado";
        int velocidadeSimulacao = 50;
        int rodadaAtual = 0;
        ArrayList<Integer> jogosCasa = new ArrayList<>();
        ArrayList<Integer> jogosForaCriados = new ArrayList<>();
        int ultimas5resultados[] = {1,1,1,1,1}; // 1 vitória, 0 empate, -1 derrota

        Clube(String nome, String estadio, String treinador, String serie) {
            this.nome = nome;
            this.estadio = estadio;
            this.treinador = treinador;
            this.serie = serie;
            criarElenco();
            criarCalendario();
        }

        void atualizarConfianca(int resultado) {
            // resultado: 1 vitória, 0 empate, -1 derrota
            // Última posição das últimas 5 partidas
            for (int i = 0; i < 4; i++) {
                ultimas5resultados[i] = ultimas5resultados[i+1];
            }
            ultimas5resultados[4] = resultado;
            
            // Calcular média das últimas 5 partidas
            int soma = 0;
            for (int r : ultimas5resultados) soma += r;
            
            if (soma > 0) {
                confiancaTorcida = Math.min(100, confiancaTorcida + 3);
                confiancaDiretoria = Math.min(100, confiancaDiretoria + 2);
            } else if (soma < 0) {
                confiancaTorcida = Math.max(0, confiancaTorcida - 5);
                confiancaDiretoria = Math.max(0, confiancaDiretoria - 3);
            }
            
            // Dívida afeta diretoria
            if (debito > 0) {
                confiancaDiretoria = Math.max(0, confiancaDiretoria - 1);
            }
            if (dinheiro > 5000000) {
                confiancaDiretoria = Math.min(100, confiancaDiretoria + 1);
            }
        }

        void criarElenco() {
            String[] nomasPrimeiros = {
                "Alisson", "Ederson", "Lucas", "Gabriel", "Vinícius", "Rodrygo", "Neymar", "Paquetá",
                "Thiago Silva", "Marquinhos", "Talisca", "Antony", "Bruno Guimarães", "Casemiro",
                "Fred", "André", "Sergio Navarro", "Roberto", "Diego", "Cristiano"
            };
            
            String[] nomasUltimos = {
                "Silva", "Santos", "Oliveira", "Ferreira", "Souza", "Alves", "Pereira", "Costa",
                "Rocha", "Martins", "Lima", "Ribeiro", "Dias", "Carvalho", "Gomes", "Mendes",
                "Barbosa", "Machado", "Teixeira", "Lopes"
            };

            String[] posicoes = {"GR", "LD", "LE", "ZC", "ZC", "MC", "MC", "MA", "MA", "ATA", "ATA", 
                                 "GR", "LD", "LE", "ZC", "MC", "MA", "ATA", "ATA", "MC", "ZC", "LD", "MA"};

            Random r = new Random();
            for (int i = 0; i < 23; i++) {
                String nome = nomasPrimeiros[r.nextInt(nomasPrimeiros.length)] + " " + 
                             nomasUltimos[r.nextInt(nomasUltimos.length)];
                int habilidade = 40 + r.nextInt(60);
                int idade = 18 + r.nextInt(20);
                Jogador j = new Jogador(nome, habilidade, posicoes[i], idade);
                jogadores.add(j);
            }

            // Escalar os 11 titulares
            for (int i = 0; i < 11; i++) {
                jogadoresEscalados.add(jogadores.get(i));
            }
        }

        void criarCalendario() {
            // Criar 19 jogos em casa e 19 jogos fora
            for (int i = 1; i <= 38; i++) {
                if (i <= 19) {
                    jogosCasa.add(i);
                } else {
                    jogosForaCriados.add(i);
                }
            }
        }

        void atualizarEstatisticas(Partida p) {
            if (this.nome.equals(p.mandante.nome)) {
                golsFeitos += p.golsMandante;
                golsSofridos += p.golsVisitante;
                if (p.golsMandante > p.golsVisitante) {
                    vitorias++;
                    pontos += 3;
                    atualizarConfianca(1); // vitória
                } else if (p.golsMandante == p.golsVisitante) {
                    empates++;
                    pontos += 1;
                    atualizarConfianca(0); // empate
                } else {
                    derrotas++;
                    atualizarConfianca(-1); // derrota
                }
            } else if (this.nome.equals(p.visitante.nome)) {
                golsFeitos += p.golsVisitante;
                golsSofridos += p.golsMandante;
                if (p.golsVisitante > p.golsMandante) {
                    vitorias++;
                    pontos += 3;
                    atualizarConfianca(1); // vitória
                } else if (p.golsVisitante == p.golsMandante) {
                    empates++;
                    pontos += 1;
                    atualizarConfianca(0); // empate
                } else {
                    derrotas++;
                    atualizarConfianca(-1); // derrota
                }
            }
        }
    }

    static class Jogador implements Serializable {
        String nome;
        int habilidade;
        String posicao = "Desconhecida"; // GR, ZG, LD, LE, ZC, MC, MA, ATA, etc
        int idade = 25;
        String nacionalidade;
        String caracteristica1;
        String caracteristica2;
        int cansaco = 0;
        boolean lesionado = false;
        int cartaoAmarelo = 0;
        int cartaoVermelho = 0;

        Jogador(String nome, int habilidade) {
            this.nome = nome;
            this.habilidade = habilidade;
            this.idade = 18 + new Random().nextInt(20);
            this.nacionalidade = gerarNacionalidade();
            atribuirCaracteristicas();
        }

        Jogador(String nome, int habilidade, String posicao, int idade) {
            this.nome = nome;
            this.habilidade = habilidade;
            this.posicao = posicao;
            this.idade = idade;
            this.nacionalidade = gerarNacionalidade();
            atribuirCaracteristicas();
        }

        String gerarNacionalidade() {
            String[] nacionalidades = {
                "Brasil", "Argentina", "Uruguai", "Paraguai", "Chile", "Colômbia", "Peru",
                "Equador", "Venezuela", "Bolívia", "Portugal", "Espanha", "França", "Itália",
                "Alemanha", "Holanda", "Bélgica", "Suíça", "Áustria", "Inglaterra", "Irlanda",
                "Escócia", "Gales", "Dinamarca", "Suécia", "Noruega", "Finlândia", "Polônia",
                "Sérvia", "Croácia", "Bósnia", "Romênia", "Grécia", "Turquia", "Japão",
                "Coreia do Sul", "China", "Austrália", "Marrocos", "Nigéria", "Camarões",
                "Senegal", "Costa do Marfim", "Egito", "África do Sul"
            };
            return nacionalidades[new Random().nextInt(nacionalidades.length)];
        }

        void atribuirCaracteristicas() {
            String[] caracteristicas = {
                "Rápido", "Forte", "Inteligente", "Bom cabeceio", "Passe preciso",
                "Chute potente", "Drible apurado", "Recuada curta", "Visão de jogo",
                "Liderança", "Versatilidade", "Compostura", "Antecipação", "Marcação",
                "Cruzamento", "Finalização", "Elasticidade", "Aéreo", "Velocidade",
                "Esquerdo", "Canhoto", "Ambidestro", "Agressividade", "Criatividade"
            };

            Random r = new Random();
            this.caracteristica1 = caracteristicas[r.nextInt(caracteristicas.length)];
            String car2 = caracteristicas[r.nextInt(caracteristicas.length)];
            while (car2.equals(this.caracteristica1)) {
                car2 = caracteristicas[r.nextInt(caracteristicas.length)];
            }
            this.caracteristica2 = car2;
        }

        int getCansacoAtual(int minutoPartida) {
            return (cansaco + (idade - 18) * 2) + (minutoPartida / 10);
        }
    }

    static class Partida implements Serializable {
        Clube mandante;
        Clube visitante;
        int golsMandante = 0;
        int golsVisitante = 0;
        int rodada = 0;
        int posse_mandante = 0;
        int posse_visitante = 0;
        int chutesMandante = 0;
        int chutesVisitante = 0;
        int chutesForaMandante = 0;
        int chutesForaVisitante = 0;
        int escarniomMandante = 0;
        int escarnosVisitante = 0;
        int[] notasMandante;
        int[] notasVisitante;

        Partida(Clube mandante, Clube visitante) {
            this.mandante = mandante;
            this.visitante = visitante;
            this.notasMandante = new int[11];
            this.notasVisitante = new int[11];
            Arrays.fill(notasMandante, 65);
            Arrays.fill(notasVisitante, 65);
        }

        String getResultado() {
            return mandante.nome + " " + golsMandante + " x " + golsVisitante + " " + visitante.nome;
        }
    }

    static class CopaDoB implements Serializable {
        ArrayList<Clube> times;
        ArrayList<Integer>[] chaves;
        int rodada = 1;
        Clube campeao = null;
        Clube viceCampeao = null;
        boolean finalizado = false;
        String ultimoResultado = "";
        Random r = new Random();

        @SuppressWarnings("unchecked")
        CopaDoB(ArrayList<Clube> clubesList) {
            times = new ArrayList<>(clubesList);
            Collections.shuffle(times);
            chaves = new ArrayList[128];
            for (int i = 0; i < 128; i++) {
                chaves[i] = new ArrayList<>();
                chaves[i].add(i);
            }
        }

        void jogarPartida() {
            if (finalizado) return;

            ArrayList<Integer>[] novasChaves = new ArrayList[chaves.length / 2];
            for (int i = 0; i < chaves.length; i += 2) {
                if (chaves[i].isEmpty() || chaves[i + 1].isEmpty()) continue;

                int idx1 = chaves[i].get(0);
                int idx2 = chaves[i + 1].get(0);
                Clube t1 = times.get(idx1);
                Clube t2 = times.get(idx2);

                int gols1_ida = r.nextInt(5);
                int gols2_ida = r.nextInt(5);
                int gols1_volta = r.nextInt(5);
                int gols2_volta = r.nextInt(5);

                int total1 = gols1_ida + gols1_volta;
                int total2 = gols2_ida + gols2_volta;

                Clube vencedor;
                if (total1 > total2) {
                    vencedor = t1;
                } else if (total2 > total1) {
                    vencedor = t2;
                } else {
                    vencedor = r.nextBoolean() ? t1 : t2;
                }

                ultimoResultado = t1.nome + " vs " + t2.nome + "\n" +
                        "IDA: " + gols1_ida + " x " + gols2_ida + "\n" +
                        "VOLTA: " + gols1_volta + " x " + gols2_volta + "\n" +
                        "Agregado: " + total1 + " x " + total2 + "\n" +
                        "Classificado: " + vencedor.nome;

                int vencedorIdx = times.indexOf(vencedor);
                if (novasChaves[i / 2] == null) novasChaves[i / 2] = new ArrayList<>();
                novasChaves[i / 2].add(vencedorIdx);
            }

            chaves = novasChaves;
            rodada++;

            int timesRestantes = 0;
            for (ArrayList<Integer> c : chaves) {
                if (c != null && !c.isEmpty()) timesRestantes++;
            }

            if (timesRestantes == 2) {
                finalizarCopa();
            }
        }

        void finalizarCopa() {
            finalizado = true;
            int idx1 = -1, idx2 = -1;
            for (ArrayList<Integer> c : chaves) {
                if (c != null && !c.isEmpty()) {
                    if (idx1 == -1) idx1 = c.get(0);
                    else idx2 = c.get(0);
                }
            }

            campeao = times.get(idx1);
            viceCampeao = times.get(idx2);
            campeao.emLibertadores = true;
            viceCampeao.emLibertadores = true;
        }

        String getNomeRodada() {
            if (rodada == 1) return "128avos";
            if (rodada == 2) return "64avos";
            if (rodada == 3) return "32avos";
            if (rodada == 4) return "16avos";
            if (rodada == 5) return "Oitavas";
            if (rodada == 6) return "Quartas";
            if (rodada == 7) return "Semifinal";
            return "Final";
        }

        String getChaveamento() {
            StringBuilder sb = new StringBuilder("Chaveamento - " + getNomeRodada() + "\n\n");
            int contador = 1;
            for (ArrayList<Integer> c : chaves) {
                if (c != null && !c.isEmpty()) {
                    sb.append(contador++).append(". ").append(times.get(c.get(0)).nome).append("\n");
                }
            }
            return sb.toString();
        }

        String getResultadoUltimaPartida() {
            return ultimoResultado;
        }
    }

    static class CopaLibertadores implements Serializable {
        ArrayList<Clube> classificados = new ArrayList<>();

        void adicionarClube(Clube c) {
            if (!classificados.contains(c)) {
                classificados.add(c);
            }
        }

        String getClassificados() {
            StringBuilder sb = new StringBuilder("Times na Libertadores:\n\n");
            for (Clube c : classificados) {
                sb.append(c.nome).append("\n");
            }
            return sb.toString();
        }
    }

    // ===== SUPERCOPA DO BRASIL =====
    static class SupercopaBrasil implements Serializable {
        Clube campeaoBrasileiro;
        Clube campeaoCopa;
        int golsBrasileiro = 0;
        int golsCopa = 0;
        boolean finalizado = false;
        String resultado = "";

        SupercopaBrasil(Clube brasileirao, Clube copa) {
            this.campeaoBrasileiro = brasileirao;
            this.campeaoCopa = copa;
        }

        void simularPartida() {
            if (campeaoBrasileiro == null || campeaoCopa == null) {
                resultado = "Faltam campeões para disputar a Supercopa!";
                return;
            }

            Random r = new Random();
            golsBrasileiro = r.nextInt(5);
            golsCopa = r.nextInt(5);

            if (golsBrasileiro > golsCopa) {
                resultado = campeaoBrasileiro.nome + " " + golsBrasileiro + " x " + golsCopa + " " + campeaoCopa.nome +
                        "\n\n🏆 CAMPEÃO: " + campeaoBrasileiro.nome;
            } else if (golsCopa > golsBrasileiro) {
                resultado = campeaoBrasileiro.nome + " " + golsBrasileiro + " x " + golsCopa + " " + campeaoCopa.nome +
                        "\n\n🏆 CAMPEÃO: " + campeaoCopa.nome;
            } else {
                resultado = campeaoBrasileiro.nome + " " + golsBrasileiro + " x " + golsCopa + " " + campeaoCopa.nome +
                        "\n\n⚖️ EMPATE - Será decidido nos pênaltis!";
            }

            finalizado = true;
        }

        String getResultado() {
            return resultado;
        }
    }

    // ===== SIMULADOR DE PARTIDA =====
    static class SimuladorPartida implements Serializable {
        Partida partida;
        ArrayList<EventoPartida> eventos = new ArrayList<>();
        int minutoAtual = 0;
        int golsMandante = 0;
        int golsVisitante = 0;
        int posse_mandante = 0;
        int posse_visitante = 0;
        int chutesMandante = 0;
        int chutesVisitante = 0;
        int chutesForaMandante = 0;
        int chutesForaVisitante = 0;
        int escarniomMandante = 0;
        int escarnosVisitante = 0;
        int desarmesMandante = 0;
        int desarmesVisitante = 0;

        SimuladorPartida(Partida p) {
            this.partida = p;
        }

        void simularPartida() {
            Random r = new Random();
            
            for (minutoAtual = 0; minutoAtual <= 90; minutoAtual++) {
                if (minutoAtual == 45) {
                    evento(new EventoPartida(45, "INTERVALO", ""));
                }

                if (r.nextInt(100) < 8) {
                    simularGol(r);
                }
                
                if (r.nextInt(100) < 3) {
                    int tipo = r.nextInt(3);
                    String time = r.nextBoolean() ? partida.mandante.nome : partida.visitante.nome;
                    if (tipo == 0) {
                        evento(new EventoPartida(minutoAtual, "CARTÃO AMARELO", time));
                    } else if (tipo == 1) {
                        evento(new EventoPartida(minutoAtual, "CARTÃO VERMELHO", time));
                    } else {
                        evento(new EventoPartida(minutoAtual, "LESÃO", time));
                    }
                }

                if (r.nextInt(100) < 20) {
                    if (r.nextBoolean()) {
                        chutesMandante++;
                        if (r.nextInt(100) < 40) chutesForaMandante++;
                    } else {
                        chutesVisitante++;
                        if (r.nextInt(100) < 40) chutesForaVisitante++;
                    }
                }

                if (r.nextInt(100) < 5) {
                    if (r.nextBoolean()) escarniomMandante++;
                    else escarnosVisitante++;
                }

                if (r.nextInt(100) < 20) {
                    if (r.nextBoolean()) desarmesMandante++;
                    else desarmesVisitante++;
                }

                if (r.nextBoolean()) posse_mandante++;
                else posse_visitante++;
            }

            partida.golsMandante = golsMandante;
            partida.golsVisitante = golsVisitante;
            partida.posse_mandante = posse_mandante;
            partida.posse_visitante = posse_visitante;
            partida.chutesMandante = chutesMandante;
            partida.chutesVisitante = chutesVisitante;
            partida.chutesForaMandante = chutesForaMandante;
            partida.chutesForaVisitante = chutesForaVisitante;
            partida.escarniomMandante = escarniomMandante;
            partida.escarnosVisitante = escarnosVisitante;
        }

        void simularGol(Random r) {
            if (r.nextBoolean()) {
                golsMandante++;
                int jogador = r.nextInt(Math.min(11, partida.mandante.jogadoresEscalados.size()));
                int assistidor = r.nextInt(Math.min(11, partida.mandante.jogadoresEscalados.size()));
                while (assistidor == jogador && partida.mandante.jogadoresEscalados.size() > 1) {
                    assistidor = r.nextInt(Math.min(11, partida.mandante.jogadoresEscalados.size()));
                }
                evento(new EventoPartida(minutoAtual, "GOL!", 
                    partida.mandante.nome + " - " + 
                    partida.mandante.jogadoresEscalados.get(jogador).nome + 
                    " (Assistência: " + partida.mandante.jogadoresEscalados.get(assistidor).nome + ")"));
            } else {
                golsVisitante++;
                int jogador = r.nextInt(Math.min(11, partida.visitante.jogadoresEscalados.size()));
                int assistidor = r.nextInt(Math.min(11, partida.visitante.jogadoresEscalados.size()));
                while (assistidor == jogador && partida.visitante.jogadoresEscalados.size() > 1) {
                    assistidor = r.nextInt(Math.min(11, partida.visitante.jogadoresEscalados.size()));
                }
                evento(new EventoPartida(minutoAtual, "GOL!", 
                    partida.visitante.nome + " - " + 
                    partida.visitante.jogadoresEscalados.get(jogador).nome + 
                    " (Assistência: " + partida.visitante.jogadoresEscalados.get(assistidor).nome + ")"));
            }
        }

        void evento(EventoPartida e) {
            eventos.add(e);
        }

        static class EventoPartida implements Serializable {
            int minuto;
            String tipo;
            String descricao;

            EventoPartida(int minuto, String tipo, String descricao) {
                this.minuto = minuto;
                this.tipo = tipo;
                this.descricao = descricao;
            }
        }
    }
}
