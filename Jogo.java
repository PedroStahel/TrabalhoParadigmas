import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Jogo {
    private static final int MAX_CICLOS = 25;

    // atributos
    private Tabuleiro tabuleiro;
    private JogadorSimples p1;
    private JogadorSuporte p2;
    private List<Jogador> jogadores;
    private Scanner scanner;
    private int ciclo;
    private boolean fonteEncontrada;
    private String vencedor;

    // construtor
    public Jogo() {
        this.tabuleiro = new Tabuleiro();
        Posicao centro = this.tabuleiro.setorCentral().getPosicao();
        this.p1 = new JogadorSimples(centro);
        this.p2 = new JogadorSuporte(centro);
        this.jogadores = new ArrayList<Jogador>();
        this.jogadores.add(p1);
        this.jogadores.add(p2);
        this.scanner = new Scanner(System.in);
        this.ciclo = 1;
        this.fonteEncontrada = false;
        this.vencedor = null;
    }

    // Método de controle do jogo, mantém o laço dos turnos até o jogo acabar
    public void jogar() {
        while ( !this.jogoAcabou() ){
            System.out.println("===== Ciclo " + this.ciclo + " =====");
            this.turnoJogador(p1, "P1");
            if ( !this.jogoAcabou() )
                this.turnoJogador(p2,"P2");
            if ( !this.jogoAcabou() )
                this.turnoInimigos();
            this.ciclo++;
        } 
        this.mostrarResultado();
    }

    // Método para garantir que em respostas de sim ou nao, o usuario responda S ou N
    // Retorna true para S e false para N
    private boolean perguntarSimNao(String pergunta) {
        String resposta = "";
        while ( !resposta.equalsIgnoreCase("S") && !resposta.equalsIgnoreCase("N") ) {
            System.out.print(pergunta);
            resposta = scanner.nextLine().trim();

            if ( !resposta.equalsIgnoreCase("S") && !resposta.equalsIgnoreCase("N") ) {
                System.out.println("Opção inválida. Digite S ou N.");
            }

        }

        if (resposta.equalsIgnoreCase("S"))
            return true;
        return false;
    }

    // Método para ler a direção digitada pelo usuário, que garante que seja N/S/L/O com o try catch e o método daLetra()
    private Direcao lerDirecao() {
        Direcao direcao = null;

        while ( direcao == null ) {
            System.out.print("Direção (N/S/L/O): ");

            try {
                direcao = Direcao.daLetra( this.scanner.nextLine() );
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage() );
            }
        }

        return direcao;
    }

    // Método de escolha da ação do usuário 
    // Verifica as possibilidades de ação do jogador e mostra apenas as disponiveis
    // Retorna a opção escolhida pelo usuário
    private int escolherAcao(Jogador jogador, Setor setor, int acao) {
        boolean jogadorPodeCurar = jogador instanceof JogadorSuporte && acao == 1;
        while ( true ) { 
            // mostra só as opções disponíveis
            System.out.println("0 - Passar");
            if ( setor.possuiInimigosVivos() )   
                System.out.println("1 - Atacar");
            if ( setor.permiteProcurar() ) 
                System.out.println("2 - Procurar");
            if ( jogadorPodeCurar ) 
                System.out.println("3 - Curar");
            
            System.out.print("Escolha: ");

            String resposta = scanner.nextLine().trim();

            if ( resposta.equals("1") && setor.possuiInimigosVivos() )   
                return 1;

            if ( resposta.equals("2") && setor.permiteProcurar() )     
                return 2;

            if ( resposta.equals("3") && jogadorPodeCurar )
                return 3;

            if ( resposta.equals("0") )                 
                return 0;

            System.out.println("Opção inválida ou indisponível.");
        }
    }

    // Método executa o método procurar() e mostra o resultado da procura
    private void acaoProcurar(Jogador jogador, String nome, Setor setor) {
        int dado = jogador.procurar(setor);     

        if ( dado == -1 )
            System.out.println("Não é possível procurar aqui.");
        else if ( dado <= 3 )
            System.out.println(nome + " procurou e não encontrou nada.");
        else if ( dado == 4 )
            System.out.println(nome + " encontrou +1 de DEF! Agora tem " + jogador.getDefesa());
        else if ( dado == 5 )
            System.out.println(nome + " encontrou +2 de DEF! Agora tem " + jogador.getDefesa());
        else
            System.out.println(nome + " encontrou um antivírus! Todos os inimigos do setor perdem 1 de DEF");
    }

    // Método mostra os inimigos disponiveis para atacar e garante que o usuário faça uma escolha válida
    // Executa o método atacar() e mostra o resultado do ataque
    private void acaoAtacar(Jogador jogador, String nome, Setor setor) {
        List<Inimigo> vivos = new ArrayList<>();

        for ( Inimigo inimigo : setor.getInimigos() ) { // preenche a lista de inimigos vivos
            if ( inimigo.estaVivo() )
                vivos.add(inimigo);
        }

        for ( int i = 0; i < vivos.size(); i++ ) { // mostra inimigos disponíveis para a ação
            Inimigo inimigo = vivos.get(i);
            System.out.println( (i + 1) + " - Inimigo " + inimigo.getAtaque() + "/" + inimigo.getDefesa() );
        }

        int escolha = 0;
        // while garante a escolha válida por meio do try catch
        while ( true ) {
            System.out.print("Qual inimigo atacar? ");
            try {
                escolha = Integer.parseInt( scanner.nextLine().trim() );
                if ( escolha >= 1 && escolha <= vivos.size() )
                    break;                              // se é válido, sai do while
                System.out.println("Número fora da lista.");
            } catch ( NumberFormatException e ) {       // digitou letra
                System.out.println("Digite um número.");
            }
        }
        Inimigo alvo = vivos.get( escolha - 1 );  
        
        boolean acertou = jogador.atacar(alvo, setor);   

        if ( !acertou )
            System.out.println(nome + " errou o ataque!");   // acontece no setor oculto
        else if ( !alvo.estaVivo() )
            System.out.println(nome + " eliminou o inimigo " + escolha);  
        else
            System.out.println(nome + " acertou o inimigo " + escolha + ". "+ alvo.getDefesa() + " de DEF restante.");  
    }

    // Método verifica se o jogador simples está disponível para cura
    // Garante que o usuário dê uma entrada válida e define o alvo
    // Executa o método usarHabilidade() e mostra quem foi curado e o resultado da cura
    private void acaoCurar(JogadorSuporte suporte, String nome) {
        // o P1 só pode ser curado se estiver vivo E no mesmo setor
        boolean podeCurarP1 = this.p1.estaVivo() && this.p1.getPosicao().equals( suporte.getPosicao() );

        System.out.println("1 - Curar a si mesmo (" + nome + ")");
        if ( podeCurarP1 )
            System.out.println("2 - Curar P1");

        Jogador alvo = null;
        while ( alvo == null ) { 
            System.out.print("Quem curar? ");
            String resposta = scanner.nextLine().trim();

            if ( resposta.equals("1") )
                alvo = suporte;
            else if ( resposta.equals("2") && podeCurarP1 )
                alvo = this.p1;
            else
                System.out.println("Opção inválida.");
        }

        suporte.usarHabilidade(alvo);
        System.out.println(nome + " curou! DEF agora: " + alvo.getDefesa());
    }

    // Método que executa o turno de um jogador
    // Dispõe as ações na ordem: movimento -> ação -> ação
    private void turnoJogador(Jogador jogador, String nome) {
        
        // Jogador morto não tem turno
        if ( !jogador.estaVivo() ) { 
            System.out.printf("Jogador %s foi eliminado!\n", nome);
            return;
        }
        
        // renderiza o tabuleiro
        this.tabuleiro.desenhar(jogadores); 
        System.out.println("===== Turno do " + nome + " =====");  

        Setor setor = this.tabuleiro.getSetor(jogador.getPosicao());

        // movimento só é permitido se o setor está sem inimigos
        if ( !setor.possuiInimigosVivos() ) { 
            if ( this.perguntarSimNao("Deseja Mover? (S/N): ") ) { 
                boolean moveu = false;
                boolean querTentar = true;

                // laço para garantir o movimento/opção do jogador em caso de uma escolha de direção sem portas
                while (!moveu && querTentar) {
                    Direcao direcao = lerDirecao();
                    if( this.tabuleiro.moverJogador(jogador, direcao) ) {
                        System.out.println("Jogador " + nome + " foi para " + jogador.getPosicao() );

                        this.tabuleiro.desenhar(jogadores);

                        moveu = true;
                        setor = this.tabuleiro.getSetor(jogador.getPosicao());
                        
                        if ( setor.isContemFonte() ) {
                            this.fonteEncontrada = true;
                            this.vencedor = nome;
                            return;
                        }
                    } else {
                        System.out.println("Não há porta nessa direção!");
                        querTentar = perguntarSimNao("Tentar outra direção? (S/N): ");
                    }
                }
            }
        } else 
            System.out.println("Há inimigos vivos no setor, não é possível sair!\n");

        // define as ações possiveis e tomadas
        for ( int acao = 1; acao <= 2; acao++ ) {
            System.out.println("--- Ação " + acao + " de 2 ---");
            Setor atual = this.tabuleiro.getSetor(jogador.getPosicao());

            int opcao = escolherAcao(jogador, atual, acao);

            if ( opcao == 1 )
                acaoAtacar(jogador, nome, atual);
            else if ( opcao == 2 )
                acaoProcurar(jogador, nome, atual);
            else if ( opcao == 3 ) {
                JogadorSuporte suporte = ( JogadorSuporte ) jogador;
                acaoCurar(suporte, nome);
                break; // curar consome duas ações
            } 
            else
                System.out.println(nome + " passou a ação.");
            System.out.println();
        }
    }

    // Método de execução do turno dos inimigos de cada setor dos jogadores
    private void turnoInimigos() {
        System.out.println("===== Turno dos inimigos =====");
        List<Setor> processados = new ArrayList<Setor>();

        for (Jogador j : this.jogadores) {
            // jogador morto não chama inimigo
            if ( !j.estaVivo() )   
                continue;          
                 
            Setor setor = tabuleiro.getSetor(j.getPosicao());
            // esse setor já atacou
            if ( processados.contains(setor) ) 
                continue;   
            processados.add(setor);

            int numInimigo = 0;
            // executa o ataque de cada inimigo do setor
            for (Inimigo inimigo : setor.getInimigos()) {
                if (!inimigo.estaVivo()) 
                    continue;
                numInimigo++;

                // executa o ataque em cada jogador, se estiverem no mesmo setor
                for (Jogador alvo : this.jogadores) {
                    String nome;
                    if (alvo == p1)
                        nome = "P1";
                    else
                        nome = "P2";

                    // o alvo precisa estar vivo e no mesmo setor
                    if (alvo.estaVivo() && alvo.getPosicao().equals(setor.getPosicao())) {
                        boolean acertou = inimigo.atacar(alvo, setor);

                        if (!acertou) 
                            System.out.println("Inimigo " + numInimigo + " errou o ataque ao " + nome + " no setor " + alvo.getPosicao());
                        else if ( !alvo.estaVivo() ) 
                            System.out.println("Inimigo " + numInimigo + " eliminou o jogador " + nome + " no setor " + alvo.getPosicao());
                        else
                            System.out.println("Inimigo " + numInimigo + " acertou o ataque em " + nome + " no setor " + alvo.getPosicao() + ". DEF restante: " + alvo.getDefesa());
                    }
                }
            }
        }
    }

    // Método retorna true se uma das condições de fim de jogo forem satisfeitas, e false caso contrário
    private boolean jogoAcabou() {
        return ( this.fonteEncontrada || this.ciclo > MAX_CICLOS || (!this.p1.estaVivo() && !this.p2.estaVivo()) );
    }

    // Método mostra o resultado do jogo 
    private void mostrarResultado() {
        if ( this.fonteEncontrada )
            System.out.println(this.vencedor + " encontrou a fonte... Os jogadores venceram!");
        else if ( !this.p1.estaVivo() && !this.p2.estaVivo() )
            System.out.println("Os jogadores morreram... Os inimigos venceram!");
        else if ( this.ciclo > MAX_CICLOS )
            System.out.println("Tempo esgotado... Os inimigos venceram!");
    }
}